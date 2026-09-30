package com.tutiket.repository.impl;

import com.tutiket.domain.CuentaPromotora;
import com.tutiket.repository.CuentaPromotoraRepository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCuentaPromotoraRepository implements CuentaPromotoraRepository {

    @Override
    public CuentaPromotora guardar(Connection conn, CuentaPromotora cuenta) throws SQLException {
        String sql = "INSERT INTO cuentas_promotora (id_promotora, numero_cuenta, banco, saldo, activo) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, cuenta.getIdPromotora());
            stmt.setString(2, cuenta.getNumeroCuenta());
            stmt.setString(3, cuenta.getBanco());
            stmt.setBigDecimal(4, cuenta.getSaldo());
            stmt.setBoolean(5, cuenta.getActivo() != null ? cuenta.getActivo() : true);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    cuenta.setId(rs.getLong(1));
                }
            }
        }
        return cuenta;
    }

    @Override
    public Optional<CuentaPromotora> buscarPorId(Connection conn, Long id) throws SQLException {
        String sql = "SELECT * FROM cuentas_promotora WHERE id = ? AND activo = TRUE";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCuenta(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<CuentaPromotora> buscarPorPromotoraId(Connection conn, Long idPromotora) throws SQLException {
        String sql = "SELECT * FROM cuentas_promotora WHERE id_promotora = ? AND activo = TRUE";
        List<CuentaPromotora> lista = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idPromotora);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapResultSetToCuenta(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public void actualizarSaldo(Connection conn, Long idCuenta, BigDecimal nuevoSaldo) throws SQLException {
        String sql = "UPDATE cuentas_promotora SET saldo = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, nuevoSaldo);
            stmt.setLong(2, idCuenta);
            stmt.executeUpdate();
        }
    }

    private CuentaPromotora mapResultSetToCuenta(ResultSet rs) throws SQLException {
        CuentaPromotora c = new CuentaPromotora();
        c.setId(rs.getLong("id"));
        c.setIdPromotora(rs.getLong("id_promotora"));
        c.setNumeroCuenta(rs.getString("numero_cuenta"));
        c.setBanco(rs.getString("banco"));
        c.setSaldo(rs.getBigDecimal("saldo"));
        c.setActivo(rs.getBoolean("activo"));
        return c;
    }
}