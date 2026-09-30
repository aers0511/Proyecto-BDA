package com.tutiket.view.admin;

import com.tutiket.domain.Administrador;
import com.tutiket.view.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AdminMainFrame extends JFrame {

    private final Administrador adminActual;
    private final CardLayout cardLayout;
    private final JPanel panelContenido;

    private JButton btnNavEventos;
    private JButton btnNavReportes;
    private JButton btnNavUsuarios;

    private AdminEventosPanel panelEventos;
    private AdminReportesPanel panelReportes;

    public AdminMainFrame(Administrador admin) {
        this.adminActual = admin;

        setTitle("TuTiket - Panel de Administración");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 768);
        setMinimumSize(new Dimension(1024, 600));
        setLocationRelativeTo(null);

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(Theme.CONTENT_BG);

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(15, 23, 42));
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBorder(new EmptyBorder(20, 15, 20, 15));

        JLabel lblLogo = new JLabel("TuTiket Admin");
        lblLogo.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setAlignmentX(Component.LEFT_ALIGNMENT);

        String nombreAdmin = (adminActual != null && adminActual.getNombre() != null) ? adminActual.getNombre() : "Administrador";
        JLabel lblUserRole = new JLabel("Hola, " + nombreAdmin);
        lblUserRole.setFont(Theme.FONT_BODY);
        lblUserRole.setForeground(new Color(148, 163, 184));
        lblUserRole.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(lblLogo);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        sidebar.add(lblUserRole);
        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));

        // Navegación
        btnNavEventos = crearBotonNav("Gestión Eventos");
        btnNavReportes = crearBotonNav("Reportes y KPIs");
        btnNavUsuarios = crearBotonNav("Gestión Cuentas");

        btnNavEventos.addActionListener(e -> mostrarPanel("EVENTOS"));
        btnNavReportes.addActionListener(e -> mostrarPanel("REPORTES"));
        btnNavUsuarios.addActionListener(e -> mostrarPanel("USUARIOS"));

        sidebar.add(btnNavEventos);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(btnNavReportes);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(btnNavUsuarios);

        sidebar.add(Box.createVerticalGlue());

        JButton btnLogout = new JButton("Cerrar Sesión");
        btnLogout.setFont(Theme.FONT_BOLD);
        btnLogout.setForeground(new Color(248, 113, 113));
        btnLogout.setBackground(new Color(30, 41, 59));
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnLogout.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Deseas cerrar la sesión de administración?",
                    "Cerrar Sesión",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
            }
        });

        sidebar.add(btnLogout);

        // Contenido Principal
        cardLayout = new CardLayout();
        panelContenido = new JPanel(cardLayout);
        panelContenido.setOpaque(false);

        panelEventos = new AdminEventosPanel();
        panelReportes = new AdminReportesPanel();

        panelContenido.add(panelEventos, "EVENTOS");
        panelContenido.add(panelReportes, "REPORTES");

        rootPanel.add(sidebar, BorderLayout.WEST);
        rootPanel.add(panelContenido, BorderLayout.CENTER);

        setContentPane(rootPanel);
        mostrarPanel("EVENTOS");
    }

    private JButton crearBotonNav(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(Theme.FONT_BOLD);
        btn.setForeground(new Color(203, 213, 225));
        btn.setBackground(new Color(15, 23, 42));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setBorder(new EmptyBorder(0, 12, 0, 12));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.getBackground().equals(new Color(15, 23, 42))) {
                    btn.setBackground(new Color(30, 41, 59));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!btn.getForeground().equals(Color.WHITE)) {
                    btn.setBackground(new Color(15, 23, 42));
                }
            }
        });

        return btn;
    }

    private void mostrarPanel(String nombreCard) {
        resetsBotonesNav();

        if ("EVENTOS".equals(nombreCard)) {
            marcarBotonActivo(btnNavEventos);
            panelEventos.cargarEventos();
        } else if ("REPORTES".equals(nombreCard)) {
            marcarBotonActivo(btnNavReportes);
            panelReportes.calcularMetricas();
        }

        cardLayout.show(panelContenido, nombreCard);
    }

    private void resetsBotonesNav() {
        Color baseBg = new Color(15, 23, 42);
        Color baseText = new Color(203, 213, 225);

        btnNavEventos.setBackground(baseBg);
        btnNavEventos.setForeground(baseText);

        btnNavReportes.setBackground(baseBg);
        btnNavReportes.setForeground(baseText);

        btnNavUsuarios.setBackground(baseBg);
        btnNavUsuarios.setForeground(baseText);
    }

    private void marcarBotonActivo(JButton btn) {
        btn.setBackground(Theme.PRIMARY_BTN);
        btn.setForeground(Color.WHITE);
    }
}