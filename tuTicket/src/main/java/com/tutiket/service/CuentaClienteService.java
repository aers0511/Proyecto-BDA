package com.tutiket.service;

import com.tutiket.config.DatabaseConfig;
import com.tutiket.domain.CuentaCliente;
import com.tutiket.exception.BusinessException;
import com.tutiket.repository.CuentaClienteRepository;
import com.tutiket.repository.OperacionCuentaRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Random;

public class CuentaClienteService {

    private final CuentaClienteRepository cuentaRepository;
    private final OperacionCuentaRepository operacionRepository;

    public CuentaClienteService(CuentaClienteRepository cuentaRepository, OperacionCuentaRepository operacionRepository) {
        this.cuentaRepository = cuentaRepository;
        this.operacionRepository = operacionRepository;
    }

    public List<CuentaCliente> obtenerCuentasPorCliente(Long idCliente) throws Exception {
        try (Connection conn = DatabaseConfig.getConnection()) {
            return cuentaRepository.buscarPorClienteId(conn, idCliente);
        }
    }

    public void recargarSaldo(Long idCuenta, BigDecimal monto) throws Exception {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El monto a recargar debe ser mayor a $0.00.");
        }

        DatabaseConfig.beginTransaction();
        try {
            Connection conn = DatabaseConfig.getConnection();
            CuentaCliente cuenta = cuentaRepository.buscarPorId(conn, idCuenta)
                    .orElseThrow(() -> new BusinessException("La cuenta seleccionada no existe."));

            BigDecimal nuevoSaldo = cuenta.getSaldo().add(monto);
            cuentaRepository.actualizarSaldo(conn, idCuenta, nuevoSaldo);

            DatabaseConfig.commitTransaction();
        } catch (Exception e) {
            DatabaseConfig.rollbackTransaction();
            throw e;
        }
    }

    public CuentaCliente registrarNuevaCuenta(Long idCliente, String banco, BigDecimal saldoInicial) throws Exception {
        if (banco == null || banco.isBlank()) {
            throw new BusinessException("Debe seleccionar un banco válido.");
        }
        if (saldoInicial == null || saldoInicial.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("El saldo inicial no puede ser negativo.");
        }

        DatabaseConfig.beginTransaction();
        try {
            Connection conn = DatabaseConfig.getConnection();
            Random random = new Random();
            String numCuenta;

            // Genera número de cuenta/tarjeta único de 16 dígitos
            do {
                numCuenta = "4152" + String.format("%012d", Math.abs(random.nextLong()) % 100000000000L);
            } while (cuentaRepository.existeNumeroCuenta(conn, numCuenta));

            CuentaCliente nuevaCuenta = new CuentaCliente();
            nuevaCuenta.setIdCliente(idCliente);
            nuevaCuenta.setNumeroCuenta(numCuenta);
            nuevaCuenta.setBanco(banco.trim());
            nuevaCuenta.setSaldo(saldoInicial);
            nuevaCuenta.setActivo(true);

            CuentaCliente guardada = cuentaRepository.guardar(conn, nuevaCuenta);
            DatabaseConfig.commitTransaction();
            return guardada;

        } catch (Exception e) {
            DatabaseConfig.rollbackTransaction();
            throw e;
        }
    }
}