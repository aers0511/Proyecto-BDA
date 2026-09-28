package controlador;

import entidad.CuentaCliente;
import entidad.OperacionCuenta;
import negocio.CuentaClienteBO;
import persistencia.ICuentaClienteDAO;
import persistencia.IOperacionCuentaDAO;
import persistenciaDAO.CuentaClienteDAO;
import persistenciaDAO.OperacionCuentaDAO;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class CuentaClienteControlador {

    private final CuentaClienteBO cuentaClienteBO;
    private final ICuentaClienteDAO cuentaClienteDAO;
    private final IOperacionCuentaDAO operacionCuentaDAO;

    public CuentaClienteControlador() {
        this.cuentaClienteBO = new CuentaClienteBO();
        this.cuentaClienteDAO = new CuentaClienteDAO();
        this.operacionCuentaDAO = new OperacionCuentaDAO();
    }

    public CuentaCliente registrarCuenta(CuentaCliente cuenta) throws SQLException {
        return cuentaClienteDAO.insertar(cuenta);
    }

    public boolean recargarSaldo(int idCuentaCliente, BigDecimal monto) throws Exception {
        return cuentaClienteBO.recargarSaldo(idCuentaCliente, monto);
    }

    public List<CuentaCliente> obtenerCuentasPorCliente(int idCliente) throws SQLException {
        return cuentaClienteDAO.obtenerPorCliente(idCliente);
    }

    public List<OperacionCuenta> obtenerHistorialOperaciones(int idCuentaCliente) throws SQLException {
        return operacionCuentaDAO.obtenerPorCuentaCliente(idCuentaCliente);
    }
}