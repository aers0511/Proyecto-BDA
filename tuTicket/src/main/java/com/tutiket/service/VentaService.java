package com.tutiket.service;

import com.tutiket.config.DatabaseConfig;
import com.tutiket.domain.*;
import com.tutiket.domain.enums.EstadoBoleto;
import com.tutiket.domain.enums.EstadoCompra;
import com.tutiket.domain.enums.TipoOperacion;
import com.tutiket.dto.CompraRequestDTO;
import com.tutiket.exception.BusinessException;
import com.tutiket.exception.InsufficientBalanceException;
import com.tutiket.exception.ResourceNotFoundException;
import com.tutiket.repository.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;

public class VentaService {

    private final BoletoRepository boletoRepository;
    private final CuentaClienteRepository cuentaClienteRepository;
    private final CompraRepository compraRepository;
    private final OperacionCuentaRepository operacionRepository;

    public VentaService(BoletoRepository boletoRepository, CuentaClienteRepository cuentaClienteRepository,
                        CompraRepository compraRepository, OperacionCuentaRepository operacionRepository) {
        this.boletoRepository = boletoRepository;
        this.cuentaClienteRepository = cuentaClienteRepository;
        this.compraRepository = compraRepository;
        this.operacionRepository = operacionRepository;
    }

    public Compra procesarCompra(CompraRequestDTO dto) throws Exception {
        DatabaseConfig.beginTransaction();
        try {
            Connection conn = DatabaseConfig.getConnection();

            Boleto boleto = boletoRepository.buscarPorId(conn, dto.getIdBoleto())
                    .orElseThrow(() -> new ResourceNotFoundException("Boleto no encontrado."));

            if (boleto.getEstado() != EstadoBoleto.DISPONIBLE) {
                throw new BusinessException("El boleto seleccionado ya no se encuentra disponible.");
            }

            CuentaCliente cuenta = cuentaClienteRepository.buscarPorId(conn, dto.getIdCuentaCliente())
                    .orElseThrow(() -> new ResourceNotFoundException("Cuenta bancaria no encontrada."));

            if (!cuenta.getIdCliente().equals(dto.getIdCliente())) {
                throw new BusinessException("La cuenta bancaria no pertenece al cliente solicitante.");
            }

            if (cuenta.getSaldo().compareTo(boleto.getPrecio()) < 0) {
                throw new InsufficientBalanceException("Saldo insuficiente para realizar la compra.");
            }

            Compra compra = new Compra();
            compra.setIdCliente(dto.getIdCliente());
            compra.setIdCuenta(dto.getIdCuentaCliente());
            compra.setTotal(boleto.getPrecio());
            compra.setFechaCompra(LocalDateTime.now());
            compra.setEstado(EstadoCompra.COMPLETADA);

            Compra compraGuardada = compraRepository.guardar(conn, compra);

            boletoRepository.actualizarVenta(conn, boleto.getId(), compraGuardada.getId(), EstadoBoleto.VENDIDO.name());

            BigDecimal nuevoSaldo = cuenta.getSaldo().subtract(boleto.getPrecio());
            cuentaClienteRepository.actualizarSaldo(conn, cuenta.getId(), nuevoSaldo);

            OperacionCuenta op = new OperacionCuenta();
            op.setIdCuentaCliente(cuenta.getId());
            op.setTipoOperacion(TipoOperacion.PAGO_BOLETO);
            op.setMonto(boleto.getPrecio());
            op.setFechaOperacion(LocalDateTime.now());
            op.setDescripcion("Pago de boleto Folio: " + boleto.getFolio());
            operacionRepository.guardar(conn, op);

            DatabaseConfig.commitTransaction();
            return compraGuardada;

        } catch (Exception e) {
            DatabaseConfig.rollbackTransaction();
            throw e;
        }
    }
}