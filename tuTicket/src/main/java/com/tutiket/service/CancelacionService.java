package com.tutiket.service;

import com.tutiket.config.DatabaseConfig;
import com.tutiket.domain.Compra;
import com.tutiket.domain.CuentaCliente;
import com.tutiket.domain.OperacionCuenta;
import com.tutiket.domain.enums.EstadoCompra;
import com.tutiket.domain.enums.TipoOperacion;
import com.tutiket.exception.CancellationExpiredException;
import com.tutiket.exception.ResourceNotFoundException;
import com.tutiket.repository.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.Duration;
import java.time.LocalDateTime;

public class CancelacionService {

    private final CompraRepository compraRepository;
    private final BoletoRepository boletoRepository;
    private final CuentaClienteRepository cuentaRepository;
    private final OperacionCuentaRepository operacionRepository;

    public CancelacionService(CompraRepository compraRepository,
                              BoletoRepository boletoRepository,
                              CuentaClienteRepository cuentaRepository,
                              OperacionCuentaRepository operacionRepository) {
        this.compraRepository = compraRepository;
        this.boletoRepository = boletoRepository;
        this.cuentaRepository = cuentaRepository;
        this.operacionRepository = operacionRepository;
    }

    public void solicitarCancelacion(Long idCompra) throws Exception {
        DatabaseConfig.beginTransaction();
        try {
            Connection conn = DatabaseConfig.getConnection();

            Compra compra = compraRepository.buscarPorId(conn, idCompra)
                    .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada."));

            if (compra.getEstado() == EstadoCompra.CANCELADA) {
                throw new IllegalStateException("La compra ya se encuentra cancelada.");
            }

            // Requisito Legal: Política de arrepentimiento máximo 24 horas
            long horasTranscurridas = Duration.between(compra.getFechaCompra(), LocalDateTime.now()).toHours();
            if (horasTranscurridas >= 24) {
                throw new CancellationExpiredException("Han transcurrido más de 24 horas. La compra ya no puede ser cancelada.");
            }

            compraRepository.actualizarEstado(conn, idCompra, EstadoCompra.CANCELADA.name());
            boletoRepository.liberarBoletosPorCompra(conn, idCompra);

            CuentaCliente cuenta = cuentaRepository.buscarPorId(conn, compra.getIdCuenta())
                    .orElseThrow(() -> new ResourceNotFoundException("Cuenta bancaria no encontrada."));

            BigDecimal nuevoSaldo = cuenta.getSaldo().add(compra.getTotal());
            cuentaRepository.actualizarSaldo(conn, cuenta.getId(), nuevoSaldo);

            OperacionCuenta op = new OperacionCuenta();
            op.setIdCuentaCliente(cuenta.getId());
            op.setTipoOperacion(TipoOperacion.DEPOSITO);
            op.setMonto(compra.getTotal());
            op.setFechaOperacion(LocalDateTime.now());
            op.setDescripcion("Reembolso íntegro por cancelación de compra ID: " + idCompra);
            operacionRepository.guardar(conn, op);

            DatabaseConfig.commitTransaction();
        } catch (Exception e) {
            DatabaseConfig.rollbackTransaction();
            throw e;
        }
    }

    // Alias para compatibilidad con la vista
    public void procesarCancelacion(Long idCompra) throws Exception {
        solicitarCancelacion(idCompra);
    }
}