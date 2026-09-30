package com.tutiket.repository.impl;

import com.tutiket.domain.OperacionCuenta;
import com.tutiket.domain.enums.TipoOperacion;
import com.tutiket.repository.OperacionCuentaRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcOperacionCuentaRepository implements OperacionCuentaRepository {

    @Override
    public OperacionCuenta guardar(Connection conn, OperacionCuenta op) throws SQLException {
        String sql = "INSERT INTO operaciones_cuenta (id_cuenta_cliente, id_cuenta_promotora, tipo_operacion, monto, fecha_operacion, descripcion) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (op.getIdCuentaCliente() != null) stmt.setLong(1, op.getIdCuentaCliente()); else stmt.setNull(1, Types.BIGINT);
            if (op.getIdCuentaPromotora() != null) stmt.setLong(2, op.getIdCuentaPromotora()); else stmt.setNull(2, Types.BIGINT);
            stmt.setString(3, op.getTipoOperacion().name());
            stmt.setBigDecimal(4, op.getMonto());
            stmt.setTimestamp(5, Timestamp.valueOf(op.getFechaOperacion()));
            stmt.setString(6, op.getDescripcion());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    op.setId(rs.getLong(1));
                }
            }
        }
        return op;
    }

    @Override
    public List<OperacionCuenta> buscarPorCuentaCliente(Connection conn, Long idCuentaCliente) throws SQLException {
        String sql = "SELECT * FROM operaciones_cuenta WHERE id_cuenta_cliente = ? ORDER BY fecha_operacion DESC";
        List<OperacionCuenta> lista = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idCuentaCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapResultSetToOperacion(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public List<OperacionCuenta> buscarPorCuentaPromotora(Connection conn, Long idCuentaPromotora) throws SQLException {
        String sql = "SELECT * FROM operaciones_cuenta WHERE id_cuenta_promotora = ? ORDER BY fecha_operacion DESC";
        List<OperacionCuenta> lista = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idCuentaPromotora);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapResultSetToOperacion(rs));
                }
            }
        }
        return lista;
    }

    private OperacionCuenta mapResultSetToOperacion(ResultSet rs) throws SQLException {
        OperacionCuenta op = new OperacionCuenta();
        op.setId(rs.getLong("id"));
        long idCli = rs.getLong("id_cuenta_cliente");
        if (!rs.wasNull()) op.setIdCuentaCliente(idCli);
        long idPro = rs.getLong("id_cuenta_promotora");
        if (!rs.wasNull()) op.setIdCuentaPromotora(idPro);
        op.setTipoOperacion(TipoOperacion.valueOf(rs.getString("tipo_operacion")));
        op.setMonto(rs.getBigDecimal("monto"));
        Timestamp ts = rs.getTimestamp("fecha_operacion");
        if (ts != null) op.setFechaOperacion(ts.toLocalDateTime());
        op.setDescripcion(rs.getString("descripcion"));
        return op;
    }
}