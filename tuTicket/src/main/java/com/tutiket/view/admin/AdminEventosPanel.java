package com.tutiket.view.admin;

import com.tutiket.domain.Evento;
import com.tutiket.repository.impl.JdbcBoletoRepository;
import com.tutiket.repository.impl.JdbcEventoRepository;
import com.tutiket.service.EventoService;
import com.tutiket.view.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminEventosPanel extends JPanel {

    private JTable tablaEventos;
    private DefaultTableModel model;
    private final EventoService eventoService;

    public AdminEventosPanel() {
        this.eventoService = new EventoService(
                new JdbcEventoRepository(),
                new JdbcBoletoRepository()
        );

        setLayout(new BorderLayout(0, 15));
        setBackground(Theme.CONTENT_BG);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel lblTitle = new JLabel("🛡 Control y Cancelación de Eventos");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(Theme.TEXT_DARK);

        model = new DefaultTableModel(new String[]{"ID", "Evento", "Promotora", "Fecha y Hora", "Lugar", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tablaEventos = new JTable(model);
        tablaEventos.setRowHeight(32);

        JPanel panelBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBtns.setOpaque(false);

        // Configuración de estilo corregida para el botón de Cancelar Evento
        JButton btnCancelar = new JButton("🚫 Cancelar Evento");
        btnCancelar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnCancelar.setBackground(new Color(220, 38, 38)); // Rojo
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setOpaque(true);
        btnCancelar.setBorderPainted(false);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.setPreferredSize(new Dimension(170, 38));

        btnCancelar.addActionListener(e -> cancelarEvento());

        panelBtns.add(btnCancelar);

        add(lblTitle, BorderLayout.NORTH);
        add(new JScrollPane(tablaEventos), BorderLayout.CENTER);
        add(panelBtns, BorderLayout.SOUTH);
    }

    public void cargarEventos() {
        model.setRowCount(0);
        try {
            List<Evento> eventos = eventoService.obtenerTodosLosEventos();
            for (Evento ev : eventos) {
                model.addRow(new Object[]{
                        ev.getId(),
                        ev.getNombre(),
                        ev.getImagenPath() != null ? ev.getImagenPath() : "Sin Promotora",
                        ev.getFechaHora(),
                        ev.getLugar(),
                        ev.getEstado() != null ? ev.getEstado().name() : "PENDIENTE"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar la lista de eventos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelarEvento() {
        int row = tablaEventos.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un evento de la lista.");
            return;
        }

        Long id = (Long) model.getValueAt(row, 0);
        String nombre = (String) model.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Estás seguro de que deseas cancelar el evento '" + nombre + "'?",
                "Confirmar Cancelación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                eventoService.cancelarEvento(id);
                JOptionPane.showMessageDialog(this, "Evento cancelado correctamente.");
                cargarEventos();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al cancelar el evento: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}