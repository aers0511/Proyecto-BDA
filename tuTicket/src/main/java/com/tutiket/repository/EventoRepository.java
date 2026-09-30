package com.tutiket.repository;

import com.tutiket.domain.Evento;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface EventoRepository {
    Evento guardar(Connection conn, Evento evento) throws SQLException;
    Optional<Evento> buscarPorId(Connection conn, Long id) throws SQLException;
    List<Evento> listarActivos(Connection conn) throws SQLException;
    List<Evento> listarPorPromotora(Connection conn, Long idPromotora) throws SQLException;
    void actualizarEstado(Connection conn, Long idEvento, String estado) throws SQLException;
}