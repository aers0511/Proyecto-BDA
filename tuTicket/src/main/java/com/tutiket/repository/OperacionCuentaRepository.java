package com.tutiket.repository;

import com.tutiket.domain.OperacionCuenta;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface OperacionCuentaRepository {
    OperacionCuenta guardar(Connection conn, OperacionCuenta operacion) throws SQLException;
    List<OperacionCuenta> buscarPorCuentaCliente(Connection conn, Long idCuentaCliente) throws SQLException;
    List<OperacionCuenta> buscarPorCuentaPromotora(Connection conn, Long idCuentaPromotora) throws SQLException;
}