package persistenciaDAO;

import entidad.Cliente;
import persistencia.IClienteDAO;
import persistenciaUtil.ConexionBD;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ClienteDAO implements IClienteDAO {

    @Override
    public Cliente insertar(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO CLIENTE (nombres, apellidoPaterno, apellidoMaterno, fechaNacimiento, usuario, contrasena) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cliente.getNombres());
            ps.setString(2, cliente.getApellidoPaterno());
            ps.setString(3, cliente.getApellidoMaterno());
            ps.setDate(4, cliente.getFechaNacimiento() != null ? Date.valueOf(cliente.getFechaNacimiento()) : null);
            ps.setString(5, cliente.getUsuario());
            ps.setString(6, cliente.getContrasena());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    cliente.setIdCliente(rs.getInt(1));
                }
            }
        }
        return cliente;
    }

    @Override
    public Cliente obtenerPorId(int idCliente) throws SQLException {
        String sql = "SELECT idCliente, nombres, apellidoPaterno, apellidoMaterno, fechaNacimiento, usuario, contrasena FROM CLIENTE WHERE idCliente = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public Cliente obtenerPorUsuario(String usuario) throws SQLException {
        String sql = "SELECT idCliente, nombres, apellidoPaterno, apellidoMaterno, fechaNacimiento, usuario, contrasena FROM CLIENTE WHERE usuario = ?";
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
    public boolean actualizar(Cliente cliente) throws SQLException {
        String sql = "UPDATE CLIENTE SET nombres = ?, apellidoPaterno = ?, apellidoMaterno = ?, fechaNacimiento = ?, usuario = ?, contrasena = ? WHERE idCliente = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cliente.getNombres());
            ps.setString(2, cliente.getApellidoPaterno());
            ps.setString(3, cliente.getApellidoMaterno());
            ps.setDate(4, cliente.getFechaNacimiento() != null ? Date.valueOf(cliente.getFechaNacimiento()) : null);
            ps.setString(5, cliente.getUsuario());
            ps.setString(6, cliente.getContrasena());
            ps.setInt(7, cliente.getIdCliente());

            return ps.executeUpdate() > 0;
        }
    }

    private Cliente mapearResultSet(ResultSet rs) throws SQLException {
        Date fecha = rs.getDate("fechaNacimiento");
        return new Cliente(
            rs.getInt("idCliente"),
            rs.getString("nombres"),
            rs.getString("apellidoPaterno"),
            rs.getString("apellidoMaterno"),
            fecha != null ? fecha.toLocalDate() : null,
            rs.getString("usuario"),
            rs.getString("contrasena")
        );
    }
}