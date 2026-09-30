package com.tutiket.repository;

import com.tutiket.domain.CuentaPromotora;
import java.sql.Connection;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CuentaPromotoraRepository {
    CuentaPromotora guardar(Connection conn, CuentaPromotora cuenta) throws SQLException;
    Optional<CuentaPromotora> buscarPorId(Connection conn, Long id) throws SQLException;
    List<CuentaPromotora> buscarPorPromotoraId(Connection conn, Long idPromotora) throws SQLException;
    void actualizarSaldo(Connection conn, Long idCuenta, BigDecimal nuevoSaldo) throws SQLException;
}