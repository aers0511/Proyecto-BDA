package com.tutiket.repository.impl;

import com.tutiket.domain.CuentaCliente;
import com.tutiket.repository.CuentaClienteRepository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCuentaClienteRepository implements CuentaClienteRepository {

    @Override
    public CuentaCliente guardar(Connection conn, CuentaCliente cuenta) throws SQLException {
        String sql = "INSERT INTO cuentas_cliente (id_cliente, numero_cuenta, banco, saldo, activo) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, cuenta.getIdCliente());
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
    public Optional<CuentaCliente> buscarPorId(Connection conn, Long id) throws SQLException {
        String sql = "SELECT * FROM cuentas_cliente WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<CuentaCliente> buscarPorClienteId(Connection conn, Long idCliente) throws SQLException {
        String sql = "SELECT * FROM cuentas_cliente WHERE id_cliente = ? AND activo = true";
        List<CuentaCliente> lista = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapResultSet(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public void actualizarSaldo(Connection conn, Long idCuenta, BigDecimal nuevoSaldo) throws SQLException {
        String sql = "UPDATE cuentas_cliente SET saldo = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, nuevoSaldo);
            stmt.setLong(2, idCuenta);
            stmt.executeUpdate();
        }
    }

    @Override
    public boolean existeNumeroCuenta(Connection conn, String numeroCuenta) throws SQLException {
        String sql = "SELECT COUNT(*) FROM cuentas_cliente WHERE numero_cuenta = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numeroCuenta);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private CuentaCliente mapResultSet(ResultSet rs) throws SQLException {
        CuentaCliente cc = new CuentaCliente();
        cc.setId(rs.getLong("id"));
        cc.setIdCliente(rs.getLong("id_cliente"));
        cc.setNumeroCuenta(rs.getString("numero_cuenta"));
        cc.setBanco(rs.getString("banco"));
        cc.setSaldo(rs.getBigDecimal("saldo"));
        cc.setActivo(rs.getBoolean("activo"));
        return cc;
    }
}