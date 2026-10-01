package com.tutiket.view.promotora;

import com.tutiket.config.DatabaseConfig;
import com.tutiket.domain.Boleto;
import com.tutiket.domain.Evento;
import com.tutiket.repository.impl.JdbcBoletoRepository;
import com.tutiket.repository.impl.JdbcEventoRepository;
import com.tutiket.view.Theme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
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
        JPanel panelHeader = new JPanel();
        panelHeader.setLayout(new BoxLayout(panelHeader, BoxLayout.Y_AXIS));
        panelHeader.setOpaque(false);

        JLabel lblTitulo = new JLabel("Métricas y Ventas - Promotora");
        lblTitulo.setFont(Theme.FONT_TITLE);
        lblTitulo.setForeground(Theme.TEXT_DARK);

        JLabel lblSub = new JLabel("Resumen de ingresos y disponibilidad de tus eventos publicados.");
        lblSub.setFont(Theme.FONT_BODY);
        lblSub.setForeground(Theme.TEXT_MUTED);

        panelHeader.add(lblTitulo);
        panelHeader.add(Box.createRigidArea(new Dimension(0, 4)));
        panelHeader.add(lblSub);

        // Subpanel de KPIs
        JPanel panelCards = new JPanel(new GridLayout(1, 2, 20, 0));
        panelCards.setOpaque(false);

        lblTotalIngresos = new JLabel("$0.00 MXN");
        lblTotalIngresos.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTotalIngresos.setForeground(Theme.TAG_GREEN_TEXT);

        lblTotalVendidos = new JLabel("0 boletos");
        lblTotalVendidos.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTotalVendidos.setForeground(Theme.TAG_BLUE_TEXT);

        panelCards.add(crearKpiCard("INGRESOS TOTALES RECAUDADOS", lblTotalIngresos));
        panelCards.add(crearKpiCard("BOLETOS VENDIDOS TOTALES", lblTotalVendidos));

        // Header + KPIs agrupados en la sección superior
        JPanel panelTop = new JPanel(new BorderLayout(0, 20));
        panelTop.setOpaque(false);
        panelTop.add(panelHeader, BorderLayout.NORTH);
        panelTop.add(panelCards, BorderLayout.SOUTH);

        add(panelTop, BorderLayout.NORTH);

        // Contenedor Card de la Tabla de Desglose
        JPanel cardTabla = new JPanel(new BorderLayout(15, 15));
        cardTabla.setBackground(Theme.CARD_BG);
        cardTabla.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblTablaTitulo = new JLabel("Desglose de Rendimiento por Evento");
        lblTablaTitulo.setFont(Theme.FONT_SUBTITLE);
        lblTablaTitulo.setForeground(Theme.TEXT_DARK);

        String[] columnas = {"ID Evento", "Nombre del Evento", "Precio Base", "Vendidos", "Disponibles", "Recaudación"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaResumen = new JTable(tableModel);
        tablaResumen.setFont(Theme.FONT_BODY);
        tablaResumen.setRowHeight(38);
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

        cardTabla.add(lblTablaTitulo, BorderLayout.NORTH);
        cardTabla.add(scrollTabla, BorderLayout.CENTER);

        add(cardTabla, BorderLayout.CENTER);

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
        JPanel card = new JPanel();
        card.setBackground(Theme.CARD_BG);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(18, 20, 18, 20)
        ));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblTit.setForeground(Theme.TEXT_MUTED);

        card.add(lblTit);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(lblValor);

        return card;
    }
}
