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

public class AdminPromotorasPanel extends JPanel {

    private JTable tablaPromotoras;
    private DefaultTableModel model;

    public AdminPromotorasPanel() {
        setLayout(new BorderLayout());
        setBackground(Theme.CONTENT_BG);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel lblTitle = new JLabel("🏢 Gestión y Aprobación de Promotoras");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(Theme.TEXT_DARK);

        // Columnas alineadas con el esquema real: id, nombre_empresa, rfc, correo, usuario, activo
        model = new DefaultTableModel(new String[]{"ID", "Empresa", "RFC", "Correo", "Usuario", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaPromotoras = new JTable(model);
        tablaPromotoras.setRowHeight(32);

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelAcciones.setOpaque(false);

        JButton btnAprobar = new JButton("✓ Activar / Aprobar");
        btnAprobar.setBackground(new Color(22, 163, 74));
        btnAprobar.setForeground(Color.WHITE);
        btnAprobar.setFocusPainted(false);
        btnAprobar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAprobar.addActionListener(e -> cambiarEstadoPromotora(true));

        JButton btnSuspender = new JButton("🚫 Suspender / Inactivar");
        btnSuspender.setBackground(new Color(220, 38, 38));
        btnSuspender.setForeground(Color.WHITE);
        btnSuspender.setFocusPainted(false);
        btnSuspender.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSuspender.addActionListener(e -> cambiarEstadoPromotora(false));

        panelAcciones.add(btnAprobar);
        panelAcciones.add(btnSuspender);

        add(lblTitle, BorderLayout.NORTH);
        add(new JScrollPane(tablaPromotoras), BorderLayout.CENTER);
        add(panelAcciones, BorderLayout.SOUTH);
    }

    public void cargarPromotoras() {
        model.setRowCount(0);
        // Consulta ajustada con las columnas reales de la tabla
        String sql = "SELECT id, nombre_empresa, rfc, correo, usuario, COALESCE(activo, true) AS activo FROM promotoras";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                boolean activo = rs.getBoolean("activo");
                model.addRow(new Object[]{
                        rs.getLong("id"),
                        rs.getString("nombre_empresa"),
                        rs.getString("rfc"),
                        rs.getString("correo"),
                        rs.getString("usuario"),
                        activo ? "Activo" : "Suspendido"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar promotoras: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cambiarEstadoPromotora(boolean nuevoEstado) {
        int row = tablaPromotoras.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una promotora de la tabla.");
            return;
        }

        Long id = (Long) model.getValueAt(row, 0);
        // Se actualiza el campo booleano 'activo'
        String sql = "UPDATE promotoras SET activo = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, nuevoEstado);
            ps.setLong(2, id);
            ps.executeUpdate();

            String estadoTexto = nuevoEstado ? "activada" : "suspendida";
            JOptionPane.showMessageDialog(this, "Promotora " + estadoTexto + " correctamente.");
            cargarPromotoras();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al actualizar estado: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }
}