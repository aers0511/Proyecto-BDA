package persistencia;

import entidad.Boleto;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IBoletoDAO {
    boolean insertarLote(List<Boleto> boletos) throws SQLException;
    Boleto obtenerPorId(int idBoleto) throws SQLException;
    List<Boleto> obtenerPorEvento(int idEvento) throws SQLException;
    List<Boleto> obtenerDisponiblesPorEvento(int idEvento, int cantidad) throws SQLException;
    boolean actualizarEstado(int idBoleto, String estado, Connection conn) throws SQLException;
}