package com.tutiket.view.dialog;

import com.tutiket.domain.Boleto;
import com.tutiket.domain.Cliente;
import com.tutiket.domain.CuentaCliente;
import com.tutiket.dto.CompraRequestDTO;
import com.tutiket.exception.InsufficientBalanceException;
import com.tutiket.repository.impl.*;
import com.tutiket.service.CuentaClienteService;
import com.tutiket.service.VentaService;
import com.tutiket.view.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class DialogoCompraBoleto extends JDialog {

    private final Cliente cliente;
    private final Boleto boleto;
    private final VentaService ventaService;
    private final CuentaClienteService cuentaService;

    private JComboBox<CuentaClienteWrapper> cbCuentas;
    private boolean compraExitosa = false;

    public DialogoCompraBoleto(Window parentOwner, Cliente cliente, Boleto boleto) {
        super(parentOwner, "Confirmar Compra de Boleto", ModalityType.APPLICATION_MODAL);
        this.cliente = cliente;
        this.boleto = boleto;

        // Inicialización de servicios
        this.ventaService = new VentaService(
                new JdbcBoletoRepository(),
                new JdbcCuentaClienteRepository(),
                new JdbcCompraRepository(),
                new JdbcOperacionCuentaRepository()
        );
        this.cuentaService = new CuentaClienteService(
                new JdbcCuentaClienteRepository(),
                new JdbcOperacionCuentaRepository()
        );

        initUI();
        cargarCuentasDelCliente();
    }

    private void initUI() {
        setSize(450, 380);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());
        setResizable(false);

        JPanel panelContenido = new JPanel();
        panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
        panelContenido.setBackground(Color.WHITE);
        panelContenido.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Resumen del Boleto
        JLabel lblTitulo = new JLabel("Resumen de Compra");
        lblTitulo.setFont(Theme.FONT_TITLE);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblFolio = new JLabel("Folio: " + boleto.getFolio());
        lblFolio.setFont(Theme.FONT_BODY);
        lblFolio.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblPrecio = new JLabel(String.format("Total a pagar: $%.2f MXN", boleto.getPrecio()));
        lblPrecio.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblPrecio.setForeground(Color.decode("#2563EB"));
        lblPrecio.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Combo de Cuentas
        JLabel lblSeleccionar = new JLabel("Selecciona el método de pago:");
        lblSeleccionar.setFont(Theme.FONT_BOLD);
        lblSeleccionar.setAlignmentX(Component.LEFT_ALIGNMENT);

        cbCuentas = new JComboBox<>();
        cbCuentas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        cbCuentas.setFont(Theme.FONT_BODY);
        cbCuentas.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelContenido.add(lblTitulo);
        panelContenido.add(Box.createRigidArea(new Dimension(0, 10)));
        panelContenido.add(lblFolio);
        panelContenido.add(Box.createRigidArea(new Dimension(0, 5)));
        panelContenido.add(lblPrecio);
        panelContenido.add(Box.createRigidArea(new Dimension(0, 20)));
        panelContenido.add(lblSeleccionar);
        panelContenido.add(Box.createRigidArea(new Dimension(0, 8)));
        panelContenido.add(cbCuentas);

        // Panel de Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        panelBotones.setBackground(Theme.CONTENT_BG);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(Theme.FONT_BODY);
        btnCancelar.addActionListener(e -> dispose());

        JButton btnPagar = new JButton("Confirmar Pago");
        btnPagar.setFont(Theme.FONT_BOLD);
        btnPagar.setForeground(Color.WHITE);
        btnPagar.setBackground(Color.decode("#10B981")); // Verde
        btnPagar.setFocusPainted(false);
        btnPagar.addActionListener(e -> ejecutarPago());

        panelBotones.add(btnCancelar);
        panelBotones.add(btnPagar);

        add(panelContenido, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarCuentasDelCliente() {
        try {
            List<CuentaCliente> cuentas = cuentaService.obtenerCuentasPorCliente(cliente.getId());
            cbCuentas.removeAllItems();

            for (CuentaCliente c : cuentas) {
                cbCuentas.addItem(new CuentaClienteWrapper(c));
            }

            if (cuentas.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No tienes cuentas bancarias registradas. Por favor agrega una en 'Cuentas bancarias'.",
                        "Sin cuentas", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar tarjetas: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ejecutarPago() {
        CuentaClienteWrapper wrapper = (CuentaClienteWrapper) cbCuentas.getSelectedItem();
        if (wrapper == null) {
            JOptionPane.showMessageDialog(this, "Selecciona una cuenta para pagar.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        CuentaCliente cuentaSeleccionada = wrapper.getCuenta();

        try {
            CompraRequestDTO dto = new CompraRequestDTO();
            dto.setIdCliente(cliente.getId());
            dto.setIdBoleto(boleto.getId());
            dto.setIdCuentaCliente(cuentaSeleccionada.getId());

            // Invocación a tu VentaService
            ventaService.procesarCompra(dto);

            compraExitosa = true;
            JOptionPane.showMessageDialog(this,
                    "¡Compra realizada con éxito!\nTu boleto digital ya está disponible en 'Mis boletos'.",
                    "¡Éxito!", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (InsufficientBalanceException ex) {
            JOptionPane.showMessageDialog(this,
                    "Saldo insuficiente en la cuenta de " + cuentaSeleccionada.getBanco() + ".\nSelecciona otra tarjeta o recarga saldo.",
                    "Saldo Insuficiente", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudo procesar la compra: " + ex.getMessage(),
                    "Error en Transacción", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isCompraExitosa() {
        return compraExitosa;
    }

    // Clase auxiliar para formatear la vista en el JComboBox
    private static class CuentaClienteWrapper {
        private final CuentaCliente cuenta;

        public CuentaClienteWrapper(CuentaCliente cuenta) {
            this.cuenta = cuenta;
        }

        public CuentaCliente getCuenta() {
            return cuenta;
        }

        @Override
        public String toString() {
            String ultimos4 = (cuenta.getNumeroCuenta() != null && cuenta.getNumeroCuenta().length() >= 4)
                    ? cuenta.getNumeroCuenta().substring(cuenta.getNumeroCuenta().length() - 4)
                    : "****";
            return String.format("%s (**** %s) - $%.2f MXN",
                    cuenta.getBanco() != null ? cuenta.getBanco() : "Banco",
                    ultimos4,
                    cuenta.getSaldo());
        }
    }
}