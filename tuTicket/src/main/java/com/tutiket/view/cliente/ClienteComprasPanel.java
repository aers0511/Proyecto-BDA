package com.tutiket.view.cliente;

import com.tutiket.config.DatabaseConfig;
import com.tutiket.domain.Boleto;
import com.tutiket.domain.Cliente;
import com.tutiket.domain.Compra;
import com.tutiket.domain.enums.EstadoCompra;
import com.tutiket.repository.impl.*;
import com.tutiket.service.CancelacionService;
import com.tutiket.view.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ClienteComprasPanel extends JPanel {

    private final Cliente clienteLogueado;
    private final JTable tablaCompras;
    private final DefaultTableModel tableModel;
    private final CancelacionService cancelacionService;

    public ClienteComprasPanel(Cliente cliente) {
        this.clienteLogueado = cliente;

        this.cancelacionService = new CancelacionService(
                new JdbcCompraRepository(),
                new JdbcBoletoRepository(),
                new JdbcCuentaClienteRepository(),
                new JdbcOperacionCuentaRepository()
        );

        setLayout(new BorderLayout(20, 20));
        setBackground(Theme.CONTENT_BG);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // Header Superior
        JPanel panelHeader = new JPanel();
        panelHeader.setLayout(new BoxLayout(panelHeader, BoxLayout.Y_AXIS));
        panelHeader.setOpaque(false);

        JLabel lblTitulo = new JLabel("Mis Compras");
        lblTitulo.setFont(Theme.FONT_TITLE);
        lblTitulo.setForeground(Theme.TEXT_DARK);

        JLabel lblSub = new JLabel("Revisa el historial de tus boletos adquiridos y gestiona devoluciones.");
        lblSub.setFont(Theme.FONT_BODY);
        lblSub.setForeground(Theme.TEXT_MUTED);

        panelHeader.add(lblTitulo);
        panelHeader.add(Box.createRigidArea(new Dimension(0, 4)));
        panelHeader.add(lblSub);

        add(panelHeader, BorderLayout.NORTH);

        // Contenedor Card de la Tabla
        JPanel cardTable = new JPanel(new BorderLayout(15, 15));
        cardTable.setBackground(Theme.CARD_BG);
        cardTable.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        String[] columnas = {"ID Compra", "Fecha Compra", "Monto Total", "Boleto(s)", "Estado"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaCompras = new JTable(tableModel);
        tablaCompras.setFont(Theme.FONT_BODY);
        tablaCompras.setRowHeight(38);
        tablaCompras.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaCompras.setShowGrid(false);
        tablaCompras.setIntercellSpacing(new Dimension(0, 0));

        // Estilo del Header de la Tabla
        tablaCompras.getTableHeader().setFont(Theme.FONT_BOLD);
        tablaCompras.getTableHeader().setBackground(new Color(241, 245, 249));
        tablaCompras.getTableHeader().setForeground(Theme.TEXT_DARK);
        tablaCompras.getTableHeader().setPreferredSize(new Dimension(0, 40));
        tablaCompras.getTableHeader().setReorderingAllowed(false);

        // Renderizado visual avanzado para filas y estados
        configurarRenderizadorTabla();

        JScrollPane scrollTable = new JScrollPane(tablaCompras);
        scrollTable.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        scrollTable.getViewport().setBackground(Color.WHITE);

        // Botón Solicitar Cancelación
        JButton btnCancelarCompra = new JButton("Solicitar Cancelación / Devolución");
        btnCancelarCompra.setFont(Theme.FONT_BOLD);
        btnCancelarCompra.setForeground(Color.WHITE);
        btnCancelarCompra.setBackground(new Color(220, 38, 38));
        btnCancelarCompra.setFocusPainted(false);
        btnCancelarCompra.setBorderPainted(false);
        btnCancelarCompra.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelarCompra.setBorder(new EmptyBorder(10, 18, 10, 18));

        btnCancelarCompra.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnCancelarCompra.setBackground(new Color(185, 28, 28));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnCancelarCompra.setBackground(new Color(220, 38, 38));
            }
        });

        btnCancelarCompra.addActionListener(e -> procesarCancelacion());

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelAcciones.setOpaque(false);
        panelAcciones.add(btnCancelarCompra);

        cardTable.add(scrollTable, BorderLayout.CENTER);
        cardTable.add(panelAcciones, BorderLayout.SOUTH);

        add(cardTable, BorderLayout.CENTER);

        cargarHistorialCompras();

        // Listener para recargar las compras automáticamente cada vez que la pantalla se vuelva visible
        addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                cargarHistorialCompras();
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

                // Alineación
                if (column == 0 || column == 1 || column == 4) {
                    setHorizontalAlignment(CENTER);
                } else {
                    setHorizontalAlignment(LEFT);
                }

                // Colores alternados
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    c.setForeground(Theme.TEXT_DARK);
                }

                // Resaltado de la columna Estado (índice 4)
                if (column == 4 && value != null) {
                    String estadoStr = value.toString();
                    if ("CANCELADA".equalsIgnoreCase(estadoStr)) {
                        c.setForeground(new Color(220, 38, 38));
                    } else if ("COMPLETADA".equalsIgnoreCase(estadoStr) || "PAGADO".equalsIgnoreCase(estadoStr)) {
                        c.setForeground(new Color(22, 101, 52));
                    }
                }

                return c;
            }
        };

        for (int i = 0; i < tablaCompras.getColumnCount(); i++) {
            tablaCompras.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
    }

    public void cargarHistorialCompras() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConfig.getConnection()) {
            JdbcCompraRepository repoCompra = new JdbcCompraRepository();
            JdbcBoletoRepository repoBoleto = new JdbcBoletoRepository();

            List<Compra> compras = repoCompra.buscarPorClienteId(conn, clienteLogueado.getId());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            for (Compra c : compras) {
                List<Boleto> boletos = repoBoleto.buscarPorCompra(conn, c.getId());

                String idBoletos;
                if (boletos.isEmpty()) {
                    idBoletos = (c.getEstado() == EstadoCompra.CANCELADA) ? "Devuelto / Liberado" : "N/A";
                } else {
                    idBoletos = boletos.stream()
                            .map(b -> String.valueOf(b.getId()))
                            .collect(Collectors.joining(", "));
                }

                String fechaFormateada = (c.getFechaCompra() != null) ? c.getFechaCompra().format(formatter) : "N/A";
                String montoFormateado = (c.getTotal() != null) ? String.format("$%,.2f MXN", c.getTotal()) : "$0.00 MXN";
                String estadoStr = (c.getEstado() != null) ? c.getEstado().name() : "COMPLETADA";

                Object[] fila = {
                    c.getId(),
                    fechaFormateada,
                    montoFormateado,
                    idBoletos,
                    estadoStr
                };
                tableModel.addRow(fila);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar el historial de compras: " + ex.getMessage(),
                    "Error de Carga",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void procesarCancelacion() {
        int filaSel = tablaCompras.getSelectedRow();
        if (filaSel == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona una compra de la tabla para solicitar la devolución.",
                    "Atención",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long idCompra = (Long) tableModel.getValueAt(filaSel, 0);
        String estadoActual = (String) tableModel.getValueAt(filaSel, 4);

        if (EstadoCompra.CANCELADA.name().equalsIgnoreCase(estadoActual)) {
            JOptionPane.showMessageDialog(this,
                    "Esta compra ya se encuentra en estado CANCELADA.",
                    "Atención",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas solicitar la devolución de la compra #" + idCompra + "?\n\nNota: El importe será abonado al saldo de tu cuenta.",
                "Confirmar Devolución",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                cancelacionService.solicitarCancelacion(idCompra);
                JOptionPane.showMessageDialog(this,
                        "La devolución fue procesada con éxito y el reembolso ha sido aplicado a tu saldo.",
                        "Devolución Exitosa",
                        JOptionPane.INFORMATION_MESSAGE);
                cargarHistorialCompras();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "No se pudo realizar la devolución: " + ex.getMessage(),
                        "Error de Devolución",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
