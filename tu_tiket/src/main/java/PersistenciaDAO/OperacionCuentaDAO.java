package persistenciaDAO;

import entidad.OperacionCuenta;
import persistencia.IOperacionCuentaDAO;
import persistenciaUtil.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OperacionCuentaDAO implements IOperacionCuentaDAO {

    @Override
    public OperacionCuenta insertar(OperacionCuenta operacion, Connection conn) throws SQLException {
        String sql = "INSERT INTO OPERACION_CUENTA (tipoOperacion, monto, fechaHora, saldoAnterior, saldoNuevo, idCuentaCliente) VALUES (?, ?, ?, ?, ?, ?)";
        boolean conexionPropia = false;

        if (conn == null) {
            conn = ConexionBD.obtenerConexion();
            conexionPropia = true;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, operacion.getTipoOperacion());
            ps.setBigDecimal(2, operacion.getMonto());
            ps.setTimestamp(3, operacion.getFechaHora() != null ? Timestamp.valueOf(operacion.getFechaHora()) : Timestamp.valueOf(LocalDateTime.now()));
            ps.setBigDecimal(4, operacion.getSaldoAnterior());
            ps.setBigDecimal(5, operacion.getSaldoNuevo());
            ps.setInt(6, operacion.getIdCuentaCliente());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    operacion.setIdOperacion(rs.getInt(1));
                }
            }
        } finally {
            if (conexionPropia && conn != null) {
                conn.close();
            }
        }
        return operacion;
    }

    @Override
    public List<OperacionCuenta> obtenerPorCuentaCliente(int idCuentaCliente) throws SQLException {
        List<OperacionCuenta> lista = new ArrayList<>();
        String sql = "SELECT idOperacion, tipoOperacion, monto, fechaHora, saldoAnterior, saldoNuevo, idCuentaCliente " +
                     "FROM OPERACION_CUENTA WHERE idCuentaCliente = ? ORDER BY fechaHora DESC";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCuentaCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearResultSet(rs));
                }
            }
        }
        return lista;
    }

    private OperacionCuenta mapearResultSet(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("fechaHora");
        return new OperacionCuenta(
            rs.getInt("idOperacion"),
            rs.getString("tipoOperacion"),
            rs.getBigDecimal("monto"),
            ts != null ? ts.toLocalDateTime() : null,
            rs.getBigDecimal("saldoAnterior"),
            rs.getBigDecimal("saldoNuevo"),
            rs.getInt("idCuentaCliente")
        );
    }
}