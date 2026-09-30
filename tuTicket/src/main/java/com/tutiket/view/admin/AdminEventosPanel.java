package com.tutiket.view.admin;

import com.tutiket.config.DatabaseConfig;
import com.tutiket.view.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminEventosPanel extends JPanel {

    private JTable tablaEventos;
    private DefaultTableModel model;

    public AdminEventosPanel() {
        setLayout(new BorderLayout());
        setBackground(Theme.CONTENT_BG);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel lblTitle = new JLabel("🛡 Moderación y Aprobación de Eventos");
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

        JButton btnPublicar = new JButton("✓ Aprobar y Publicar");
        btnPublicar.setBackground(new Color(37, 99, 235));
        btnPublicar.setForeground(Color.WHITE);
        btnPublicar.setFocusPainted(false);
        btnPublicar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPublicar.addActionListener(e -> actualizarEstadoEvento("PUBLICADO"));

        JButton btnCancelar = new JButton("🚫 Cancelar Evento");
        btnCancelar.setBackground(new Color(220, 38, 38));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.addActionListener(e -> actualizarEstadoEvento("CANCELADO"));

        panelBtns.add(btnPublicar);
        panelBtns.add(btnCancelar);

        add(lblTitle, BorderLayout.NORTH);
        add(new JScrollPane(tablaEventos), BorderLayout.CENTER);
        add(panelBtns, BorderLayout.SOUTH);
    }

    public void cargarEventos() {
        model.setRowCount(0);
        // Columnas corregidas: 'nombre' y 'fecha_hora' coincidiendo con Evento.java
        String sql = "SELECT e.id, e.nombre, COALESCE(p.nombre_empresa, 'Sin Promotora') as promotora, "
                + "e.fecha_hora, e.lugar, COALESCE(e.estado, 'PENDIENTE') as estado "
                + "FROM eventos e LEFT JOIN promotoras p ON e.id_promotora = p.id "
                + "ORDER BY e.fecha_hora DESC";

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getLong("id"),
                    rs.getString("nombre"), // Corregido: antes 'titulo'
                    rs.getString("promotora"),
                    rs.getTimestamp("fecha_hora"), // Corregido: antes 'fecha_evento'
                    rs.getString("lugar"),
                    rs.getString("estado")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar eventos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarEstadoEvento(String nuevoEstado) {
        int row = tablaEventos.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un evento de la lista.");
            return;
        }

        Long id = (Long) model.getValueAt(row, 0);
        String sql = "UPDATE eventos SET estado = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado);
            ps.setLong(2, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Estado del evento actualizado a: " + nuevoEstado);
            cargarEventos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al actualizar evento: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }
}
