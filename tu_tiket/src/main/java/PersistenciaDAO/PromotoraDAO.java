package persistenciaDAO;

import entidad.Promotora;
import persistencia.IPromotoraDAO;
import persistenciaUtil.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PromotoraDAO implements IPromotoraDAO {

    @Override
    public Promotora insertar(Promotora promotora) throws SQLException {
        String sql = "INSERT INTO PROMOTORA (nombreComercial, calle, numero, colonia, ciudad, estado) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, promotora.getNombreComercial());
            ps.setString(2, promotora.getCalle());
            ps.setString(3, promotora.getNumero());
            ps.setString(4, promotora.getColonia());
            ps.setString(5, promotora.getCiudad());
            ps.setString(6, promotora.getEstado());
            
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    promotora.setIdPromotora(rs.getInt(1));
                }
            }
        }
        return promotora;
    }

    @Override
    public Promotora obtenerPorId(int idPromotora) throws SQLException {
        String sql = "SELECT idPromotora, nombreComercial, calle, numero, colonia, ciudad, estado FROM PROMOTORA WHERE idPromotora = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPromotora);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Promotora> obtenerTodos() throws SQLException {
        List<Promotora> lista = new ArrayList<>();
        String sql = "SELECT idPromotora, nombreComercial, calle, numero, colonia, ciudad, estado FROM PROMOTORA";
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
    public boolean actualizar(Promotora promotora) throws SQLException {
        String sql = "UPDATE PROMOTORA SET nombreComercial = ?, calle = ?, numero = ?, colonia = ?, ciudad = ?, estado = ? WHERE idPromotora = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, promotora.getNombreComercial());
            ps.setString(2, promotora.getCalle());
            ps.setString(3, promotora.getNumero());
            ps.setString(4, promotora.getColonia());
            ps.setString(5, promotora.getCiudad());
            ps.setString(6, promotora.getEstado());
            ps.setInt(7, promotora.getIdPromotora());
            
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean eliminar(int idPromotora) throws SQLException {
        String sql = "DELETE FROM PROMOTORA WHERE idPromotora = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPromotora);
            return ps.executeUpdate() > 0;
        }
    }

    private Promotora mapearResultSet(ResultSet rs) throws SQLException {
        return new Promotora(
            rs.getInt("idPromotora"),
            rs.getString("nombreComercial"),
            rs.getString("calle"),
            rs.getString("numero"),
            rs.getString("colonia"),
            rs.getString("ciudad"),
            rs.getString("estado")
        );
    }
}