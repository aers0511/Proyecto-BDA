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

public class AdminClientesPanel extends JPanel {

    private JTable tablaClientes;
    private DefaultTableModel model;

    public AdminClientesPanel() {
        setLayout(new BorderLayout());
        setBackground(Theme.CONTENT_BG);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel lblTitle = new JLabel("👥 Control de Clientes y Usuarios");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(Theme.TEXT_DARK);

        // Columnas alineadas con el modelo Cliente: id, nombre, usuario, correo, activo, fecha_registro
        model = new DefaultTableModel(new String[]{"ID", "Nombre", "Usuario", "Correo", "Estado", "Fecha Registro"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaClientes = new JTable(model);
        tablaClientes.setRowHeight(32);

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelAcciones.setOpaque(false);

        JButton btnActivar = new JButton("✓ Activar Cliente");
        btnActivar.setBackground(new Color(22, 163, 74));
        btnActivar.setForeground(Color.WHITE);
        btnActivar.setFocusPainted(false);
        btnActivar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnActivar.addActionListener(e -> cambiarEstadoCliente(true));

        JButton btnBloquear = new JButton("🚫 Bloquear Cliente");
        btnBloquear.setBackground(new Color(220, 38, 38));
        btnBloquear.setForeground(Color.WHITE);
        btnBloquear.setFocusPainted(false);
        btnBloquear.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBloquear.addActionListener(e -> cambiarEstadoCliente(false));

        panelAcciones.add(btnActivar);
        panelAcciones.add(btnBloquear);

        add(lblTitle, BorderLayout.NORTH);
        add(new JScrollPane(tablaClientes), BorderLayout.CENTER);
        add(panelAcciones, BorderLayout.SOUTH);
    }

    public void cargarClientes() {
        model.setRowCount(0);
        // Consulta SQL con los campos reales del modelo Cliente
        String sql = "SELECT id, nombre, usuario, correo, COALESCE(activo, true) AS activo, fecha_registro " +
                     "FROM clientes ORDER BY id DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                boolean activo = rs.getBoolean("activo");
                model.addRow(new Object[]{
                        rs.getLong("id"),
                        rs.getString("nombre"),
                        rs.getString("usuario"),
                        rs.getString("correo"),                 // Corregido: antes 'email'
                        activo ? "Activo" : "Bloqueado",       // Manejo de la propiedad activo
                        rs.getTimestamp("fecha_registro")      // Corregido: agregada fechaRegistro
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar clientes: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cambiarEstadoCliente(boolean nuevoEstado) {
        int row = tablaClientes.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un cliente de la tabla.");
            return;
        }

        Long id = (Long) model.getValueAt(row, 0);
        String sql = "UPDATE clientes SET activo = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, nuevoEstado);
            ps.setLong(2, id);
            ps.executeUpdate();

            String estadoTexto = nuevoEstado ? "activado" : "bloqueado";
            JOptionPane.showMessageDialog(this, "Cliente " + estadoTexto + " correctamente.");
            cargarClientes();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al actualizar estado del cliente: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }
}