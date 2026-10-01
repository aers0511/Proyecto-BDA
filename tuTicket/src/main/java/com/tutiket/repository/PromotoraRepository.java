package com.tutiket.repository;

import com.tutiket.domain.Promotora;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface PromotoraRepository {

    Promotora guardar(Connection conn, Promotora promotora) throws SQLException;

    Optional<Promotora> buscarPorUsuario(Connection conn, String usuario) throws SQLException;

    boolean existeRfcOCorreoOUsuario(Connection conn, String rfc, String correo, String usuario) throws SQLException;

    List<Promotora> buscarTodas(Connection conn) throws SQLException;
}
