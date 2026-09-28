package negocio;

import entidad.CuentaCliente;
import entidad.OperacionCuenta;
import persistencia.ICuentaClienteDAO;
import persistencia.IOperacionCuentaDAO;
import persistenciaDAO.CuentaClienteDAO;
import persistenciaDAO.OperacionCuentaDAO;
import persistenciaUtil.ConexionBD;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class CuentaClienteBO {

    private final ICuentaClienteDAO cuentaClienteDAO;
    private final IOperacionCuentaDAO operacionCuentaDAO;

    public CuentaClienteBO() {
        this.cuentaClienteDAO = new CuentaClienteDAO();
        this.operacionCuentaDAO = new OperacionCuentaDAO();
    }

    public boolean recargarSaldo(int idCuentaCliente, BigDecimal monto) throws Exception {
        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("El monto a recargar debe ser mayor a cero.");
        }

        Connection conn = null;
        try {
            conn = ConexionBD.obtenerConexion();
            conn.setAutoCommit(false);

            CuentaCliente cuenta = cuentaClienteDAO.obtenerPorId(idCuentaCliente);
            if (cuenta == null) {
                throw new Exception("La cuenta especificada no existe.");
            }

            BigDecimal saldoAnterior = cuenta.getSaldo();
            BigDecimal saldoNuevo = saldoAnterior.add(monto);

            boolean actualizado = cuentaClienteDAO.actualizarSaldo(idCuentaCliente, saldoNuevo, conn);
            if (!actualizado) {
                throw new Exception("Error al procesar la recarga.");
            }

            OperacionCuenta op = new OperacionCuenta();
            op.setTipoOperacion("RECARGA");
            op.setMonto(monto);
            op.setFechaHora(LocalDateTime.now());
            op.setSaldoAnterior(saldoAnterior);
            op.setSaldoNuevo(saldoNuevo);
            op.setIdCuentaCliente(idCuentaCliente);
            operacionCuentaDAO.insertar(op, conn);

            conn.commit();
            return true;

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}