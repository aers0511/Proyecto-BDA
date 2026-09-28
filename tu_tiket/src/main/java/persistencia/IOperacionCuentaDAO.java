package persistencia;

import entidad.OperacionCuenta;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IOperacionCuentaDAO {
    OperacionCuenta insertar(OperacionCuenta operacion, Connection conn) throws SQLException;
    List<OperacionCuenta> obtenerPorCuentaCliente(int idCuentaCliente) throws SQLException;
}