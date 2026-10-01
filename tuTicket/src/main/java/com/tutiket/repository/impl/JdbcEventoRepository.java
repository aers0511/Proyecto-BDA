package com.tutiket.repository.impl;

import com.tutiket.domain.Evento;
import com.tutiket.domain.enums.EstadoEvento;
import com.tutiket.repository.EventoRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcEventoRepository implements EventoRepository {

    @Override
    public Evento guardar(Connection conn, Evento evento) throws SQLException {
        String sql = "INSERT INTO eventos (id_promotora, nombre, categoria, clasificacion, fecha_hora, lugar, precio_base, cantidad_boletos, imagen_path, estado) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setObject(1, evento.getIdPromotora());
            stmt.setString(2, evento.getNombre());
            stmt.setString(3, evento.getCategoria());
            stmt.setString(4, evento.getClasificacion());
            stmt.setTimestamp(5, Timestamp.valueOf(evento.getFechaHora()));
            stmt.setString(6, evento.getLugar());
            stmt.setBigDecimal(7, evento.getPrecioBase());
            stmt.setInt(8, evento.getCantidadBoletos());
            stmt.setString(9, evento.getImagenPath());
            stmt.setString(10, evento.getEstado() != null ? evento.getEstado().name() : EstadoEvento.ACTIVO.name());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    evento.setId(rs.getLong(1));
                }
            }
        }
        return evento;
    }

    @Override
    public List<Evento> listarActivos(Connection conn) throws SQLException {
        List<Evento> eventos = new ArrayList<>();
        String sql = "SELECT * FROM eventos WHERE estado = 'ACTIVO'";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                eventos.add(mapResultSetToEvento(rs));
            }
        }
        return eventos;
    }

    @Override
    public List<Evento> listarTodosConPromotora(Connection conn) throws SQLException {
        List<Evento> eventos = new ArrayList<>();
        String sql = "SELECT e.*, COALESCE(p.nombre_empresa, 'Sin Promotora') AS nombre_promotora " +
                     "FROM eventos e LEFT JOIN promotoras p ON e.id_promotora = p.id " +
                     "ORDER BY e.fecha_hora DESC";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Evento evento = mapResultSetToEvento(rs);
                // Si tienes un campo transient o setter temporal para mostrar la promotora
                evento.setImagenPath(rs.getString("nombre_promotora")); // O mapearlo en tu DTO / Objeto
                eventos.add(evento);
            }
        }
        return eventos;
    }

    @Override
    public Optional<Evento> buscarPorId(Connection conn, Long id) throws SQLException {
        String sql = "SELECT * FROM eventos WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEvento(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Evento> listarPorPromotora(Connection conn, Long idPromotora) throws SQLException {
        List<Evento> eventos = new ArrayList<>();
        String sql = "SELECT * FROM eventos WHERE id_promotora = ? AND estado = 'ACTIVO'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idPromotora);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    eventos.add(mapResultSetToEvento(rs));
                }
            }
        }
        return eventos;
    }

    @Override
    public void actualizarEstado(Connection conn, Long idEvento, String estado) throws SQLException {
        String sql = "UPDATE eventos SET estado = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, estado);
            stmt.setLong(2, idEvento);
            stmt.executeUpdate();
        }
    }

    private Evento mapResultSetToEvento(ResultSet rs) throws SQLException {
        Evento evento = new Evento();
        evento.setId(rs.getLong("id"));
        evento.setIdPromotora(rs.getObject("id_promotora") != null ? rs.getLong("id_promotora") : null);
        evento.setNombre(rs.getString("nombre"));
        evento.setCategoria(rs.getString("categoria"));
        evento.setClasificacion(rs.getString("clasificacion"));
        Timestamp ts = rs.getTimestamp("fecha_hora");
        if (ts != null) {
            evento.setFechaHora(ts.toLocalDateTime());
        }
        evento.setLugar(rs.getString("lugar"));
        evento.setPrecioBase(rs.getBigDecimal("precio_base"));
        evento.setCantidadBoletos(rs.getInt("cantidad_boletos"));

        String estadoStr = rs.getString("estado");
        if (estadoStr != null) {
            try {
                evento.setEstado(EstadoEvento.valueOf(estadoStr));
            } catch (IllegalArgumentException e) {
                evento.setEstado(null);
            }
        }
        return evento;
    }
}