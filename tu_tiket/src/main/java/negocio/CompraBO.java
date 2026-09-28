package negocio;

import entidad.Boleto;
import entidad.Compra;
import entidad.CuentaCliente;
import entidad.OperacionCuenta;
import persistencia.IBoletoDAO;
import persistencia.ICompraDAO;
import persistencia.ICuentaClienteDAO;
import persistencia.IOperacionCuentaDAO;
import persistenciaDAO.BoletoDAO;
import persistenciaDAO.CompraDAO;
import persistenciaDAO.CuentaClienteDAO;
import persistenciaDAO.OperacionCuentaDAO;
import persistenciaUtil.ConexionBD;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CompraBO {

    private final IBoletoDAO boletoDAO;
    private final ICuentaClienteDAO cuentaClienteDAO;
    private final ICompraDAO compraDAO;
    private final IOperacionCuentaDAO operacionCuentaDAO;

    public CompraBO() {
        this.boletoDAO = new BoletoDAO();
        this.cuentaClienteDAO = new CuentaClienteDAO();
        this.compraDAO = new CompraDAO();
        this.operacionCuentaDAO = new OperacionCuentaDAO();
    }

    public List<Compra> realizarCompra(int idCliente, int idCuentaCliente, int idEvento, int cantidad) throws Exception {
        Connection conn = null;
        List<Compra> comprasRealizadas = new ArrayList<>();

        try {
            conn = ConexionBD.obtenerConexion();
            conn.setAutoCommit(false);

            // 1. Validar disponibilidad de boletos
            List<Boleto> boletosDisponibles = boletoDAO.obtenerDisponiblesPorEvento(idEvento, cantidad);
            if (boletosDisponibles.size() < cantidad) {
                throw new Exception("No hay suficientes boletos disponibles para este evento.");
            }

            // 2. Calcular costo total
            BigDecimal precioUnitario = boletosDisponibles.get(0).getPrecio();
            BigDecimal costoTotal = precioUnitario.multiply(new BigDecimal(cantidad));

            // 3. Validar y actualizar saldo de la cuenta
            CuentaCliente cuenta = cuentaClienteDAO.obtenerPorId(idCuentaCliente);
            if (cuenta == null || cuenta.getIdCliente() != idCliente) {
                throw new Exception("La cuenta bancaria seleccionada no es válida.");
            }

            if (cuenta.getSaldo().compareTo(costoTotal) < 0) {
                throw new Exception("Saldo insuficiente en la cuenta seleccionada.");
            }

            BigDecimal saldoAnterior = cuenta.getSaldo();
            BigDecimal saldoNuevo = saldoAnterior.subtract(costoTotal);

            boolean saldoActualizado = cuentaClienteDAO.actualizarSaldo(idCuentaCliente, saldoNuevo, conn);
            if (!saldoActualizado) {
                throw new Exception("No se pudo debitar el saldo de la cuenta.");
            }

            // 4. Registrar la operación contable
            OperacionCuenta op = new OperacionCuenta();
            op.setTipoOperacion("COMPRA_BOLETOS");
            op.setMonto(costoTotal);
            op.setFechaHora(LocalDateTime.now());
            op.setSaldoAnterior(saldoAnterior);
            op.setSaldoNuevo(saldoNuevo);
            op.setIdCuentaCliente(idCuentaCliente);
            operacionCuentaDAO.insertar(op, conn);

            // 5. Actualizar boletos y generar registros de compra
            for (Boleto boleto : boletosDisponibles) {
                boletoDAO.actualizarEstado(boleto.getIdBoleto(), "Vendido", conn);

                Compra compra = new Compra();
                compra.setFechaHora(LocalDateTime.now());
                compra.setPrecioFinal(boleto.getPrecio());
                compra.setEstado("Completada");
                compra.setIdCliente(idCliente);
                compra.setIdCuentaCliente(idCuentaCliente);
                compra.setIdBoleto(boleto.getIdBoleto());

                Compra compraGuardada = compraDAO.insertar(compra, conn);
                comprasRealizadas.add(compraGuardada);
            }

            conn.commit();

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

        return comprasRealizadas;
    }
}