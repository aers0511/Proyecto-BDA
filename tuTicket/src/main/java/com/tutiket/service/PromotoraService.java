package com.tutiket.service;

import com.tutiket.config.DatabaseConfig;
import com.tutiket.domain.Promotora;
import com.tutiket.domain.CuentaPromotora;
import com.tutiket.exception.BusinessException;
import com.tutiket.repository.PromotoraRepository;
import com.tutiket.repository.CuentaPromotoraRepository;
import com.tutiket.util.PasswordUtil;

import java.math.BigDecimal;
import java.sql.Connection;

public class PromotoraService {

    private final PromotoraRepository promotoraRepository;
    private final CuentaPromotoraRepository cuentaPromotoraRepository;

    public PromotoraService(PromotoraRepository promotoraRepository, CuentaPromotoraRepository cuentaPromotoraRepository) {
        this.promotoraRepository = promotoraRepository;
        this.cuentaPromotoraRepository = cuentaPromotoraRepository;
    }

    public Promotora registrarPromotora(Promotora promotora) throws Exception {
        DatabaseConfig.beginTransaction();
        try {
            Connection conn = DatabaseConfig.getConnection();

            if (promotoraRepository.existeRfcOCorreoOUsuario(conn, promotora.getRfc(), promotora.getCorreo(), promotora.getUsuario())) {
                throw new BusinessException("RFC, Correo o Usuario ya registrado para otra promotora.");
            }

            promotora.setContrasena(PasswordUtil.hashPassword(promotora.getContrasena()));
            promotora.setActivo(true);

            Promotora guardada = promotoraRepository.guardar(conn, promotora);

            // Requisito: Crear 2 cuentas iniciales con saldo 0 para la promotora[cite: 1]
            for (int i = 1; i <= 2; i++) {
                CuentaPromotora c = new CuentaPromotora();
                c.setIdPromotora(guardada.getId());
                c.setNumeroCuenta("9876" + System.currentTimeMillis() + i);
                c.setBanco("BBVA");
                c.setSaldo(BigDecimal.ZERO);
                c.setActivo(true);
                cuentaPromotoraRepository.guardar(conn, c);
            }

            DatabaseConfig.commitTransaction();
            return guardada;
        } catch (Exception e) {
            DatabaseConfig.rollbackTransaction();
            throw e;
        }
    }
}