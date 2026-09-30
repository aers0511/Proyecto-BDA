package com.tutiket.view.admin;

import com.tutiket.config.DatabaseConfig;
import com.tutiket.view.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.math.BigDecimal;

public class AdminDashboardPanel extends JPanel {

    private JLabel lblTotalVentas;
    private JLabel lblEventosActivos;
    private JLabel lblTotalUsuarios;
    private JLabel lblComisiones;

    private JTable tablaActividad;
    private DefaultTableModel tableModel;

    public AdminDashboardPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(Theme.CONTENT_BG);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // 1. Encabezado
        JPanel panelHeader = new JPanel();
        panelHeader.setLayout(new BoxLayout(panelHeader, BoxLayout.Y_AXIS));
        panelHeader.setOpaque(false);

        JLabel lblTitulo = new JLabel("Panel de Control General");
        lblTitulo.setFont(Theme.FONT_TITLE);
        lblTitulo.setForeground(Theme.TEXT_DARK);

        JLabel lblSub = new JLabel("Visión general del rendimiento del sistema, métricas financieras y actividad reciente.");
        lblSub.setFont(Theme.FONT_BODY);
        lblSub.setForeground(Theme.TEXT_MUTED);

        panelHeader.add(lblTitulo);
        panelHeader.add(Box.createRigidArea(new Dimension(0, 4)));
        panelHeader.add(lblSub);

        add(panelHeader, BorderLayout.NORTH);

        // 2. Contenedor Central (KPIs + Tabla de Actividad)
        JPanel panelCentral = new JPanel(new BorderLayout(20, 20));
        panelCentral.setOpaque(false);

        // Grid de Tarjetas KPI (2x2 o 1x4)
        JPanel panelCards = new JPanel(new GridLayout(1, 4, 15, 0));
        panelCards.setOpaque(false);

        lblTotalVentas = new JLabel("$0.00 MXN");
        lblComisiones = new JLabel("$0.00 MXN");
        lblEventosActivos = new JLabel("0");
        lblTotalUsuarios = new JLabel("0");

        panelCards.add(crearKpiCard("VENTAS GLOBALES", lblTotalVentas, new Color(15, 23, 42), Color.WHITE));
        panelCards.add(crearKpiCard("COMISIONES RETENIDAS", lblComisiones, new Color(16, 185, 129), Color.WHITE));
        panelCards.add(crearKpiCard("EVENTOS ACTIVOS", lblEventosActivos, Color.WHITE, Theme.TEXT_DARK));
        panelCards.add(crearKpiCard("USUARIOS REGISTRADOS", lblTotalUsuarios, Color.WHITE, Theme.TEXT_DARK));

        panelCentral.add(panelCards, BorderLayout.NORTH);

        // Tabla de Compras / Actividad Reciente del Sistema
        JPanel cardTable = new JPanel(new BorderLayout(15, 15));
        cardTable.setBackground(Theme.CARD_BG);
        cardTable.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblTablaTitulo = new JLabel("Últimas Transacciones Registradas");
        lblTablaTitulo.setFont(Theme.FONT_BOLD);
        lblTablaTitulo.setForeground(Theme.TEXT_DARK);

        String[] columnas = {"ID Compra", "Cliente", "Fecha", "Monto Total", "Estado"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaActividad = new JTable(tableModel);
        tablaActividad.setFont(Theme.FONT_BODY);
        tablaActividad.setRowHeight(36);
        tablaActividad.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaActividad.setShowGrid(false);

        // Estilo del Header de la Tabla
        tablaActividad.getTableHeader().setFont(Theme.FONT_BOLD);
        tablaActividad.getTableHeader().setBackground(new Color(241, 245, 249));
        tablaActividad.getTableHeader().setForeground(Theme.TEXT_DARK);
        tablaActividad.getTableHeader().setPreferredSize(new Dimension(0, 38));
        tablaActividad.getTableHeader().setReorderingAllowed(false);

        configurarRenderizadorTabla();

        JScrollPane scrollTable = new JScrollPane(tablaActividad);
        scrollTable.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        scrollTable.getViewport().setBackground(Color.WHITE);

        cardTable.add(lblTablaTitulo, BorderLayout.NORTH);
        cardTable.add(scrollTable, BorderLayout.CENTER);

        panelCentral.add(cardTable, BorderLayout.CENTER);

        add(panelCentral, BorderLayout.CENTER);

        // Carga de datos
        cargarMetricas();
        cargarActividadReciente();
    }

