package com.tutiket.repository;

import com.tutiket.domain.Compra;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CompraRepository {
    Compra guardar(Connection conn, Compra compra) throws SQLException;
    Optional<Compra> buscarPorId(Connection conn, Long id) throws SQLException;
    List<Compra> buscarPorCliente(Connection conn, Long idCliente) throws SQLException;
    void actualizarEstado(Connection conn, Long idCompra, String estado) throws SQLException;
}