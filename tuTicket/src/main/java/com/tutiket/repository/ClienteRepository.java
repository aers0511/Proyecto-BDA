package com.tutiket.repository;

import com.tutiket.domain.Cliente;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public interface ClienteRepository {
    Cliente guardar(Connection conn, Cliente cliente) throws SQLException;
    Optional<Cliente> buscarPorId(Connection conn, Long id) throws SQLException;
    Optional<Cliente> buscarPorUsuario(Connection conn, String usuario) throws SQLException;
    boolean existeUsuarioOCorreo(Connection conn, String usuario, String correo) throws SQLException;
}