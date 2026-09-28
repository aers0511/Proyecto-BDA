package persistenciaDAO;

import entidad.CuentaPromotora;
import persistencia.ICuentaPromotoraDAO;
import persistenciaUtil.ConexionBD;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CuentaPromotoraDAO implements ICuentaPromotoraDAO {

    @Override
    public CuentaPromotora insertar(CuentaPromotora cuenta) throws SQLException {
        String sql = "INSERT INTO CUENTA_PROMOTORA (banco, numeroCuenta, saldo, idPromotora) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cuenta.getBanco());
            ps.setString(2, cuenta.getNumeroCuenta());
            ps.setBigDecimal(3, cuenta.getSaldo());
            ps.setInt(4, cuenta.getIdPromotora());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    cuenta.setIdCuenta(rs.getInt(1));
                }
            }
        }
        return cuenta;
    }

    @Override
    public CuentaPromotora obtenerPorId(int idCuenta) throws SQLException {
        String sql = "SELECT idCuenta, banco, numeroCuenta, saldo, idPromotora FROM CUENTA_PROMOTORA WHERE idCuenta = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCuenta);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<CuentaPromotora> obtenerPorPromotora(int idPromotora) throws SQLException {
        List<CuentaPromotora> lista = new ArrayList<>();
        String sql = "SELECT idCuenta, banco, numeroCuenta, saldo, idPromotora FROM CUENTA_PROMOTORA WHERE idPromotora = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPromotora);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearResultSet(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public boolean actualizarSaldo(int idCuenta, BigDecimal nuevoSaldo) throws SQLException {
        String sql = "UPDATE CUENTA_PROMOTORA SET saldo = ? WHERE idCuenta = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, nuevoSaldo);
            ps.setInt(2, idCuenta);

            return ps.executeUpdate() > 0;
        }
    }

    private CuentaPromotora mapearResultSet(ResultSet rs) throws SQLException {
        return new CuentaPromotora(
            rs.getInt("idCuenta"),
            rs.getString("banco"),
            rs.getString("numeroCuenta"),
            rs.getBigDecimal("saldo"),
            rs.getInt("idPromotora")
        );
    }
}