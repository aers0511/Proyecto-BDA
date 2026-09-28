package persistencia;

import entidad.CuentaPromotora;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface ICuentaPromotoraDAO {
    CuentaPromotora insertar(CuentaPromotora cuenta) throws SQLException;
    CuentaPromotora obtenerPorId(int idCuenta) throws SQLException;
    List<CuentaPromotora> obtenerPorPromotora(int idPromotora) throws SQLException;
    boolean actualizarSaldo(int idCuenta, BigDecimal nuevoSaldo) throws SQLException;
}