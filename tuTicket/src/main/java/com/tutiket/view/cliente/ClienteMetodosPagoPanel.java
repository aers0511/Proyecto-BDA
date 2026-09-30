package com.tutiket.view.cliente;

import com.tutiket.config.DatabaseConfig;
import com.tutiket.domain.Cliente;
import com.tutiket.domain.CuentaCliente;
import com.tutiket.repository.impl.JdbcCuentaClienteRepository;
import com.tutiket.view.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

public class ClienteMetodosPagoPanel extends JPanel {

    private Cliente clienteLogueado;
    private JPanel panelCuentasContainer;

    public ClienteMetodosPagoPanel(Cliente cliente) {
        this.clienteLogueado = cliente;

        setLayout(new BorderLayout(20, 20));
        setBackground(Theme.CONTENT_BG);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // Header
        JPanel panelHeader = new JPanel(new GridLayout(2, 1));
        panelHeader.setOpaque(false);
        JLabel lblTitulo = new JLabel("Métodos de Pago y Saldos");
        lblTitulo.setFont(Theme.FONT_TITLE);
        JLabel lblSub = new JLabel("Consulta tus cuentas bancarias asociadas para realizar tus compras.");
        lblSub.setFont(Theme.FONT_BODY);
        lblSub.setForeground(Theme.TEXT_MUTED);
        panelHeader.add(lblTitulo);
        panelHeader.add(lblSub);

        add(panelHeader, BorderLayout.NORTH);

        // Contenedor de Tarjetas de Cuentas Bancarias
        panelCuentasContainer = new JPanel();
        panelCuentasContainer.setLayout(new BoxLayout(panelCuentasContainer, BoxLayout.Y_AXIS));
        panelCuentasContainer.setOpaque(false);

        JScrollPane scrollCuentas = new JScrollPane(panelCuentasContainer);
        scrollCuentas.setBorder(null);
        scrollCuentas.setOpaque(false);
        scrollCuentas.getViewport().setOpaque(false);

        add(scrollCuentas, BorderLayout.CENTER);

        cargarCuentasBancarias();
    }

    public void cargarCuentasBancarias() {
        panelCuentasContainer.removeAll();
        try (Connection conn = DatabaseConfig.getConnection()) {
            JdbcCuentaClienteRepository repo = new JdbcCuentaClienteRepository();
            List<CuentaCliente> cuentas = repo.buscarPorClienteId(conn, clienteLogueado.getId());

            if (cuentas.isEmpty()) {
                JLabel lblEmpty = new JLabel("No tienes cuentas bancarias registradas.");
                lblEmpty.setFont(Theme.FONT_BODY);
                lblEmpty.setForeground(Theme.TEXT_MUTED);
                panelCuentasContainer.add(lblEmpty);
            } else {
                for (CuentaCliente cta : cuentas) {
                    panelCuentasContainer.add(crearTarjetaCuenta(cta));
                    panelCuentasContainer.add(Box.createRigidArea(new Dimension(0, 15)));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        panelCuentasContainer.revalidate();
        panelCuentasContainer.repaint();
    }

    private JPanel crearTarjetaCuenta(CuentaCliente cta) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Theme.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));
        card.setMaximumSize(new Dimension(800, 100));

        JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
        panelInfo.setOpaque(false);

        JLabel lblCuentaNum = new JLabel("💳 Cuenta N°: " + cta.getNumeroCuenta());
        lblCuentaNum.setFont(Theme.FONT_SUBTITLE);

        JLabel lblSaldo = new JLabel("Saldo disponible: $" + cta.getSaldo() + " MXN");
        lblSaldo.setFont(Theme.FONT_BOLD);
        lblSaldo.setForeground(Theme.PRIMARY_BTN);

        panelInfo.add(lblCuentaNum);
        panelInfo.add(Box.createRigidArea(new Dimension(0, 5)));
        panelInfo.add(lblSaldo);

        JButton btnRecargar = new JButton("Ingresar Saldo");
        btnRecargar.setFont(Theme.FONT_BOLD);
        btnRecargar.setForeground(Color.WHITE);
        btnRecargar.setBackground(Theme.PRIMARY_BTN);
        btnRecargar.setFocusPainted(false);
        btnRecargar.addActionListener(e -> ingresarSaldoSimulado(cta));

        card.add(panelInfo, BorderLayout.CENTER);
        card.add(btnRecargar, BorderLayout.EAST);

        return card;
    }

    private void ingresarSaldoSimulado(CuentaCliente cta) {
        String input = JOptionPane.showInputDialog(this, "Ingresa el monto a depositar (MXN):", "Recargar Saldo", JOptionPane.QUESTION_MESSAGE);
        if (input != null && !input.trim().isEmpty()) {
            try {
                BigDecimal monto = new BigDecimal(input.trim());
                if (monto.compareTo(BigDecimal.ZERO) <= 0) {
                    JOptionPane.showMessageDialog(this, "Ingresa un monto válido mayor a 0.", "Monto Inválido", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try (Connection conn = DatabaseConfig.getConnection()) {
                    JdbcCuentaClienteRepository repo = new JdbcCuentaClienteRepository();
                    cta.setSaldo(cta.getSaldo().add(monto));
                    repo.actualizarSaldo(conn, cta.getId(), cta.getSaldo());
                    JOptionPane.showMessageDialog(this, "¡Depósito realizado correctamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarCuentasBancarias();
                }
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Monto no válido.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al actualizar saldo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}