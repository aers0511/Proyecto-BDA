package persistencia;

import entidad.Compra;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ICompraDAO {
    Compra insertar(Compra compra, Connection conn) throws SQLException;
    Compra obtenerPorId(int idCompra) throws SQLException;
    List<Compra> obtenerPorCliente(int idCliente) throws SQLException;
    boolean actualizarEstado(int idCompra, String estado, Connection conn) throws SQLException;
}