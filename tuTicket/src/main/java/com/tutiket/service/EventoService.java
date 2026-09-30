package com.tutiket.service;

import com.tutiket.config.DatabaseConfig;
import com.tutiket.domain.Boleto;
import com.tutiket.domain.Evento;
import com.tutiket.domain.enums.EstadoBoleto;
import com.tutiket.exception.BusinessException;
import com.tutiket.repository.BoletoRepository;
import com.tutiket.repository.EventoRepository;

import java.sql.Connection;
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

            // 1. Guardar el evento en la BD
            Evento eventoGuardado = eventoRepository.guardar(conn, evento);

            // 2. Generar automáticamente la lista de boletos disponibles
            for (int i = 1; i <= cantidadBoletos; i++) {
                Boleto boleto = new Boleto();
                boleto.setIdEvento(eventoGuardado.getId());
                boleto.setPrecio(eventoGuardado.getPrecioBase());
                boleto.setEstado(EstadoBoleto.DISPONIBLE);
                
                // Generar un folio único por boleto (ejemplo: EV5-0001-A1B2)
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
}