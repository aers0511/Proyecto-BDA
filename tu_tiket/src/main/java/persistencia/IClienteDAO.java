package persistencia;

import entidad.Cliente;
import java.sql.SQLException;

public interface IClienteDAO {
    Cliente insertar(Cliente cliente) throws SQLException;
    Cliente obtenerPorId(int idCliente) throws SQLException;
    Cliente obtenerPorUsuario(String usuario) throws SQLException;
    boolean actualizar(Cliente cliente) throws SQLException;
}