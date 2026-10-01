package com.tutiket.view.admin;

import com.tutiket.config.DatabaseConfig;
import com.tutiket.domain.Evento;
import com.tutiket.repository.impl.JdbcBoletoRepository;
import com.tutiket.repository.impl.JdbcEventoRepository;
import com.tutiket.view.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

public class AdminReportesPanel extends JPanel {

    private final JLabel lblTotalIngresos;
    private final JLabel lblTotalEventos;
    private final JLabel lblTotalVendidos;
    private final JTable tablaDesglose;
    private final DefaultTableModel tableModel;

    public AdminReportesPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(Theme.CONTENT_BG);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // Header Superior sin Botón de Actualizar
        JPanel panelHeader = new JPanel(new BorderLayout(15, 0));
        panelHeader.setOpaque(false);

        JPanel panelTextoHeader = new JPanel();
        panelTextoHeader.setLayout(new BoxLayout(panelTextoHeader, BoxLayout.Y_AXIS));
        panelTextoHeader.setOpaque(false);

        JLabel lblTitulo = new JLabel("Dashboard y Reportes");
        lblTitulo.setFont(Theme.FONT_TITLE);
        lblTitulo.setForeground(Theme.TEXT_DARK);

        JLabel lblSub = new JLabel("Resumen general de rendimiento, recaudación y boletaje del sistema.");
        lblSub.setFont(Theme.FONT_BODY);
        lblSub.setForeground(Theme.TEXT_MUTED);

        panelTextoHeader.add(lblTitulo);
        panelTextoHeader.add(Box.createRigidArea(new Dimension(0, 4)));
        panelTextoHeader.add(lblSub);

        panelHeader.add(panelTextoHeader, BorderLayout.CENTER);

        // Tarjetas KPI Grid
        JPanel gridKpis = new JPanel(new GridLayout(1, 3, 20, 0));
        gridKpis.setOpaque(false);

        lblTotalIngresos = new JLabel("$0.00 MXN");
        lblTotalEventos = new JLabel("0");
        lblTotalVendidos = new JLabel("0");

        gridKpis.add(crearCardMetric("INGRESOS TOTALES", lblTotalIngresos, Theme.TAG_GREEN_TEXT));
        gridKpis.add(crearCardMetric("EVENTOS ACTIVOS", lblTotalEventos, Theme.TAG_BLUE_TEXT));
        gridKpis.add(crearCardMetric("BOLETOS VENDIDOS", lblTotalVendidos, Theme.TAG_YELLOW_TEXT));

        // Panel Superior (Header + KPIs)
        JPanel panelTop = new JPanel(new BorderLayout(0, 20));
        panelTop.setOpaque(false);
        panelTop.add(panelHeader, BorderLayout.NORTH);
        panelTop.add(gridKpis, BorderLayout.SOUTH);

        add(panelTop, BorderLayout.NORTH);

        // Tabla de Desglose por Evento
        JPanel cardTabla = new JPanel(new BorderLayout(15, 15));
        cardTabla.setBackground(Theme.CARD_BG);
        cardTabla.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblTablaTitulo = new JLabel("Desglose de Rendimiento por Evento");
        lblTablaTitulo.setFont(Theme.FONT_SUBTITLE);
        lblTablaTitulo.setForeground(Theme.TEXT_DARK);

        String[] columnas = {"Evento", "Categoría", "Precio Base", "Total Boletos", "Vendidos", "Disponibles", "Total Recaudado"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaDesglose = new JTable(tableModel);
        tablaDesglose.setFont(Theme.FONT_BODY);
        tablaDesglose.setRowHeight(38);
        tablaDesglose.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaDesglose.setShowGrid(false);
        tablaDesglose.setIntercellSpacing(new Dimension(0, 0));

        tablaDesglose.getTableHeader().setFont(Theme.FONT_BOLD);
        tablaDesglose.getTableHeader().setBackground(new Color(241, 245, 249));
        tablaDesglose.getTableHeader().setForeground(Theme.TEXT_DARK);
        tablaDesglose.getTableHeader().setPreferredSize(new Dimension(0, 40));
        tablaDesglose.getTableHeader().setReorderingAllowed(false);

        configurarRenderizadorTabla();

        JScrollPane scrollTabla = new JScrollPane(tablaDesglose);
        scrollTabla.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        scrollTabla.getViewport().setBackground(Color.WHITE);

        cardTabla.add(lblTablaTitulo, BorderLayout.NORTH);
        cardTabla.add(scrollTabla, BorderLayout.CENTER);

        add(cardTabla, BorderLayout.CENTER);

        // Carga automática de datos al iniciar el componente
        calcularMetricas();
    }

