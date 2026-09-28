package persistenciaDAO;

import entidad.Evento;
import persistencia.IEventoDAO;
import persistenciaUtil.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EventoDAO implements IEventoDAO {

    @Override
    public Evento insertar(Evento evento) throws SQLException {
        String sql = "INSERT INTO EVENTO (nombreShow, tipoEvento, edadMinima, imagen, cantidadBoletos, precioBoleto, idPromotora) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, evento.getNombreShow());
            ps.setString(2, evento.getTipoEvento());
            ps.setInt(3, evento.getEdadMinima());
            ps.setString(4, evento.getImagen());
            ps.setInt(5, evento.getCantidadBoletos());
            ps.setBigDecimal(6, evento.getPrecioBoleto());
            ps.setInt(7, evento.getIdPromotora());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    evento.setIdEvento(rs.getInt(1));
                }
            }
        }
        return evento;
    }

    @Override
    public Evento obtenerPorId(int idEvento) throws SQLException {
        String sql = "SELECT idEvento, nombreShow, tipoEvento, edadMinima, imagen, cantidadBoletos, precioBoleto, idPromotora FROM EVENTO WHERE idEvento = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEvento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Evento> obtenerTodos() throws SQLException {
        List<Evento> lista = new ArrayList<>();
        String sql = "SELECT idEvento, nombreShow, tipoEvento, edadMinima, imagen, cantidadBoletos, precioBoleto, idPromotora FROM EVENTO";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }
        }
        return lista;
    }

    @Override
    public List<Evento> obtenerPorPromotora(int idPromotora) throws SQLException {
        List<Evento> lista = new ArrayList<>();
        String sql = "SELECT idEvento, nombreShow, tipoEvento, edadMinima, imagen, cantidadBoletos, precioBoleto, idPromotora FROM EVENTO WHERE idPromotora = ?";
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
    public boolean actualizar(Evento evento) throws SQLException {
        String sql = "UPDATE EVENTO SET nombreShow = ?, tipoEvento = ?, edadMinima = ?, imagen = ?, cantidadBoletos = ?, precioBoleto = ?, idPromotora = ? WHERE idEvento = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, evento.getNombreShow());
            ps.setString(2, evento.getTipoEvento());
            ps.setInt(3, evento.getEdadMinima());
            ps.setString(4, evento.getImagen());
            ps.setInt(5, evento.getCantidadBoletos());
            ps.setBigDecimal(6, evento.getPrecioBoleto());
            ps.setInt(7, evento.getIdPromotora());
            ps.setInt(8, evento.getIdEvento());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean eliminar(int idEvento) throws SQLException {
        String sql = "DELETE FROM EVENTO WHERE idEvento = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEvento);
            return ps.executeUpdate() > 0;
        }
    }

    private Evento mapearResultSet(ResultSet rs) throws SQLException {
        return new Evento(
            rs.getInt("idEvento"),
            rs.getString("nombreShow"),
            rs.getString("tipoEvento"),
            rs.getInt("edadMinima"),
            rs.getString("imagen"),
            rs.getInt("cantidadBoletos"),
            rs.getBigDecimal("precioBoleto"),
            rs.getInt("idPromotora")
        );
    }
}