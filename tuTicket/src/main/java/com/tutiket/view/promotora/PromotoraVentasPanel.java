package com.tutiket.view.promotora;

import com.tutiket.config.DatabaseConfig;
import com.tutiket.domain.Boleto;
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

public class PromotoraVentasPanel extends JPanel {

    private final JLabel lblTotalIngresos;
    private final JLabel lblTotalVendidos;
    private final JTable tablaResumen;
    private final DefaultTableModel tableModel;
    private final Long idPromotora;

    public PromotoraVentasPanel(Long idPromotora) {
        this.idPromotora = idPromotora;

        setLayout(new BorderLayout(20, 20));
        setBackground(Theme.CONTENT_BG);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // Header Superior
        JPanel panelHeader = new JPanel(new GridLayout(2, 1));
        panelHeader.setOpaque(false);

        JLabel lblTitulo = new JLabel("Métricas y Ventas - Promotora");
        lblTitulo.setFont(Theme.FONT_TITLE);

        JLabel lblSub = new JLabel("Resumen de ingresos y disponibilidad de tus eventos publicados.");
        lblSub.setFont(Theme.FONT_BODY);
        lblSub.setForeground(Theme.TEXT_MUTED);

        panelHeader.add(lblTitulo);
        panelHeader.add(lblSub);

        add(panelHeader, BorderLayout.NORTH);

        // Subpanel de KPIs
        JPanel panelCards = new JPanel(new GridLayout(1, 2, 20, 0));
        panelCards.setOpaque(false);

        lblTotalIngresos = new JLabel("$0.00 MXN");
        lblTotalIngresos.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTotalIngresos.setForeground(Theme.PRIMARY_BTN);

        lblTotalVendidos = new JLabel("0 boletos");
        lblTotalVendidos.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTotalVendidos.setForeground(Theme.TAG_GREEN_TEXT);

        panelCards.add(crearKpiCard("Ingresos Totales Recaudados", lblTotalIngresos));
        panelCards.add(crearKpiCard("Boletos Vendidos Totales", lblTotalVendidos));

        // Tabla de Desglose
        String[] columnas = {"ID Evento", "Nombre del Evento", "Precio Base", "Vendidos", "Disponibles", "Recaudación"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaResumen = new JTable(tableModel);
        tablaResumen.setFont(Theme.FONT_BODY);
        tablaResumen.setRowHeight(36);
        tablaResumen.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaResumen.setShowGrid(false);
        tablaResumen.setIntercellSpacing(new Dimension(0, 0));

        tablaResumen.getTableHeader().setFont(Theme.FONT_BOLD);
        tablaResumen.getTableHeader().setBackground(new Color(241, 245, 249));
        tablaResumen.getTableHeader().setForeground(Theme.TEXT_DARK);
        tablaResumen.getTableHeader().setPreferredSize(new Dimension(0, 40));
        tablaResumen.getTableHeader().setReorderingAllowed(false);

        configurarRenderizadorTabla();

        JScrollPane scrollTabla = new JScrollPane(tablaResumen);
        scrollTabla.getViewport().setBackground(Color.WHITE);
        scrollTabla.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));

        JPanel panelCentro = new JPanel(new BorderLayout(0, 15));
        panelCentro.setOpaque(false);
        panelCentro.add(panelCards, BorderLayout.NORTH);
        panelCentro.add(scrollTabla, BorderLayout.CENTER);

        add(panelCentro, BorderLayout.CENTER);

        cargarMetricas();
    }

    private void configurarRenderizadorTabla() {
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus,
                    int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (column == 0) {
                    setHorizontalAlignment(CENTER);
                } else if (column >= 2) {
                    setHorizontalAlignment(RIGHT);
                } else {
                    setHorizontalAlignment(LEFT);
                }

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    c.setForeground(Theme.TEXT_DARK);
                } else {
                    c.setBackground(new Color(224, 231, 255));
                    c.setForeground(Theme.TEXT_DARK);
                }

                if (column == 5 && !isSelected) {
                    c.setFont(Theme.FONT_BOLD);
                    c.setForeground(new Color(16, 185, 129));
                }

                return c;
            }
        };

        for (int i = 0; i < tablaResumen.getColumnCount(); i++) {
            tablaResumen.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
    }

    public void cargarMetricas() {
        tableModel.setRowCount(0);
        BigDecimal ingresosGlobales = BigDecimal.ZERO;
        int vendidosGlobales = 0;

        try (Connection conn = DatabaseConfig.getConnection()) {
            JdbcEventoRepository repoEv = new JdbcEventoRepository();
            JdbcBoletoRepository repoBol = new JdbcBoletoRepository();

            List<Evento> eventos = (idPromotora != null)
                    ? repoEv.listarPorPromotora(conn, idPromotora)
                    : repoEv.listarActivos(conn);

            for (Evento ev : eventos) {
                List<Boleto> boletosDisp = repoBol.buscarDisponiblesPorEvento(conn, ev.getId());

                int totalCreados = ev.getCantidadBoletos();
                int disponibles = boletosDisp != null ? boletosDisp.size() : 0;
                int vendidos = Math.max(0, totalCreados - disponibles);

                BigDecimal precioBase = ev.getPrecioBase() != null ? ev.getPrecioBase() : BigDecimal.ZERO;
                BigDecimal recaudadoEvento = precioBase.multiply(new BigDecimal(vendidos));

                ingresosGlobales = ingresosGlobales.add(recaudadoEvento);
                vendidosGlobales += vendidos;

                tableModel.addRow(new Object[]{
                    ev.getId(),
                    ev.getNombre(),
                    String.format("$%,.2f MXN", precioBase),
                    String.format("%,d", vendidos),
                    String.format("%,d", disponibles),
                    String.format("$%,.2f MXN", recaudadoEvento)
                });
            }

            lblTotalIngresos.setText(String.format("$%,.2f MXN", ingresosGlobales));
            lblTotalVendidos.setText(String.format("%,d boletos", vendidosGlobales));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar las métricas de la promotora: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel crearKpiCard(String titulo, JLabel lblValor) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(18, 20, 18, 20)
        ));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(Theme.FONT_SMALL);
        lblTit.setForeground(Theme.TEXT_MUTED);

        card.add(lblTit, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);

        return card;
    }
}