    private JPanel crearCardMetric(String titulo, JLabel lblValor, Color textColor) {
        JPanel card = new JPanel();
        card.setBackground(Theme.CARD_BG);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(18, 20, 18, 20)
        ));

        JLabel lblTitle = new JLabel(titulo);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblTitle.setForeground(Theme.TEXT_MUTED);

        lblValor.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblValor.setForeground(textColor);

        card.add(lblTitle);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(lblValor);

        return card;
    }

    private void configurarRenderizadorTabla() {
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus,
                    int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Alineaciones
                if (column == 1) {
                    setHorizontalAlignment(CENTER);
                } else if (column >= 2) {
                    setHorizontalAlignment(RIGHT);
                } else {
                    setHorizontalAlignment(LEFT);
                }

                // Zebra Striping
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    c.setForeground(Theme.TEXT_DARK);
                } else {
                    c.setBackground(new Color(224, 231, 255));
                    c.setForeground(Theme.TEXT_DARK);
                }

                // Resaltar columna "Total Recaudado" (índice 6)
                if (column == 6 && !isSelected) {
                    c.setFont(Theme.FONT_BOLD);
                    c.setForeground(new Color(16, 185, 129));
                }

                return c;
            }
        };

        for (int i = 0; i < tablaDesglose.getColumnCount(); i++) {
            tablaDesglose.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
    }

    public void calcularMetricas() {
        tableModel.setRowCount(0);

        try (Connection conn = DatabaseConfig.getConnection()) {
            JdbcEventoRepository repoEv = new JdbcEventoRepository();
            JdbcBoletoRepository repoBol = new JdbcBoletoRepository();

            List<Evento> eventos = repoEv.listarActivos(conn);
            lblTotalEventos.setText(String.valueOf(eventos.size()));

            int totalVendidosContador = 0;
            BigDecimal ingresosTotalesAcumulados = BigDecimal.ZERO;

            for (Evento ev : eventos) {
                var boletosDisponibles = repoBol.buscarDisponiblesPorEvento(conn, ev.getId());

                int totalBoletos = ev.getCantidadBoletos();
                int numDisponibles = boletosDisponibles != null ? boletosDisponibles.size() : 0;
                int vendidosEv = Math.max(0, totalBoletos - numDisponibles);

                BigDecimal precioBase = ev.getPrecioBase() != null ? ev.getPrecioBase() : BigDecimal.ZERO;
                BigDecimal recaudadoEvento = precioBase.multiply(new BigDecimal(vendidosEv));

                totalVendidosContador += vendidosEv;
                ingresosTotalesAcumulados = ingresosTotalesAcumulados.add(recaudadoEvento);

                Object[] fila = {
                    ev.getNombre(),
                    ev.getCategoria() != null ? ev.getCategoria() : "Sin categoría",
                    String.format("$%,.2f MXN", precioBase),
                    String.format("%,d", totalBoletos),
                    String.format("%,d", vendidosEv),
                    String.format("%,d", numDisponibles),
                    String.format("$%,.2f MXN", recaudadoEvento)
                };
                tableModel.addRow(fila);
            }

            lblTotalVendidos.setText(String.format("%,d", totalVendidosContador));
            lblTotalIngresos.setText(String.format("$%,.2f MXN", ingresosTotalesAcumulados));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al calcular las métricas del sistema: " + ex.getMessage(),
                    "Error de Carga",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}