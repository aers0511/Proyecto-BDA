package com.tutiket.repository.impl;

import com.tutiket.domain.Boleto;
import com.tutiket.domain.enums.EstadoBoleto;
import com.tutiket.repository.BoletoRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcBoletoRepository implements BoletoRepository {

    @Override
    public void guardar(Connection conn, Boleto boleto) throws SQLException {
        String sql = "INSERT INTO boletos (id_evento, folio, zona, asiento, precio, estado) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, boleto.getIdEvento());
            stmt.setString(2, boleto.getFolio());
            stmt.setString(3, boleto.getZona() != null ? boleto.getZona() : "General"); // 👈 Asigna 'General' si viene null
            stmt.setString(4, boleto.getAsiento());
            stmt.setBigDecimal(5, boleto.getPrecio());
            stmt.setString(6, boleto.getEstado().name());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    boleto.setId(rs.getLong(1));
                }
            }
        }
    }

    @Override
    public void guardarLote(Connection conn, List<Boleto> boletos) throws SQLException {
        String sql = "INSERT INTO boletos (id_evento, folio, zona, asiento, precio, estado) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Boleto b : boletos) {
                stmt.setLong(1, b.getIdEvento());
                stmt.setString(2, b.getFolio());
                // 👈 Corregido: antes tenías b.getZona() directamente y enviaba null
                stmt.setString(3, b.getZona() != null ? b.getZona() : "General"); 
                stmt.setString(4, b.getAsiento());
                stmt.setBigDecimal(5, b.getPrecio());
                stmt.setString(6, b.getEstado().name());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    @Override
    public Optional<Boleto> buscarPorId(Connection conn, Long id) throws SQLException {
        String sql = "SELECT * FROM boletos WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToBoleto(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Boleto> buscarDisponiblesPorEvento(Connection conn, Long idEvento) throws SQLException {
        String sql = "SELECT * FROM boletos WHERE id_evento = ? AND estado = 'DISPONIBLE'";
        List<Boleto> lista = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idEvento);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapResultSetToBoleto(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public List<Boleto> buscarPorCompra(Connection conn, Long idCompra) throws SQLException {
        String sql = "SELECT * FROM boletos WHERE id_compra = ?";
        List<Boleto> lista = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idCompra);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapResultSetToBoleto(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public List<Boleto> buscarPorCliente(Connection conn, Long idCliente) throws SQLException {
        String sql = "SELECT b.* FROM boletos b " +
                     "JOIN compras c ON b.id_compra = c.id " +
                     "WHERE c.id_cliente = ? AND b.estado = 'VENDIDO'";
        List<Boleto> lista = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapResultSetToBoleto(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public void actualizarVenta(Connection conn, Long idBoleto, Long idCompra, String estado) throws SQLException {
        String sql = "UPDATE boletos SET id_compra = ?, estado = ? WHERE id = ? AND estado = 'DISPONIBLE'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idCompra);
            stmt.setString(2, estado);
            stmt.setLong(3, idBoleto);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("El boleto ya no se encuentra disponible o no existe.");
            }
        }
    }

    @Override
    public void liberarBoletosPorCompra(Connection conn, Long idCompra) throws SQLException {
        String sql = "UPDATE boletos SET id_compra = NULL, estado = 'DISPONIBLE' WHERE id_compra = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idCompra);
            stmt.executeUpdate();
        }
    }

    private Boleto mapResultSetToBoleto(ResultSet rs) throws SQLException {
        Boleto b = new Boleto();
        b.setId(rs.getLong("id"));
        b.setIdEvento(rs.getLong("id_evento"));
        long idCompra = rs.getLong("id_compra");
        if (!rs.wasNull()) b.setIdCompra(idCompra);
        b.setFolio(rs.getString("folio"));
        b.setZona(rs.getString("zona"));
        b.setAsiento(rs.getString("asiento"));
        b.setPrecio(rs.getBigDecimal("precio"));
        b.setEstado(EstadoBoleto.valueOf(rs.getString("estado")));
        return b;
    }
}