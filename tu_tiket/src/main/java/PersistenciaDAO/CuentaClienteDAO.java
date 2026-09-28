package persistenciaDAO;

import entidad.CuentaCliente;
import persistencia.ICuentaClienteDAO;
import persistenciaUtil.ConexionBD;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CuentaClienteDAO implements ICuentaClienteDAO {

    @Override
    public CuentaCliente insertar(CuentaCliente cuenta) throws SQLException {
        String sql = "INSERT INTO CUENTA_CLIENTE (banco, numeroCuenta, saldo, idCliente) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cuenta.getBanco());
            ps.setString(2, cuenta.getNumeroCuenta());
            ps.setBigDecimal(3, cuenta.getSaldo());
            ps.setInt(4, cuenta.getIdCliente());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    cuenta.setIdCuentaCliente(rs.getInt(1));
                }
            }
        }
        return cuenta;
    }

    @Override
    public CuentaCliente obtenerPorId(int idCuentaCliente) throws SQLException {
        String sql = "SELECT idCuentaCliente, banco, numeroCuenta, saldo, idCliente FROM CUENTA_CLIENTE WHERE idCuentaCliente = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCuentaCliente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<CuentaCliente> obtenerPorCliente(int idCliente) throws SQLException {
        List<CuentaCliente> lista = new ArrayList<>();
        String sql = "SELECT idCuentaCliente, banco, numeroCuenta, saldo, idCliente FROM CUENTA_CLIENTE WHERE idCliente = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearResultSet(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public boolean actualizarSaldo(int idCuentaCliente, BigDecimal nuevoSaldo, Connection conn) throws SQLException {
        String sql = "UPDATE CUENTA_CLIENTE SET saldo = ? WHERE idCuentaCliente = ?";
        boolean conexionPropia = false;
        if (conn == null) {
            conn = ConexionBD.obtenerConexion();
            conexionPropia = true;
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, nuevoSaldo);
            ps.setInt(2, idCuentaCliente);
            return ps.executeUpdate() > 0;
        } finally {
            if (conexionPropia && conn != null) {
                conn.close();
            }
        }
    }

    private CuentaCliente mapearResultSet(ResultSet rs) throws SQLException {
        return new CuentaCliente(
            rs.getInt("idCuentaCliente"),
            rs.getString("banco"),
            rs.getString("numeroCuenta"),
            rs.getBigDecimal("saldo"),
            rs.getInt("idCliente")
        );
    }
}