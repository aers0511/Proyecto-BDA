package com.tutiket.repository;

import com.tutiket.domain.CuentaCliente;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CuentaClienteRepository {
    CuentaCliente guardar(Connection conn, CuentaCliente cuenta) throws SQLException;
    Optional<CuentaCliente> buscarPorId(Connection conn, Long id) throws SQLException;
    List<CuentaCliente> buscarPorClienteId(Connection conn, Long idCliente) throws SQLException;
    void actualizarSaldo(Connection conn, Long idCuenta, BigDecimal nuevoSaldo) throws SQLException;
    boolean existeNumeroCuenta(Connection conn, String numeroCuenta) throws SQLException;
}