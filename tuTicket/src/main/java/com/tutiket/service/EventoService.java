package com.tutiket.service;

import com.tutiket.config.DatabaseConfig;
import com.tutiket.domain.Boleto;
import com.tutiket.domain.Evento;
import com.tutiket.domain.enums.EstadoBoleto;
import com.tutiket.exception.BusinessException;
import com.tutiket.repository.BoletoRepository;
import com.tutiket.repository.EventoRepository;

import java.sql.Connection;
import java.util.List;
import java.util.UUID;

public class EventoService {

    private final EventoRepository eventoRepository;
    private final BoletoRepository boletoRepository;

    public EventoService(EventoRepository eventoRepository, BoletoRepository boletoRepository) {
        this.eventoRepository = eventoRepository;
        this.boletoRepository = boletoRepository;
    }

    public Evento registrarEventoConBoletos(Evento evento, int cantidadBoletos) throws Exception {
        if (cantidadBoletos <= 0) {
            throw new BusinessException("La cantidad de boletos a generar debe ser mayor a 0.");
        }

        DatabaseConfig.beginTransaction();
        try {
            Connection conn = DatabaseConfig.getConnection();

            Evento eventoGuardado = eventoRepository.guardar(conn, evento);

            for (int i = 1; i <= cantidadBoletos; i++) {
                Boleto boleto = new Boleto();
                boleto.setIdEvento(eventoGuardado.getId());
                boleto.setPrecio(eventoGuardado.getPrecioBase());
                boleto.setEstado(EstadoBoleto.DISPONIBLE);
                
                String folio = String.format("EV%d-%04d-%s", 
                        eventoGuardado.getId(), 
                        i, 
                        UUID.randomUUID().toString().substring(0, 4).toUpperCase());
                boleto.setFolio(folio);

                boletoRepository.guardar(conn, boleto);
            }

            DatabaseConfig.commitTransaction();
            return eventoGuardado;

        } catch (Exception e) {
            DatabaseConfig.rollbackTransaction();
            throw e;
        }
    }

    public List<Evento> obtenerTodosLosEventos() throws Exception {
        try (Connection conn = DatabaseConfig.getConnection()) {
            return eventoRepository.listarTodosConPromotora(conn);
        }
    }

    public void cancelarEvento(Long idEvento) throws Exception {
        if (idEvento == null || idEvento <= 0) {
            throw new BusinessException("El ID del evento no es válido.");
        }

        DatabaseConfig.beginTransaction();
        try {
            Connection conn = DatabaseConfig.getConnection();
            eventoRepository.actualizarEstado(conn, idEvento, "CANCELADO");
            DatabaseConfig.commitTransaction();
        } catch (Exception e) {
            DatabaseConfig.rollbackTransaction();
            throw e;
        }
    }
}