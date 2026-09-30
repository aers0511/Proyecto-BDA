package com.tutiket.repository.impl;

import com.tutiket.dto.ReporteVentasDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcReporteRepository {

    public List<ReporteVentasDTO> obtenerVentasPorEvento(Connection conn) throws SQLException {
        String sql = "SELECT e.id, e.nombre, COUNT(b.id) as totales, "
                + "SUM(CASE WHEN b.estado = 'VENDIDO' THEN 1 ELSE 0 END) as vendidos, "
                + "COALESCE(SUM(CASE WHEN b.estado = 'VENDIDO' THEN b.precio ELSE 0 END), 0) as ingresos "
                + "FROM eventos e "
                + "LEFT JOIN boletos b ON e.id = b.id_evento "
                + "GROUP BY e.id, e.nombre";

        List<ReporteVentasDTO> reportes = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                reportes.add(mapResultSetToReporte(rs));
            }
        }
        return reportes;
    }

    public List<ReporteVentasDTO> obtenerVentasPorPromotora(Connection conn, Long idPromotora) throws SQLException {
        String sql = "SELECT e.id, e.nombre, COUNT(b.id) as totales, "
                + "SUM(CASE WHEN b.estado = 'VENDIDO' THEN 1 ELSE 0 END) as vendidos, "
                + "COALESCE(SUM(CASE WHEN b.estado = 'VENDIDO' THEN b.precio ELSE 0 END), 0) as ingresos "
                + "FROM eventos e "
                + "LEFT JOIN boletos b ON e.id = b.id_evento "
                + "WHERE e.promotora_id = ? "
                + // Verifica si en tu tabla 'eventos' la columna se llama 'promotora_id' o 'id_promotora'
                "GROUP BY e.id, e.nombre";

        List<ReporteVentasDTO> reportes = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idPromotora);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reportes.add(mapResultSetToReporte(rs));
                }
            }
        }
        return reportes;
    }

    private ReporteVentasDTO mapResultSetToReporte(ResultSet rs) throws SQLException {
        ReporteVentasDTO r = new ReporteVentasDTO();
        r.setIdEvento(rs.getLong("id"));
        r.setNombreEvento(rs.getString("nombre"));
        r.setBoletosTotales(rs.getInt("totales"));
        r.setBoletosVendidos(rs.getInt("vendidos"));
        r.setIngresosTotales(rs.getBigDecimal("ingresos"));
        return r;
    }
}
