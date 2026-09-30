package com.tutiket.repository;

import com.tutiket.domain.Administrador;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public interface AdministradorRepository {
    Administrador guardar(Connection conn, Administrador admin) throws SQLException;
    Optional<Administrador> buscarPorUsuario(Connection conn, String usuario) throws SQLException;
    Optional<Administrador> buscarPorId(Connection conn, Long id) throws SQLException;
    boolean existeUsuarioOCorreo(Connection conn, String usuario, String correo) throws SQLException;
}