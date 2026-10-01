package com.tutiket.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.tutiket.config.DatabaseConfig;
import com.tutiket.domain.Administrador;
import com.tutiket.domain.Cliente;
import com.tutiket.domain.Promotora;
import com.tutiket.util.PasswordUtil;

public class LoginFrame extends JFrame {

    private JTextField txtUsuarioEmail;
    private JPasswordField txtPassword;

    // Paleta de colores refinada
    private static final Color COLOR_LEFT_BG = new Color(2, 132, 199);     // Azul claro vibrante (Sky 600)
    private static final Color COLOR_RIGHT_BG = new Color(248, 250, 252);   // Gris ultra claro (Slate 50)
    private static final Color COLOR_PRIMARY_BTN = new Color(2, 132, 199); // Azul primario vibrante (Sky 600)
    private static final Color COLOR_TEXT_DARK = new Color(30, 41, 59);     // Slate 800
    private static final Color COLOR_TEXT_MUTED = new Color(100, 116, 139); // Slate 500
    private static final Color COLOR_BORDER = new Color(226, 232, 240);    // Slate 200

    public LoginFrame() {
        setTitle("BoletosFan / TuTiket - Iniciar Sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setMinimumSize(new Dimension(850, 580));
        setLocationRelativeTo(null);

        // Panel Principal con split de 2 columnas
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        // -----------------------------------------------------------------
        // PANEL IZQUIERDO (Branding azul claro)
        // -----------------------------------------------------------------
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(COLOR_LEFT_BG);
        leftPanel.setBorder(new EmptyBorder(40, 50, 40, 50));

        // Header Superior Izquierdo (Logo e Isotipo)
        JLabel lblTopBrand = new JLabel("🎟 BoletosFan");
        lblTopBrand.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTopBrand.setForeground(Color.WHITE);

        // Centro Izquierdo (Título gigante y Slogan)
        JPanel leftCenterPanel = new JPanel();
        leftCenterPanel.setOpaque(false);
        leftCenterPanel.setLayout(new BoxLayout(leftCenterPanel, BoxLayout.Y_AXIS));

        JLabel lblHeroTitle = new JLabel("BoletosFan");
        lblHeroTitle.setFont(new Font("SansSerif", Font.BOLD, 40));
        lblHeroTitle.setForeground(Color.WHITE);
        lblHeroTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblHeroSub = new JLabel("<html>Compra boletos para los mejores eventos y espectáculos en México.</html>");
        lblHeroSub.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lblHeroSub.setForeground(new Color(224, 242, 254)); // Texto claro de alto contraste (Sky 100)
        lblHeroSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblHeroSub.setMaximumSize(new Dimension(320, 80));

        leftCenterPanel.add(Box.createVerticalGlue());
        leftCenterPanel.add(lblHeroTitle);
        leftCenterPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        leftCenterPanel.add(lblHeroSub);
        leftCenterPanel.add(Box.createVerticalGlue());

        leftPanel.add(lblTopBrand, BorderLayout.NORTH);
        leftPanel.add(leftCenterPanel, BorderLayout.CENTER);

        // -----------------------------------------------------------------
        // PANEL DERECHO (Formulario en tarjeta flotante)
        // -----------------------------------------------------------------
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(COLOR_RIGHT_BG);

        // Tarjeta blanca flotante del Login
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(35, 40, 35, 40)
        ));
        cardPanel.setMinimumSize(new Dimension(380, 460));
        cardPanel.setPreferredSize(new Dimension(400, 480));
        cardPanel.setMaximumSize(new Dimension(420, 500));

        // Título y Subtítulo
        JLabel lblTitle = new JLabel("Inicia sesión");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTitle.setForeground(COLOR_TEXT_DARK);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Ingresa tus credenciales para acceder a tu cuenta.");
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSub.setForeground(COLOR_TEXT_MUTED);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Campos de texto
        txtUsuarioEmail = new JTextField();
        estilarCampoTexto(txtUsuarioEmail);

        txtPassword = new JPasswordField();
        estilarCampoTexto(txtPassword);

