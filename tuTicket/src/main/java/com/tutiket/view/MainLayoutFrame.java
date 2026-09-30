package com.tutiket.view;

import com.tutiket.view.admin.AdminClientesPanel;
import com.tutiket.domain.Administrador;
import com.tutiket.domain.Cliente;
import com.tutiket.domain.Promotora;
import com.tutiket.view.admin.*;
import com.tutiket.view.cliente.*;
import com.tutiket.view.promotora.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class MainLayoutFrame extends JFrame {

    private final JPanel panelContenidoCentral;
    private final CardLayout cardLayout;
    private final Object usuarioAutenticado;
    private final Map<String, JButton> menuButtonsMap = new HashMap<>();

    // Paneles por Rol
    private ClienteEventosPanel clienteEventosPanel;
    private ClienteBoletosPanel clienteBoletosPanel;
    private ClienteComprasPanel clienteComprasPanel;
    private ClienteCuentaPanel clienteCuentaPanel;

    private PromotoraEventosPanel promotoraEventosPanel;
    private PromotoraVentasPanel promotoraVentasPanel;

    private AdminReportesPanel adminReportesPanel;
    private AdminPromotorasPanel adminPromotorasPanel;
    private AdminEventosPanel adminEventosPanel;
    private AdminClientesPanel adminClientesPanel;

    public MainLayoutFrame(Object usuario) {
        this.usuarioAutenticado = usuario;

        setTitle("TuTiket - Panel General (" + obtenerRolUsuario() + ")");
        setSize(1300, 850);
        setMinimumSize(new Dimension(1024, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        panelContenidoCentral = new JPanel(cardLayout);
        panelContenidoCentral.setBackground(Theme.CONTENT_BG);

        JPanel sidebar = crearSidebar();
        add(sidebar, BorderLayout.WEST);

        // Registro dinámico de vistas según el rol
        if (usuarioAutenticado instanceof Cliente) {
            Cliente c = (Cliente) usuarioAutenticado;
            clienteEventosPanel = new ClienteEventosPanel(c, this);
            clienteBoletosPanel = new ClienteBoletosPanel(c);
            clienteComprasPanel = new ClienteComprasPanel(c);
            clienteCuentaPanel = new ClienteCuentaPanel(c);

            panelContenidoCentral.add(clienteEventosPanel, "EVENTOS_CLIENTE");
            panelContenidoCentral.add(clienteBoletosPanel, "MIS_BOLETOS");
            panelContenidoCentral.add(clienteComprasPanel, "MIS_COMPRAS");
            panelContenidoCentral.add(clienteCuentaPanel, "CUENTAS");
            panelContenidoCentral.add(new ClientePerfilPanel(c), "PERFIL");
            navegarA("EVENTOS_CLIENTE");

        } else if (usuarioAutenticado instanceof Promotora) {
            Promotora p = (Promotora) usuarioAutenticado;
            promotoraEventosPanel = new PromotoraEventosPanel(p.getId());
            promotoraVentasPanel = new PromotoraVentasPanel(p.getId());

            panelContenidoCentral.add(promotoraVentasPanel, "DASHBOARD_PROMOTORA");
            panelContenidoCentral.add(promotoraEventosPanel, "EVENTOS_PROMOTORA");
            navegarA("DASHBOARD_PROMOTORA");

        } else if (usuarioAutenticado instanceof Administrador) {
            adminReportesPanel = new AdminReportesPanel();
            adminPromotorasPanel = new AdminPromotorasPanel();
            adminEventosPanel = new AdminEventosPanel();
            adminClientesPanel = new AdminClientesPanel();

            panelContenidoCentral.add(adminReportesPanel, "ADMIN_REPORTES");
            panelContenidoCentral.add(adminPromotorasPanel, "ADMIN_PROMOTORAS");
            panelContenidoCentral.add(adminEventosPanel, "ADMIN_EVENTOS");
            panelContenidoCentral.add(adminClientesPanel, "ADMIN_CLIENTES");
            navegarA("ADMIN_REPORTES");
        }

        add(panelContenidoCentral, BorderLayout.CENTER);
    }

    private JPanel crearSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(270, 850));
        sidebar.setBackground(Theme.SIDEBAR_BG);
        sidebar.setBorder(new EmptyBorder(25, 15, 25, 15));

        JPanel panelTopSidebar = new JPanel();
        panelTopSidebar.setOpaque(false);
        panelTopSidebar.setLayout(new BoxLayout(panelTopSidebar, BoxLayout.Y_AXIS));

        JLabel lblLogo = new JLabel("🎟 TuTiket");
        lblLogo.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblLogo.setForeground(Color.WHITE);

        JPanel cardUser = new JPanel();
        cardUser.setLayout(new BoxLayout(cardUser, BoxLayout.Y_AXIS));
        cardUser.setBackground(Theme.SIDEBAR_HOVER);
        cardUser.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.SIDEBAR_ACTIVE, 1),
                new EmptyBorder(10, 12, 10, 12)
        ));
        cardUser.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel lblUserName = new JLabel("👤 " + obtenerNombreUsuario());
        lblUserName.setFont(Theme.FONT_BOLD);
        lblUserName.setForeground(Color.WHITE);

        JLabel lblUserRole = new JLabel(obtenerRolUsuario());
        lblUserRole.setFont(Theme.FONT_SMALL);
        lblUserRole.setForeground(Theme.TAG_BLUE_BG);

        cardUser.add(lblUserName);
        cardUser.add(Box.createRigidArea(new Dimension(0, 3)));
        cardUser.add(lblUserRole);

        panelTopSidebar.add(lblLogo);
        panelTopSidebar.add(Box.createRigidArea(new Dimension(0, 18)));
        panelTopSidebar.add(cardUser);
        panelTopSidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel menuNav = new JPanel();
        menuNav.setOpaque(false);
        menuNav.setLayout(new BoxLayout(menuNav, BoxLayout.Y_AXIS));

        if (usuarioAutenticado instanceof Cliente) {
            agregarBotonMenu(menuNav, "📅  Cartelera", "EVENTOS_CLIENTE");
            agregarBotonMenu(menuNav, "🎟  Mis Boletos", "MIS_BOLETOS");
            agregarBotonMenu(menuNav, "🛒  Historial Compras", "MIS_COMPRAS");
            agregarBotonMenu(menuNav, "💳  Cuentas Bancarias", "CUENTAS");
            agregarBotonMenu(menuNav, "👤  Perfil", "PERFIL");
        } else if (usuarioAutenticado instanceof Promotora) {
            agregarBotonMenu(menuNav, "📊  Métricas Ventas", "DASHBOARD_PROMOTORA");
            agregarBotonMenu(menuNav, "📅  Mis Eventos", "EVENTOS_PROMOTORA");
        } else if (usuarioAutenticado instanceof Administrador) {
            agregarBotonMenu(menuNav, "📊  Métricas Globales", "ADMIN_REPORTES");
            agregarBotonMenu(menuNav, "🏢  Aprobar Promotoras", "ADMIN_PROMOTORAS");
            agregarBotonMenu(menuNav, "🛡️  Moderación Eventos", "ADMIN_EVENTOS");
            agregarBotonMenu(menuNav, "👥  Gestión Clientes", "ADMIN_CLIENTES");
        }

        JButton btnLogout = new JButton("🚪  Cerrar Sesión");
        btnLogout.setFont(Theme.FONT_BOLD);
        btnLogout.setForeground(new Color(254, 202, 202));
        btnLogout.setBackground(Theme.SIDEBAR_BG);
        btnLogout.setOpaque(true);
        btnLogout.setContentAreaFilled(true);
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setHorizontalAlignment(SwingConstants.LEFT);
        btnLogout.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        btnLogout.addActionListener(e -> confirmarCierreSesion());

        sidebar.add(panelTopSidebar, BorderLayout.NORTH);
        sidebar.add(new JScrollPane(menuNav) {{ setBorder(null); setOpaque(false); getViewport().setOpaque(false); }}, BorderLayout.CENTER);
        sidebar.add(btnLogout, BorderLayout.SOUTH);

        return sidebar;
    }

    private void agregarBotonMenu(JPanel parent, String texto, String cardName) {
        JButton btn = new JButton(texto);
        btn.setFont(Theme.FONT_SUBTITLE);
        btn.setForeground(new Color(226, 232, 240));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBackground(Theme.SIDEBAR_BG);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> navegarA(cardName));
        menuButtonsMap.put(cardName, btn);

        parent.add(btn);
        parent.add(Box.createRigidArea(new Dimension(0, 4)));
    }

    public void navegarA(String vistaCard) {
        for (Map.Entry<String, JButton> entry : menuButtonsMap.entrySet()) {
            JButton btn = entry.getValue();
            if (entry.getKey().equals(vistaCard)) {
                btn.setBackground(Theme.SIDEBAR_ACTIVE);
                btn.setForeground(Color.WHITE);
                btn.setFont(Theme.FONT_BOLD);
            } else {
                btn.setBackground(Theme.SIDEBAR_BG);
                btn.setForeground(new Color(226, 232, 240));
                btn.setFont(Theme.FONT_SUBTITLE);
            }
        }

        if ("ADMIN_PROMOTORAS".equals(vistaCard) && adminPromotorasPanel != null) adminPromotorasPanel.cargarPromotoras();
        if ("ADMIN_EVENTOS".equals(vistaCard) && adminEventosPanel != null) adminEventosPanel.cargarEventos();
        if ("ADMIN_CLIENTES".equals(vistaCard) && adminClientesPanel != null) adminClientesPanel.cargarClientes();
        
        cardLayout.show(panelContenidoCentral, vistaCard);
    }

    private String obtenerNombreUsuario() {
        if (usuarioAutenticado instanceof Cliente) return ((Cliente) usuarioAutenticado).getNombre();
        if (usuarioAutenticado instanceof Promotora) return ((Promotora) usuarioAutenticado).getNombreEmpresa();
        if (usuarioAutenticado instanceof Administrador) return ((Administrador) usuarioAutenticado).getNombre();
        return "Usuario";
    }

    private String obtenerRolUsuario() {
        if (usuarioAutenticado instanceof Cliente) return "Cliente Registrado";
        if (usuarioAutenticado instanceof Promotora) return "Promotora Partner";
        if (usuarioAutenticado instanceof Administrador) return "Administrador General";
        return "Usuario";
    }

    private void confirmarCierreSesion() {
        if (JOptionPane.showConfirmDialog(this, "¿Deseas cerrar sesión?", "Cerrar Sesión", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            this.dispose();
        }
    }
}