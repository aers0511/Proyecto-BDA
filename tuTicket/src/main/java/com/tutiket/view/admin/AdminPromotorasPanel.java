package com.tutiket.view.admin;

import com.tutiket.domain.Promotora;
import com.tutiket.service.PromotoraService;
import com.tutiket.view.Theme;
import com.tutiket.view.promotora.PromotoraRegistroDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminPromotorasPanel extends JPanel {

    private final JTable tablaPromotoras;
    private final DefaultTableModel model;
    private final PromotoraService promotoraService;

    public AdminPromotorasPanel(PromotoraService promotoraService) {
        this.promotoraService = promotoraService;

        setLayout(new BorderLayout(0, 15));
        setBackground(Theme.CONTENT_BG);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel lblTitle = new JLabel("🏢 Gestión de Promotoras");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(Theme.TEXT_DARK);

        model = new DefaultTableModel(new String[]{"ID", "Empresa", "RFC", "Correo", "Usuario", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaPromotoras = new JTable(model);
        tablaPromotoras.setRowHeight(32);

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelAcciones.setOpaque(false);

        // Botón de acción: Registrar Promotora estilizado correctamente
        JButton btnRegistrar = new JButton("➕ Registrar Promotora");
        btnRegistrar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnRegistrar.setBackground(new Color(16, 185, 129)); // Verde Esmeralda
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setOpaque(true);
        btnRegistrar.setBorderPainted(false);
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegistrar.setPreferredSize(new Dimension(190, 38));

        btnRegistrar.addActionListener(e -> abrirDialogoRegistro());

        panelAcciones.add(btnRegistrar);

        add(lblTitle, BorderLayout.NORTH);
        add(new JScrollPane(tablaPromotoras), BorderLayout.CENTER);
        add(panelAcciones, BorderLayout.SOUTH);
    }

    public void cargarPromotoras() {
        model.setRowCount(0);
        try {
            List<Promotora> lista = promotoraService.listarTodas();
            for (Promotora p : lista) {
                model.addRow(new Object[]{
                    p.getId(),
                    p.getNombreEmpresa(),
                    p.getRfc(),
                    p.getCorreo(),
                    p.getUsuario(),
                    p.getActivo() != null && p.getActivo() ? "Activo" : "Inactivo"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar promotoras: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirDialogoRegistro() {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        Frame parentFrame = (parentWindow instanceof Frame) ? (Frame) parentWindow : null;

        // Se pasa únicamente el parentFrame
        PromotoraRegistroDialog dialog = new PromotoraRegistroDialog(parentFrame);
        dialog.setVisible(true);

        // Recargar la tabla automáticamente tras registrar
        cargarPromotoras();
    }
}