package persistenciaDAO;

import entidad.Compra;
import persistencia.ICompraDAO;
import persistenciaUtil.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class CompraDAO implements ICompraDAO {

    @Override
    public Compra insertar(Compra compra, Connection conn) throws SQLException {
        String sql = "INSERT INTO COMPRA (fechaHora, precioFinal, estado, idCliente, idCuentaCliente, idBoleto) VALUES (?, ?, ?, ?, ?, ?)";
        boolean conexionPropia = false;
        
        if (conn == null) {
            conn = ConexionBD.obtenerConexion();
            conexionPropia = true;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setTimestamp(1, compra.getFechaHora() != null ? Timestamp.valueOf(compra.getFechaHora()) : Timestamp.valueOf(java.time.LocalDateTime.now()));
            ps.setBigDecimal(2, compra.getPrecioFinal());
            ps.setString(3, compra.getEstado());
            ps.setInt(4, compra.getIdCliente());
            ps.setInt(5, compra.getIdCuentaCliente());
            ps.setInt(6, compra.getIdBoleto());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    compra.setIdCompra(rs.getInt(1));
                }
            }
        } finally {
            if (conexionPropia && conn != null) {
                conn.close();
            }
        }
        return compra;
    }

    @Override
    public Compra obtenerPorId(int idCompra) throws SQLException {
        String sql = "SELECT idCompra, fechaHora, precioFinal, estado, idCliente, idCuentaCliente, idBoleto FROM COMPRA WHERE idCompra = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCompra);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Compra> obtenerPorCliente(int idCliente) throws SQLException {
        List<Compra> lista = new ArrayList<>();
        String sql = "SELECT idCompra, fechaHora, precioFinal, estado, idCliente, idCuentaCliente, idBoleto FROM COMPRA WHERE idCliente = ?";
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
    public boolean actualizarEstado(int idCompra, String estado, Connection conn) throws SQLException {
        String sql = "UPDATE COMPRA SET estado = ? WHERE idCompra = ?";
        boolean conexionPropia = false;

        if (conn == null) {
            conn = ConexionBD.obtenerConexion();
            conexionPropia = true;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, idCompra);
            return ps.executeUpdate() > 0;
        } finally {
            if (conexionPropia && conn != null) {
                conn.close();
            }
        }
    }

    private Compra mapearResultSet(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("fechaHora");
        return new Compra(
            rs.getInt("idCompra"),
            ts != null ? ts.toLocalDateTime() : null,
            rs.getBigDecimal("precioFinal"),
            rs.getString("estado"),
            rs.getInt("idCliente"),
            rs.getInt("idCuentaCliente"),
            rs.getInt("idBoleto")
        );
    }
}