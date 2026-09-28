package persistenciaDAO;

import entidad.Boleto;
import persistencia.IBoletoDAO;
import persistenciaUtil.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BoletoDAO implements IBoletoDAO {

    @Override
    public boolean insertarLote(List<Boleto> boletos) throws SQLException {
        String sql = "INSERT INTO BOLETO (codigoBoleto, precio, estado, idEvento) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            for (Boleto boleto : boletos) {
                ps.setString(1, boleto.getCodigoBoleto());
                ps.setBigDecimal(2, boleto.getPrecio());
                ps.setString(3, boleto.getEstado());
                ps.setInt(4, boleto.getIdEvento());
                ps.addBatch();
            }
            int[] resultados = ps.executeBatch();
            conn.commit();
            return resultados.length == boletos.size();
        }
    }

    @Override
    public Boleto obtenerPorId(int idBoleto) throws SQLException {
        String sql = "SELECT idBoleto, codigoBoleto, precio, estado, idEvento FROM BOLETO WHERE idBoleto = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idBoleto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Boleto> obtenerPorEvento(int idEvento) throws SQLException {
        List<Boleto> lista = new ArrayList<>();
        String sql = "SELECT idBoleto, codigoBoleto, precio, estado, idEvento FROM BOLETO WHERE idEvento = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEvento);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearResultSet(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public List<Boleto> obtenerDisponiblesPorEvento(int idEvento, int cantidad) throws SQLException {
        List<Boleto> lista = new ArrayList<>();
        String sql = "SELECT idBoleto, codigoBoleto, precio, estado, idEvento FROM BOLETO WHERE idEvento = ? AND estado = 'Disponible' LIMIT ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEvento);
            ps.setInt(2, cantidad);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearResultSet(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public boolean actualizarEstado(int idBoleto, String estado, Connection conn) throws SQLException {
        String sql = "UPDATE BOLETO SET estado = ? WHERE idBoleto = ?";
        boolean conexionPropia = false;
        if (conn == null) {
            conn = ConexionBD.obtenerConexion();
            conexionPropia = true;
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, idBoleto);
            return ps.executeUpdate() > 0;
        } finally {
            if (conexionPropia && conn != null) {
                conn.close();
            }
        }
    }

    private Boleto mapearResultSet(ResultSet rs) throws SQLException {
        return new Boleto(
            rs.getInt("idBoleto"),
            rs.getString("codigoBoleto"),
            rs.getBigDecimal("precio"),
            rs.getString("estado"),
            rs.getInt("idEvento")
        );
    }
}