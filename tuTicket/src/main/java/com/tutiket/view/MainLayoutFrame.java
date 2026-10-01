package com.tutiket.view;

import com.tutiket.domain.Administrador;
import com.tutiket.domain.Cliente;
import com.tutiket.domain.Promotora;
import com.tutiket.repository.CuentaPromotoraRepository;
import com.tutiket.repository.PromotoraRepository;
import com.tutiket.repository.impl.JdbcCuentaPromotoraRepository;
import com.tutiket.repository.impl.JdbcPromotoraRepository;
import com.tutiket.service.PromotoraService;
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

    // Paleta de Colores Estandarizada
    private static final Color COLOR_BASE_BG = new Color(30, 41, 59);        // Fondo normal
    private static final Color COLOR_HOVER_BG = new Color(51, 65, 85);       // Hover normal
    private static final Color COLOR_TEXT_MUTED = new Color(203, 213, 225);   // Texto inactivo
    private static final Color COLOR_TEXT_WHITE = Color.WHITE;               // Texto activo/destacado

    private static final Color COLOR_DANGER_RED_HOVER = new Color(185, 28, 28);

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
            // Inicializar las dependencias necesarias para AdminPromotorasPanel
            PromotoraRepository promotoraRepo = new JdbcPromotoraRepository();
            CuentaPromotoraRepository cuentaRepo = new JdbcCuentaPromotoraRepository();
            PromotoraService promotoraService = new PromotoraService(promotoraRepo, cuentaRepo);

            // Instanciación correcta de paneles
            adminReportesPanel = new AdminReportesPanel();
            adminPromotorasPanel = new AdminPromotorasPanel(promotoraService);
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
        cardUser.setBackground(COLOR_BASE_BG);
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
            agregarBotonMenu(menuNav, "🏢  Gestión Promotoras", "ADMIN_PROMOTORAS");
            agregarBotonMenu(menuNav, "🛡  Moderación Eventos", "ADMIN_EVENTOS");
            agregarBotonMenu(menuNav, "👥  Gestión Clientes", "ADMIN_CLIENTES");
        }

        // Botón Cerrar Sesión
        JButton btnLogout = crearBotonEstandar("🚪  Cerrar Sesión", COLOR_BASE_BG, new Color(248, 113, 113));
        btnLogout.setFont(Theme.FONT_BOLD);

        btnLogout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnLogout.setBackground(COLOR_DANGER_RED_HOVER);
                btnLogout.setForeground(COLOR_TEXT_WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnLogout.setBackground(COLOR_BASE_BG);
                btnLogout.setForeground(new Color(248, 113, 113));
            }
        });

        btnLogout.addActionListener(e -> confirmarCierreSesion());

        sidebar.add(panelTopSidebar, BorderLayout.NORTH);
        sidebar.add(new JScrollPane(menuNav) {
            {
                setBorder(null);
                setOpaque(false);
                getViewport().setOpaque(false);
            }
        }, BorderLayout.CENTER);
        sidebar.add(btnLogout, BorderLayout.SOUTH);

        return sidebar;
    }

    private JButton crearBotonEstandar(String texto, Color bg, Color fg) {
        JButton btn = new JButton(texto);
        btn.setFont(Theme.FONT_SUBTITLE);
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setPreferredSize(new Dimension(240, 42));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(0, 14, 0, 14));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        return btn;
    }

    private void agregarBotonMenu(JPanel parent, String texto, String cardName) {
        JButton btn = crearBotonEstandar(texto, COLOR_BASE_BG, COLOR_TEXT_MUTED);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!btn.getBackground().equals(Theme.SIDEBAR_ACTIVE)) {
                    btn.setBackground(COLOR_HOVER_BG);
                    btn.setForeground(COLOR_TEXT_WHITE);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!btn.getBackground().equals(Theme.SIDEBAR_ACTIVE)) {
                    btn.setBackground(COLOR_BASE_BG);
                    btn.setForeground(COLOR_TEXT_MUTED);
                }
            }
        });

        btn.addActionListener(e -> navegarA(cardName));
        menuButtonsMap.put(cardName, btn);

        parent.add(btn);
        parent.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    public void navegarA(String vistaCard) {
        for (Map.Entry<String, JButton> entry : menuButtonsMap.entrySet()) {
            JButton btn = entry.getValue();
            if (entry.getKey().equals(vistaCard)) {
                btn.setBackground(Theme.SIDEBAR_ACTIVE);
                btn.setForeground(COLOR_TEXT_WHITE);
                btn.setFont(Theme.FONT_BOLD);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 4, 0, 0, Color.WHITE),
                        new EmptyBorder(0, 10, 0, 14)
                ));
            } else {
                btn.setBackground(COLOR_BASE_BG);
                btn.setForeground(COLOR_TEXT_MUTED);
                btn.setFont(Theme.FONT_SUBTITLE);
                btn.setBorder(new EmptyBorder(0, 14, 0, 14));
            }
        }

        if ("ADMIN_PROMOTORAS".equals(vistaCard) && adminPromotorasPanel != null) {
            adminPromotorasPanel.cargarPromotoras();
        }
        if ("ADMIN_EVENTOS".equals(vistaCard) && adminEventosPanel != null) {
            adminEventosPanel.cargarEventos();
        }
        if ("ADMIN_CLIENTES".equals(vistaCard) && adminClientesPanel != null) {
            adminClientesPanel.cargarClientes();
        }

        cardLayout.show(panelContenidoCentral, vistaCard);
    }

    private String obtenerNombreUsuario() {
        if (usuarioAutenticado instanceof Cliente) {
            return ((Cliente) usuarioAutenticado).getNombre();
        }
        if (usuarioAutenticado instanceof Promotora) {
            return ((Promotora) usuarioAutenticado).getNombreEmpresa();
        }
        if (usuarioAutenticado instanceof Administrador) {
            return ((Administrador) usuarioAutenticado).getNombre();
        }
        return "Usuario";
    }

    private String obtenerRolUsuario() {
        if (usuarioAutenticado instanceof Cliente) {
            return "Cliente Registrado";
        }
        if (usuarioAutenticado instanceof Promotora) {
            return "Promotora Partner";
        }
        if (usuarioAutenticado instanceof Administrador) {
            return "Administrador General";
        }
        return "Usuario";
    }

    private void confirmarCierreSesion() {
        if (JOptionPane.showConfirmDialog(this, "¿Deseas cerrar sesión?", "Cerrar Sesión", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            this.dispose();
        }
    }
}