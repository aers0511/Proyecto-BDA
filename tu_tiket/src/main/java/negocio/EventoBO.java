package negocio;

import entidad.Boleto;
import entidad.Evento;
import persistencia.IBoletoDAO;
import persistencia.IEventoDAO;
import persistenciaDAO.BoletoDAO;
import persistenciaDAO.EventoDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EventoBO {

    private final IEventoDAO eventoDAO;
    private final IBoletoDAO boletoDAO;

    public EventoBO() {
        this.eventoDAO = new EventoDAO();
        this.boletoDAO = new BoletoDAO();
    }

    public Evento registrarEventoConBoletos(Evento evento) throws Exception {
        if (evento.getCantidadBoletos() <= 0) {
            throw new Exception("La cantidad de boletos debe ser mayor a cero.");
        }
        if (evento.getPrecioBoleto() == null || evento.getPrecioBoleto().doubleValue() <= 0) {
            throw new Exception("El precio del boleto debe ser un valor positivo.");
        }

        // 1. Guardar el evento en BD para obtener el idEvento generado
        Evento eventoGuardado = eventoDAO.insertar(evento);

        // 2. Generar masivamente los boletos vinculados al evento
        List<Boleto> boletos = new ArrayList<>();
        for (int i = 1; i <= eventoGuardado.getCantidadBoletos(); i++) {
            // Genera un código único en formato EV[ID]-[NUMERO_SECUENCIAL]-[CODIGO_CORTO]
            String codigo = String.format("EV%d-%04d-%s", 
                eventoGuardado.getIdEvento(), 
                i, 
                UUID.randomUUID().toString().substring(0, 4).toUpperCase());

            Boleto boleto = new Boleto();
            boleto.setCodigoBoleto(codigo);
            boleto.setPrecio(eventoGuardado.getPrecioBoleto());
            boleto.setEstado("Disponible");
            boleto.setIdEvento(eventoGuardado.getIdEvento());

            boletos.add(boleto);
        }

        // 3. Insertar el lote en una sola transacción JDBC
        boolean insertados = boletoDAO.insertarLote(boletos);
        if (!insertados) {
            throw new Exception("No se pudieron generar los boletos para el evento.");
        }

        return eventoGuardado;
    }

    public List<Evento> obtenerTodos() throws SQLException {
        return eventoDAO.obtenerTodos();
    }

    public Evento obtenerPorId(int idEvento) throws SQLException {
        return eventoDAO.obtenerPorId(idEvento);
    }
}