    private JPanel crearKpiCard(String titulo, JLabel lblValor, Color bgColor, Color textColor) {
        JPanel card = new JPanel(new BorderLayout(8, 6));
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel lblTitle = new JLabel(titulo);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblTitle.setForeground(bgColor.equals(Color.WHITE) ? Theme.TEXT_MUTED : new Color(203, 213, 225));

        lblValor.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblValor.setForeground(textColor);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);

        return card;
    }

    private void configurarRenderizadorTabla() {
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (column == 0 || column == 2 || column == 4) {
                    setHorizontalAlignment(CENTER);
                } else if (column == 3) {
                    setHorizontalAlignment(RIGHT);
                } else {
                    setHorizontalAlignment(LEFT);
                }

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    c.setForeground(Theme.TEXT_DARK);
                }

                if (column == 4 && value != null) {
                    String estado = value.toString();
                    if ("COMPLETADA".equalsIgnoreCase(estado) || "PAGADO".equalsIgnoreCase(estado)) {
                        c.setForeground(new Color(22, 101, 52));
                    } else if ("CANCELADA".equalsIgnoreCase(estado)) {
                        c.setForeground(new Color(220, 38, 38));
                    }
                }

                return c;
            }
        };

        for (int i = 0; i < tablaActividad.getColumnCount(); i++) {
            tablaActividad.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
    }

    public void cargarMetricas() {
        try (Connection conn = DatabaseConfig.getConnection()) {
            // Total Ventas y Comisiones
            String queryVentas = "SELECT COALESCE(SUM(total), 0) AS total_ventas FROM compras WHERE estado = 'COMPLETADA'";
            try (PreparedStatement ps = conn.prepareStatement(queryVentas);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal totalVentas = rs.getBigDecimal("total_ventas");
                    lblTotalVentas.setText(String.format("$%,.2f MXN", totalVentas));
                    // Supuesto de comisión plataforma 10%
                    BigDecimal comisiones = totalVentas.multiply(new BigDecimal("0.10"));
                    lblComisiones.setText(String.format("$%,.2f MXN", comisiones));
                }
            }

            // Eventos Activos
            String queryEventos = "SELECT COUNT(*) AS total FROM eventos WHERE estado = 'ACTIVO'";
            try (PreparedStatement ps = conn.prepareStatement(queryEventos);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    lblEventosActivos.setText(String.valueOf(rs.getInt("total")));
                }
            }

            // Total Usuarios
            String queryUsuarios = "SELECT COUNT(*) AS total FROM usuarios";
            try (PreparedStatement ps = conn.prepareStatement(queryUsuarios);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    lblTotalUsuarios.setText(String.valueOf(rs.getInt("total")));
                }
            }

        } catch (Exception e) {
            lblTotalVentas.setText("$0.00 MXN");
            lblComisiones.setText("$0.00 MXN");
        }
    }

    public void cargarActividadReciente() {
        tableModel.setRowCount(0);
        String sql = "SELECT c.id, u.nombre, c.fecha_compra, c.total, c.estado " +
                     "FROM compras c " +
                     "LEFT JOIN usuarios u ON c.cliente_id = u.id " +
                     "ORDER BY c.fecha_compra DESC LIMIT 10";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Object[] fila = {
                        rs.getLong("id"),
                        rs.getString("nombre") != null ? rs.getString("nombre") : "Cliente #" + rs.getLong("id"),
                        rs.getTimestamp("fecha_compra") != null ? rs.getTimestamp("fecha_compra").toString() : "N/A",
                        String.format("$%,.2f MXN", rs.getBigDecimal("total")),
                        rs.getString("estado")
                };
                tableModel.addRow(fila);
            }
        } catch (Exception ex) {
            // Manejo silencioso o logger en vista previa
        }
    }
}