        // Botón Iniciar Sesión (Entrar)
        JButton btnLogin = new JButton("Entrar");
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBackground(COLOR_PRIMARY_BTN);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnLogin.setBackground(COLOR_PRIMARY_BTN.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnLogin.setBackground(COLOR_PRIMARY_BTN);
            }
        });

        btnLogin.addActionListener(e -> autenticarUsuario());

        // Permitir iniciar sesión presionando Enter
        getRootPane().setDefaultButton(btnLogin);

        // Separador
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separator.setForeground(COLOR_BORDER);
        separator.setBackground(COLOR_BORDER);
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Enlace / Botón Registro
        JPanel panelRegister = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        panelRegister.setOpaque(false);
        panelRegister.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelRegister.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel lblQuestion = new JLabel("¿Aún no tienes cuenta?");
        lblQuestion.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblQuestion.setForeground(COLOR_TEXT_MUTED);

        JButton btnRegistrar = new JButton("Regístrate");
        btnRegistrar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnRegistrar.setForeground(COLOR_PRIMARY_BTN);
        btnRegistrar.setContentAreaFilled(false);
        btnRegistrar.setBorderPainted(false);
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegistrar.addActionListener(e -> abrirRegistro());

        panelRegister.add(lblQuestion);
        panelRegister.add(btnRegistrar);

        // Ensamblado dentro de la tarjeta
        cardPanel.add(lblTitle);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        cardPanel.add(lblSub);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 22)));

        cardPanel.add(crearEtiquetaCampo("Correo electrónico o usuario"));
        cardPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        cardPanel.add(txtUsuarioEmail);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 16)));

        cardPanel.add(crearEtiquetaCampo("Contraseña"));
        cardPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        cardPanel.add(txtPassword);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 24)));

        cardPanel.add(btnLogin);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        cardPanel.add(separator);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        cardPanel.add(panelRegister);

        rightPanel.add(cardPanel);

        // Agregar los dos paneles principales a la ventana
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        setContentPane(mainPanel);
    }

    private void estilarCampoTexto(JComponent campo) {
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        campo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);

        LineBorder normalBorder = new LineBorder(COLOR_BORDER, 1, true);
        LineBorder activeBorder = new LineBorder(COLOR_PRIMARY_BTN, 1, true);

        campo.setBorder(BorderFactory.createCompoundBorder(
                normalBorder,
                new EmptyBorder(6, 12, 6, 12)
        ));

        // Efecto visual al enfocar el campo
        campo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                        activeBorder,
                        new EmptyBorder(6, 12, 6, 12)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                        normalBorder,
                        new EmptyBorder(6, 12, 6, 12)
                ));
            }
        });
    }

    private JLabel crearEtiquetaCampo(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(COLOR_TEXT_DARK);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private void autenticarUsuario() {
        String inputUser = txtUsuarioEmail.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (inputUser.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor ingresa tu usuario/correo y contraseña.",
                    "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConfig.getConnection()) {

            // 1. Intentar Autenticar como CLIENTE
            Cliente cliente = buscarCliente(conn, inputUser, password);
            if (cliente != null) {
                JOptionPane.showMessageDialog(this,
                        "¡Bienvenido " + (cliente.getNombre() != null ? cliente.getNombre() : cliente.getUsuario()) + "!",
                        "Inicio de Sesión Correcto", JOptionPane.INFORMATION_MESSAGE);

                new MainLayoutFrame(cliente).setVisible(true);
                dispose();
                return;
            }

            // 2. Intentar Autenticar como PROMOTORA
            Promotora promotora = buscarPromotora(conn, inputUser, password);
            if (promotora != null) {
                JOptionPane.showMessageDialog(this,
                        "¡Bienvenido " + promotora.getNombreEmpresa() + "!",
                        "Inicio de Sesión Correcto", JOptionPane.INFORMATION_MESSAGE);

                new MainLayoutFrame(promotora).setVisible(true);
                dispose();
                return;
            }

            // 3. Intentar Autenticar como ADMINISTRADOR
            Administrador admin = buscarAdministrador(conn, inputUser, password);
            if (admin != null) {
                JOptionPane.showMessageDialog(this,
                        "¡Bienvenido Admin " + admin.getNombre() + "!",
                        "Inicio de Sesión Correcto", JOptionPane.INFORMATION_MESSAGE);

                new MainLayoutFrame(admin).setVisible(true);
                dispose();
                return;
            }

            // Credenciales no encontradas
            JOptionPane.showMessageDialog(this,
                    "Usuario o contraseña incorrectos.",
                    "Error de Autenticación", JOptionPane.ERROR_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error en la conexión con la base de datos: " + ex.getMessage(),
                    "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Cliente buscarCliente(Connection conn, String inputUser, String password) throws Exception {
        String sql = "SELECT * FROM clientes WHERE (correo = ? OR usuario = ?) LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, inputUser);
            ps.setString(2, inputUser);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String passHashGuardado = rs.getString("contrasena");
                    if (password.equals(passHashGuardado) || PasswordUtil.checkPassword(password, passHashGuardado)) {
                        Cliente c = new Cliente();
                        c.setId(rs.getLong("id"));
                        c.setNombre(rs.getString("nombre"));
                        c.setUsuario(rs.getString("usuario"));
                        c.setCorreo(rs.getString("correo"));
                        return c;
                    }
                }
            }
        }
        return null;
    }

    private Promotora buscarPromotora(Connection conn, String inputUser, String password) throws Exception {
        String sql = "SELECT * FROM promotoras WHERE (correo = ? OR usuario = ?) LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, inputUser);
            ps.setString(2, inputUser);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String passHashGuardado = rs.getString("contrasena");
                    if (password.equals(passHashGuardado) || PasswordUtil.checkPassword(password, passHashGuardado)) {
                        Promotora p = new Promotora();
                        p.setId(rs.getLong("id"));
                        p.setNombreEmpresa(rs.getString("nombre_empresa"));
                        p.setCorreo(rs.getString("correo"));
                        return p;
                    }
                }
            }
        }
        return null;
    }

    private Administrador buscarAdministrador(Connection conn, String inputUser, String password) throws Exception {
        String sql = "SELECT * FROM administradores WHERE (correo = ? OR usuario = ?) LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, inputUser);
            ps.setString(2, inputUser);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String passHashGuardado = rs.getString("contrasena");
                    if (password.equals(passHashGuardado) || PasswordUtil.checkPassword(password, passHashGuardado)) {
                        Administrador admin = new Administrador();
                        admin.setId(rs.getLong("id"));
                        admin.setNombre(rs.getString("nombre"));
                        admin.setUsuario(rs.getString("usuario"));
                        return admin;
                    }
                }
            }
        }
        return null;
    }

    private void abrirRegistro() {
        RegisterFrame registerFrame = new RegisterFrame(this);
        registerFrame.setVisible(true);
        this.setVisible(false);
    }
}