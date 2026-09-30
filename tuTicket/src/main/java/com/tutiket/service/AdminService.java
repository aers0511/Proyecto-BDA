package com.tutiket.service;

import com.tutiket.config.DatabaseConfig;
import com.tutiket.domain.Administrador;
import com.tutiket.dto.ReporteVentasDTO;
import com.tutiket.exception.BusinessException;
import com.tutiket.repository.AdministradorRepository;
import com.tutiket.repository.impl.JdbcReporteRepository;
import com.tutiket.util.PasswordUtil;

import java.sql.Connection;
import java.util.List;

public class AdminService {

    private final JdbcReporteRepository reporteRepository;
    private final AdministradorRepository administradorRepository;

    public AdminService(JdbcReporteRepository reporteRepository, AdministradorRepository administradorRepository) {
        this.reporteRepository = reporteRepository;
        this.administradorRepository = administradorRepository;
    }

    public Administrador registrarAdmin(Administrador admin) throws Exception {
        if (admin.getUsuario() == null || admin.getUsuario().isBlank() ||
            admin.getCorreo() == null || admin.getCorreo().isBlank() ||
            admin.getContrasena() == null || admin.getContrasena().isBlank()) {
            throw new BusinessException("Usuario, correo y contraseña son obligatorios.");
        }

        DatabaseConfig.beginTransaction();
        try {
            Connection conn = DatabaseConfig.getConnection();

            if (administradorRepository.existeUsuarioOCorreo(conn, admin.getUsuario(), admin.getCorreo())) {
                throw new BusinessException("El usuario o correo ya existe para otro administrador.");
            }

            admin.setContrasena(PasswordUtil.hashPassword(admin.getContrasena()));
            Administrador guardado = administradorRepository.guardar(conn, admin);

            DatabaseConfig.commitTransaction();
            return guardado;

        } catch (Exception e) {
            DatabaseConfig.rollbackTransaction();
            throw e;
        }
    }

    public List<ReporteVentasDTO> consultarTableroVentas(Long idPromotora) throws Exception {
        try (Connection conn = DatabaseConfig.getConnection()) {
            if (idPromotora != null) {
                return reporteRepository.obtenerVentasPorPromotora(conn, idPromotora);
            }
            return reporteRepository.obtenerVentasPorEvento(conn);
        }
    }

    public List<ReporteVentasDTO> consultarTableroVentas() throws Exception {
        return consultarTableroVentas(null);
    }
}