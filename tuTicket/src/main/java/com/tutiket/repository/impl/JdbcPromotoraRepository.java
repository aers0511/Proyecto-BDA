package com.tutiket.repository.impl;

import com.tutiket.domain.Promotora;
import com.tutiket.repository.PromotoraRepository;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class JdbcPromotoraRepository implements PromotoraRepository {

    @Override
    public Promotora guardar(Connection conn, Promotora promotora) throws SQLException {
        String sql = "INSERT INTO promotoras (nombre_empresa, correo, usuario, contrasena, rfc) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, promotora.getNombreEmpresa());
            stmt.setString(2, promotora.getCorreo());
            stmt.setString(3, promotora.getUsuario());
            stmt.setString(4, promotora.getContrasena());
            stmt.setString(5, promotora.getRfc());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    promotora.setId(rs.getLong(1));
                }
            }
        }
        return promotora;
    }

    @Override
    public Optional<Promotora> buscarPorUsuario(Connection conn, String usuario) throws SQLException {
        String sql = "SELECT * FROM promotoras WHERE usuario = ? OR correo = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario);
            stmt.setString(2, usuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Promotora p = new Promotora();
                    p.setId(rs.getLong("id"));
                    p.setNombreEmpresa(rs.getString("nombre_empresa"));
                    p.setCorreo(rs.getString("correo"));
                    p.setUsuario(rs.getString("usuario"));
                    p.setContrasena(rs.getString("contrasena"));
                    p.setRfc(rs.getString("rfc"));
                    return Optional.of(p);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean existeRfcOCorreoOUsuario(Connection conn, String rfc, String correo, String usuario) throws SQLException {
        String sql = "SELECT COUNT(*) FROM promotoras WHERE rfc = ? OR correo = ? OR usuario = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, rfc);
            stmt.setString(2, correo);
            stmt.setString(3, usuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

}