package persistencia;

import entidad.Evento;
import java.sql.SQLException;
import java.util.List;

public interface IEventoDAO {
    Evento insertar(Evento evento) throws SQLException;
    Evento obtenerPorId(int idEvento) throws SQLException;
    List<Evento> obtenerTodos() throws SQLException;
    List<Evento> obtenerPorPromotora(int idPromotora) throws SQLException;
    boolean actualizar(Evento evento) throws SQLException;
    boolean eliminar(int idEvento) throws SQLException;
}