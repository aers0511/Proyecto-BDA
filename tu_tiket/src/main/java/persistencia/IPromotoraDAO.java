package persistencia;

import entidad.Promotora;
import java.sql.SQLException;
import java.util.List;

public interface IPromotoraDAO {
    Promotora insertar(Promotora promotora) throws SQLException;
    Promotora obtenerPorId(int idPromotora) throws SQLException;
    List<Promotora> obtenerTodos() throws SQLException;
    boolean actualizar(Promotora promotora) throws SQLException;
    boolean eliminar(int idPromotora) throws SQLException;
}