package com.tutiket.view.admin;

import com.tutiket.domain.Cliente;
import com.tutiket.repository.impl.JdbcClienteRepository;
import com.tutiket.repository.impl.JdbcCuentaClienteRepository;
import com.tutiket.service.ClienteService;
import com.tutiket.view.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminClientesPanel extends JPanel {

    private JTable tablaClientes;
    private DefaultTableModel model;
    private final ClienteService clienteService;

    public AdminClientesPanel() {
        // Inicialización del servicio con sus repositorios
        this.clienteService = new ClienteService(
                new JdbcClienteRepository(),
                new JdbcCuentaClienteRepository()
        );

        setLayout(new BorderLayout(0, 15));
        setBackground(Theme.CONTENT_BG);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel lblTitle = new JLabel("👥 Control de Clientes y Usuarios");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(Theme.TEXT_DARK);

        model = new DefaultTableModel(new String[]{"ID", "Nombre", "Usuario", "Correo", "Estado", "Fecha Registro"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaClientes = new JTable(model);
        tablaClientes.setRowHeight(32);

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelAcciones.setOpaque(false);

        // Configuración visual del botón para garantizar legibilidad
        JButton btnQuitar = new JButton("🗑 Quitar Cliente");
        btnQuitar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnQuitar.setBackground(new Color(220, 38, 38)); // Rojo prominente
        btnQuitar.setForeground(Color.WHITE);
        btnQuitar.setOpaque(true);
        btnQuitar.setBorderPainted(false);
        btnQuitar.setFocusPainted(false);
        btnQuitar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnQuitar.setPreferredSize(new Dimension(160, 38));

        btnQuitar.addActionListener(e -> eliminarCliente());

        panelAcciones.add(btnQuitar);

        add(lblTitle, BorderLayout.NORTH);
        add(new JScrollPane(tablaClientes), BorderLayout.CENTER);
        add(panelAcciones, BorderLayout.SOUTH);
    }

    public void cargarClientes() {
        model.setRowCount(0);
        try {
            List<Cliente> clientes = clienteService.obtenerTodosLosClientes();
            for (Cliente c : clientes) {
                // Muestra solo los clientes activos en el listado
                if (Boolean.TRUE.equals(c.getActivo())) {
                    model.addRow(new Object[]{
                            c.getId(),
                            c.getNombre(),
                            c.getUsuario(),
                            c.getCorreo(),
                            "Activo",
                            c.getFechaRegistro()
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar la lista de clientes: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarCliente() {
        int row = tablaClientes.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un cliente de la tabla.");
            return;
        }

        Long id = (Long) model.getValueAt(row, 0);
        String nombre = (String) model.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Estás seguro de que deseas dar de baja al cliente '" + nombre + "'?",
                "Confirmar Baja de Cliente",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean exito = clienteService.darDeBajaCliente(id);
                if (exito) {
                    JOptionPane.showMessageDialog(this, "Cliente dado de baja correctamente.");
                    cargarClientes();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al dar de baja al cliente: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}