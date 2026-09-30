package com.tutiket.repository.impl;

import com.tutiket.domain.Cliente;
import com.tutiket.repository.ClienteRepository;

import java.sql.*;
import java.util.Optional;

public class JdbcClienteRepository implements ClienteRepository {

    @Override
    public Cliente guardar(Connection conn, Cliente cliente) throws SQLException {
        String sql = "INSERT INTO clientes (nombre, correo, usuario, contrasena, activo) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, cliente.getNombre());
            stmt.setString(2, cliente.getCorreo());
            stmt.setString(3, cliente.getUsuario());
            stmt.setString(4, cliente.getContrasena());
            stmt.setBoolean(5, cliente.getActivo() != null ? cliente.getActivo() : true);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    cliente.setId(rs.getLong(1));
                }
            }
        }
        return cliente;
    }

    @Override
    public Optional<Cliente> buscarPorId(Connection conn, Long id) throws SQLException {
        String sql = "SELECT * FROM clientes WHERE id = ? AND activo = TRUE";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCliente(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Cliente> buscarPorUsuario(Connection conn, String usuario) throws SQLException {
        String sql = "SELECT * FROM clientes WHERE usuario = ? AND activo = TRUE";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCliente(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean existeUsuarioOCorreo(Connection conn, String usuario, String correo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM clientes WHERE usuario = ? OR correo = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario);
            stmt.setString(2, correo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private Cliente mapResultSetToCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setId(rs.getLong("id"));
        cliente.setNombre(rs.getString("nombre"));
        cliente.setCorreo(rs.getString("correo"));
        cliente.setUsuario(rs.getString("usuario"));
        cliente.setContrasena(rs.getString("contrasena"));
        cliente.setActivo(rs.getBoolean("activo"));
        Timestamp ts = rs.getTimestamp("fecha_registro");
        if (ts != null) cliente.setFechaRegistro(ts.toLocalDateTime());
        return cliente;
    }
}