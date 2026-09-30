package com.tutiket.repository.impl;

import com.tutiket.domain.Compra;
import com.tutiket.domain.enums.EstadoCompra;
import com.tutiket.repository.CompraRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCompraRepository implements CompraRepository {

    @Override
    public Compra guardar(Connection conn, Compra compra) throws SQLException {
        String sql = "INSERT INTO compras (id_cliente, id_cuenta, total, fecha_compra, estado) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, compra.getIdCliente());
            stmt.setLong(2, compra.getIdCuenta());
            stmt.setBigDecimal(3, compra.getTotal());
            stmt.setTimestamp(4, Timestamp.valueOf(compra.getFechaCompra()));
            stmt.setString(5, compra.getEstado().name());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    compra.setId(rs.getLong(1));
                }
            }
        }
        return compra;
    }

    @Override
    public Optional<Compra> buscarPorId(Connection conn, Long id) throws SQLException {
        String sql = "SELECT * FROM compras WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCompra(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Compra> buscarPorCliente(Connection conn, Long idCliente) throws SQLException {
        String sql = "SELECT * FROM compras WHERE id_cliente = ?";
        List<Compra> lista = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapResultSetToCompra(rs));
                }
            }
        }
        return lista;
    }

    // Alias/Delegación para mantener compatibilidad con ClienteComprasPanel
    public List<Compra> buscarPorClienteId(Connection conn, Long idCliente) throws SQLException {
        return buscarPorCliente(conn, idCliente);
    }

    @Override
    public void actualizarEstado(Connection conn, Long idCompra, String estado) throws SQLException {
        String sql = "UPDATE compras SET estado = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, estado);
            stmt.setLong(2, idCompra);
            stmt.executeUpdate();
        }
    }

    private Compra mapResultSetToCompra(ResultSet rs) throws SQLException {
        Compra c = new Compra();
        c.setId(rs.getLong("id"));
        c.setIdCliente(rs.getLong("id_cliente"));
        c.setIdCuenta(rs.getLong("id_cuenta"));
        c.setTotal(rs.getBigDecimal("total"));
        Timestamp ts = rs.getTimestamp("fecha_compra");
        if (ts != null) {
            c.setFechaCompra(ts.toLocalDateTime());
        }
        c.setEstado(EstadoCompra.valueOf(rs.getString("estado")));
        return c;
    }
}