package persistenciaDAO;

import entidad.Administrador;
import persistencia.IAdministradorDAO;
import persistenciaUtil.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AdministradorDAO implements IAdministradorDAO {

    @Override
    public Administrador insertar(Administrador administrador) throws SQLException {
        String sql = "INSERT INTO ADMINISTRADOR (nombre, apellidoPaterno, apellidoMaterno, usuario, contrasena, idPromotora) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, administrador.getNombre());
            ps.setString(2, administrador.getApellidoPaterno());
            ps.setString(3, administrador.getApellidoMaterno());
            ps.setString(4, administrador.getUsuario());
            ps.setString(5, administrador.getContrasena());
            ps.setInt(6, administrador.getIdPromotora());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    administrador.setIdAdministrador(rs.getInt(1));
                }
            }
        }
        return administrador;
    }

    @Override
    public Administrador obtenerPorId(int idAdministrador) throws SQLException {
        String sql = "SELECT idAdministrador, nombre, apellidoPaterno, apellidoMaterno, usuario, contrasena, idPromotora FROM ADMINISTRADOR WHERE idAdministrador = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAdministrador);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public Administrador obtenerPorUsuario(String usuario) throws SQLException {
        String sql = "SELECT idAdministrador, nombre, apellidoPaterno, apellidoMaterno, usuario, contrasena, idPromotora FROM ADMINISTRADOR WHERE usuario = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Administrador> obtenerPorPromotora(int idPromotora) throws SQLException {
        List<Administrador> lista = new ArrayList<>();
        String sql = "SELECT idAdministrador, nombre, apellidoPaterno, apellidoMaterno, usuario, contrasena, idPromotora FROM ADMINISTRADOR WHERE idPromotora = ?";
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
    public boolean actualizar(Administrador administrador) throws SQLException {
        String sql = "UPDATE ADMINISTRADOR SET nombre = ?, apellidoPaterno = ?, apellidoMaterno = ?, usuario = ?, contrasena = ?, idPromotora = ? WHERE idAdministrador = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, administrador.getNombre());
            ps.setString(2, administrador.getApellidoPaterno());
            ps.setString(3, administrador.getApellidoMaterno());
            ps.setString(4, administrador.getUsuario());
            ps.setString(5, administrador.getContrasena());
            ps.setInt(6, administrador.getIdPromotora());
            ps.setInt(7, administrador.getIdAdministrador());

            return ps.executeUpdate() > 0;
        }
    }

    private Administrador mapearResultSet(ResultSet rs) throws SQLException {
        return new Administrador(
            rs.getInt("idAdministrador"),
            rs.getString("nombre"),
            rs.getString("apellidoPaterno"),
            rs.getString("apellidoMaterno"),
            rs.getString("usuario"),
            rs.getString("contrasena"),
            rs.getInt("idPromotora")
        );
    }
}