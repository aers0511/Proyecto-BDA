package com.tutiket.repository.impl;

import com.tutiket.domain.Administrador;
import com.tutiket.repository.AdministradorRepository;

import java.sql.*;
import java.util.Optional;

public class JdbcAdministradorRepository implements AdministradorRepository {

    @Override
    public Administrador guardar(Connection conn, Administrador admin) throws SQLException {
        String sql = "INSERT INTO administradores (nombre, correo, usuario, contrasena, promotora_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, admin.getNombre());
            stmt.setString(2, admin.getCorreo());
            stmt.setString(3, admin.getUsuario());
            stmt.setString(4, admin.getContrasena());

            if (admin.getIdPromotora() != null) {
                stmt.setLong(5, admin.getIdPromotora());
            } else {
                stmt.setNull(5, Types.BIGINT);
            }

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    admin.setId(rs.getLong(1));
                }
            }
        }
        return admin;
    }

    @Override
    public Optional<Administrador> buscarPorUsuario(Connection conn, String usuario) throws SQLException {
        String sql = "SELECT * FROM administradores WHERE usuario = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAdmin(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Administrador> buscarPorId(Connection conn, Long id) throws SQLException {
        String sql = "SELECT * FROM administradores WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAdmin(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean existeUsuarioOCorreo(Connection conn, String usuario, String correo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM administradores WHERE usuario = ? OR correo = ?";
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

    private Administrador mapResultSetToAdmin(ResultSet rs) throws SQLException {
        Administrador admin = new Administrador();
        admin.setId(rs.getLong("id"));
        admin.setNombre(rs.getString("nombre"));
        admin.setCorreo(rs.getString("correo"));
        admin.setUsuario(rs.getString("usuario"));
        admin.setContrasena(rs.getString("contrasena"));

        Timestamp ts = rs.getTimestamp("fecha_registro");
        if (ts != null) {
            admin.setFechaRegistro(ts.toLocalDateTime());
        }

        long idPromotora = rs.getLong("promotora_id");
        if (!rs.wasNull()) {
            admin.setIdPromotora(idPromotora);
        }

        return admin;
    }
}