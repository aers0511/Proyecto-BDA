package com.tutiket.view.cliente;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.tutiket.domain.Cliente;
import com.tutiket.domain.CuentaCliente;
import com.tutiket.repository.impl.JdbcCuentaClienteRepository;
import com.tutiket.repository.impl.JdbcOperacionCuentaRepository;
import com.tutiket.service.CuentaClienteService;
import com.tutiket.view.Theme;

public class ClienteCuentaPanel extends JPanel {

    private final Cliente clienteLogueado;
    private final CuentaClienteService cuentaService;
    private final JTable tablaCuentas;
    private final DefaultTableModel tableModel;
    private final JLabel lblSaldoTotal;

    public ClienteCuentaPanel(Cliente cliente) {
        this.clienteLogueado = cliente;
        this.cuentaService = new CuentaClienteService(
                new JdbcCuentaClienteRepository(),
                new JdbcOperacionCuentaRepository()
        );

        setLayout(new BorderLayout(20, 20));
        setBackground(Theme.CONTENT_BG);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // Header Superior y Card de Saldo Total
        JPanel panelTop = new JPanel(new BorderLayout(15, 15));
        panelTop.setOpaque(false);

        JPanel panelHeader = new JPanel();
        panelHeader.setLayout(new BoxLayout(panelHeader, BoxLayout.Y_AXIS));
        panelHeader.setOpaque(false);

        JLabel lblTitulo = new JLabel("Mis Cuentas Bancarias");
        lblTitulo.setFont(Theme.FONT_TITLE);
        lblTitulo.setForeground(Theme.TEXT_DARK);

        JLabel lblSub = new JLabel("Administra tus tarjetas asociadas y recarga fondos para la compra de boletos.");
        lblSub.setFont(Theme.FONT_BODY);
        lblSub.setForeground(Theme.TEXT_MUTED);

        panelHeader.add(lblTitulo);
        panelHeader.add(Box.createRigidArea(new Dimension(0, 4)));
        panelHeader.add(lblSub);

        // Tarjeta Resumen Saldo Total
        JPanel cardSaldo = new JPanel(new BorderLayout(8, 4));
        cardSaldo.setBackground(new Color(15, 23, 42)); // #0F172A
        cardSaldo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(30, 41, 59), 1),
                new EmptyBorder(16, 24, 16, 24)
        ));

        JLabel lblSaldoText = new JLabel("SALDO TOTAL DISPONIBLE");
        lblSaldoText.setFont(Theme.FONT_BOLD);
        lblSaldoText.setForeground(new Color(148, 163, 184)); // #94A3B8

        lblSaldoTotal = new JLabel("$0.00 MXN");
        lblSaldoTotal.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblSaldoTotal.setForeground(Color.WHITE);

        cardSaldo.add(lblSaldoText, BorderLayout.NORTH);
        cardSaldo.add(lblSaldoTotal, BorderLayout.CENTER);

        panelTop.add(panelHeader, BorderLayout.CENTER);
        panelTop.add(cardSaldo, BorderLayout.EAST);

        add(panelTop, BorderLayout.NORTH);

        // Contenedor Card de la Tabla
        JPanel cardTable = new JPanel(new BorderLayout(15, 15));
        cardTable.setBackground(Theme.CARD_BG);
        cardTable.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        String[] columnas = {"ID Cuenta", "Banco", "Número de Cuenta", "Saldo Disponible"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaCuentas = new JTable(tableModel);
        tablaCuentas.setFont(Theme.FONT_BODY);
        tablaCuentas.setRowHeight(38);
        tablaCuentas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaCuentas.setShowGrid(false);
        tablaCuentas.setIntercellSpacing(new Dimension(0, 0));

        // Estilo del Header
        tablaCuentas.getTableHeader().setFont(Theme.FONT_BOLD);
        tablaCuentas.getTableHeader().setBackground(new Color(241, 245, 249));
        tablaCuentas.getTableHeader().setForeground(Theme.TEXT_DARK);
        tablaCuentas.getTableHeader().setPreferredSize(new Dimension(0, 40));
        tablaCuentas.getTableHeader().setReorderingAllowed(false);

        // Renderizador personalizado de celdas
        configurarRenderizadorTabla();

        // Doble clic en una fila para recargar saldo directamente
        tablaCuentas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tablaCuentas.getSelectedRow() != -1) {
                    procesarRecarga();
                }
            }
        });

        JScrollPane scrollTable = new JScrollPane(tablaCuentas);
        scrollTable.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        scrollTable.getViewport().setBackground(Color.WHITE);

        // Botón Agregar Nueva Cuenta
        JButton btnAgregarCuenta = new JButton("+ Agregar Nueva Cuenta");
        btnAgregarCuenta.setFont(Theme.FONT_BOLD);
        btnAgregarCuenta.setForeground(Color.WHITE);
        btnAgregarCuenta.setBackground(new Color(16, 185, 129)); // #10B981
        btnAgregarCuenta.setFocusPainted(false);
        btnAgregarCuenta.setBorderPainted(false);
        btnAgregarCuenta.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAgregarCuenta.setBorder(new EmptyBorder(10, 18, 10, 18));

        btnAgregarCuenta.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnAgregarCuenta.setBackground(new Color(5, 150, 105));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnAgregarCuenta.setBackground(new Color(16, 185, 129));
            }
        });
        btnAgregarCuenta.addActionListener(e -> procesarNuevaCuenta());

        // Botón Recargar Saldo
        JButton btnRecargar = new JButton("Recargar Saldo");
        btnRecargar.setFont(Theme.FONT_BOLD);
        btnRecargar.setForeground(Color.WHITE);
        btnRecargar.setBackground(new Color(37, 99, 235)); // #2563EB
        btnRecargar.setFocusPainted(false);
        btnRecargar.setBorderPainted(false);
        btnRecargar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRecargar.setBorder(new EmptyBorder(10, 18, 10, 18));

        btnRecargar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnRecargar.setBackground(new Color(29, 78, 216));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnRecargar.setBackground(new Color(37, 99, 235));
            }
        });
        btnRecargar.addActionListener(e -> procesarRecarga());

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelAcciones.setOpaque(false);
        panelAcciones.add(btnAgregarCuenta);
        panelAcciones.add(btnRecargar);

        cardTable.add(scrollTable, BorderLayout.CENTER);
        cardTable.add(panelAcciones, BorderLayout.SOUTH);

        add(cardTable, BorderLayout.CENTER);

        cargarCuentas();

        // Listener para recargar las cuentas automáticamente cada vez que la pantalla se vuelva visible
        addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                cargarCuentas();
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
            }
        });
    }

    private void configurarRenderizadorTabla() {
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus,
                    int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Alineación por columna
                if (column == 0 || column == 2) {
                    setHorizontalAlignment(CENTER);
                } else if (column == 3) {
                    setHorizontalAlignment(RIGHT);
                } else {
                    setHorizontalAlignment(LEFT);
                }

                // Colores alternados para filas (zebra striping)
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    c.setForeground(Theme.TEXT_DARK);
                }

                // Resaltado de la columna Saldo (índice 3)
                if (column == 3 && !isSelected) {
                    c.setFont(Theme.FONT_BOLD);
                    c.setForeground(new Color(22, 101, 52));
                }

                return c;
            }
        };

        for (int i = 0; i < tablaCuentas.getColumnCount(); i++) {
            tablaCuentas.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
    }

    public void cargarCuentas() {
        tableModel.setRowCount(0);
        BigDecimal acumuladoTotal = BigDecimal.ZERO;

        try {
            List<CuentaCliente> cuentas = cuentaService.obtenerCuentasPorCliente(clienteLogueado.getId());
            for (CuentaCliente cc : cuentas) {
                BigDecimal saldo = cc.getSaldo() != null ? cc.getSaldo() : BigDecimal.ZERO;
                acumuladoTotal = acumuladoTotal.add(saldo);

                Object[] fila = {
                    cc.getId(),
                    cc.getBanco() != null && !cc.getBanco().trim().isEmpty() ? cc.getBanco() : "Banco TuTiket",
                    enmascararCuenta(cc.getNumeroCuenta()),
                    String.format("$%,.2f MXN", saldo)
                };
                tableModel.addRow(fila);
            }

            lblSaldoTotal.setText(String.format("$%,.2f MXN", acumuladoTotal));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al obtener las cuentas: " + ex.getMessage(),
                    "Error de Carga",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void procesarNuevaCuenta() {
        String[] bancosDisponibles = {"BBVA", "Banamex", "Banorte", "Santander", "HSBC", "Nu", "Mercado Pago"};

        String bancoSeleccionado = (String) JOptionPane.showInputDialog(
                this,
                "Selecciona el banco para la nueva cuenta:",
                "Agregar Cuenta Bancaria",
                JOptionPane.QUESTION_MESSAGE,
                null,
                bancosDisponibles,
                bancosDisponibles[0]
        );

        if (bancoSeleccionado == null) {
            return;
        }

        String inputSaldo = JOptionPane.showInputDialog(
                this,
                "Ingresa el saldo inicial para la cuenta (monto positivo):",
                "Apertura de Cuenta",
                JOptionPane.QUESTION_MESSAGE
        );

        if (inputSaldo != null && !inputSaldo.trim().isEmpty()) {
            try {
                BigDecimal saldoInicial = new BigDecimal(inputSaldo.trim());
                if (saldoInicial.compareTo(BigDecimal.ZERO) < 0) {
                    JOptionPane.showMessageDialog(this,
                            "El saldo inicial no puede ser negativo.",
                            "Monto Inválido",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                cuentaService.registrarNuevaCuenta(clienteLogueado.getId(), bancoSeleccionado, saldoInicial);

                JOptionPane.showMessageDialog(this,
                        "¡Cuenta bancaria creada exitosamente!",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                cargarCuentas();

            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this,
                        "Por favor ingresa un monto numérico válido.",
                        "Monto Inválido",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error al crear la cuenta: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void procesarRecarga() {
        int filaSel = tablaCuentas.getSelectedRow();
        if (filaSel == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona una cuenta de la tabla para abonar saldo.",
                    "Atención",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long idCuenta = (Long) tableModel.getValueAt(filaSel, 0);
        String banco = (String) tableModel.getValueAt(filaSel, 1);

        String inputMonto = JOptionPane.showInputDialog(
                this,
                "Ingresa el monto a recargar en tu cuenta de " + banco + " (mayor a $0):",
                "Recargar Fondos",
                JOptionPane.QUESTION_MESSAGE
        );

        if (inputMonto != null && !inputMonto.trim().isEmpty()) {
            try {
                BigDecimal monto = new BigDecimal(inputMonto.trim());
                if (monto.compareTo(BigDecimal.ZERO) <= 0) {
                    JOptionPane.showMessageDialog(this,
                            "El monto a recargar debe ser mayor a $0.00.",
                            "Monto Inválido",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                cuentaService.recargarSaldo(idCuenta, monto);

                JOptionPane.showMessageDialog(this,
                        "¡Recarga completada con éxito!",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                cargarCuentas();

            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this,
                        "Ingresa una cantidad numérica válida.",
                        "Monto Inválido",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "No se pudo realizar la recarga: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String enmascararCuenta(String numCuenta) {
        if (numCuenta == null || numCuenta.trim().isEmpty()) {
            return "**** **** **** ****";
        }
        String limpia = numCuenta.replaceAll("\\s+", "");
        if (limpia.length() < 4) {
            return "**** " + limpia;
        }
        return "**** **** **** " + limpia.substring(limpia.length() - 4);
    }
}
