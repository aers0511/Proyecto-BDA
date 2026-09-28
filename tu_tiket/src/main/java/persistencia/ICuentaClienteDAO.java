package persistencia;

import entidad.CuentaCliente;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ICuentaClienteDAO {
    CuentaCliente insertar(CuentaCliente cuenta) throws SQLException;
    CuentaCliente obtenerPorId(int idCuentaCliente) throws SQLException;
    List<CuentaCliente> obtenerPorCliente(int idCliente) throws SQLException;
    boolean actualizarSaldo(int idCuentaCliente, BigDecimal nuevoSaldo, Connection conn) throws SQLException;
}