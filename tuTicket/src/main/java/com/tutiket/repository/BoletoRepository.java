package com.tutiket.repository;

import com.tutiket.domain.Boleto;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface BoletoRepository {
    void guardar(Connection conn, Boleto boleto) throws SQLException;
    void guardarLote(Connection conn, List<Boleto> boletos) throws SQLException;
    Optional<Boleto> buscarPorId(Connection conn, Long id) throws SQLException;
    List<Boleto> buscarDisponiblesPorEvento(Connection conn, Long idEvento) throws SQLException;
    List<Boleto> buscarPorCompra(Connection conn, Long idCompra) throws SQLException;
    List<Boleto> buscarPorCliente(Connection conn, Long idCliente) throws SQLException; // 👈 Nuevo método
    void actualizarVenta(Connection conn, Long idBoleto, Long idCompra, String estado) throws SQLException;
    void liberarBoletosPorCompra(Connection conn, Long idCompra) throws SQLException;
}