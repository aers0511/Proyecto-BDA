package persistencia;

import entidad.Administrador;
import java.sql.SQLException;
import java.util.List;

public interface IAdministradorDAO {
    Administrador insertar(Administrador administrador) throws SQLException;
    Administrador obtenerPorId(int idAdministrador) throws SQLException;
    Administrador obtenerPorUsuario(String usuario) throws SQLException;
    List<Administrador> obtenerPorPromotora(int idPromotora) throws SQLException;
    boolean actualizar(Administrador administrador) throws SQLException;
}