package controlador;

import entidad.Evento;
import negocio.EventoBO;

import java.sql.SQLException;
import java.util.List;

public class EventoControlador {

    private final EventoBO eventoBO;

    public EventoControlador() {
        this.eventoBO = new EventoBO();
    }

    public Evento registrarEvento(Evento evento) throws Exception {
        return eventoBO.registrarEventoConBoletos(evento);
    }

    public List<Evento> listarEventos() throws SQLException {
        return eventoBO.obtenerTodos();
    }

    public Evento obtenerEventoPorId(int idEvento) throws SQLException {
        return eventoBO.obtenerPorId(idEvento);
    }
}