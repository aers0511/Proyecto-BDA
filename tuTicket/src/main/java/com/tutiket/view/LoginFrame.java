package com.tutiket.view;

import com.tutiket.config.DatabaseConfig;
import com.tutiket.domain.Administrador;
import com.tutiket.domain.Cliente;
import com.tutiket.domain.Promotora;
import com.tutiket.util.PasswordUtil;
import com.tutiket.view.promotora.PromotoraRegistroDialog;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginFrame extends JFrame {

    private JTextField txtUsuarioEmail;
    private JPasswordField txtPassword;

    public LoginFrame() {
        setTitle("TuTiket - Iniciar Sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 600);
        setResizable(false);
        setLocationRelativeTo(null);

        // Contenedor principal centrado
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(245, 247, 250));

        // Tarjeta del formulario de Login
        JPanel panelCard = new JPanel();
        panelCard.setLayout(new BoxLayout(panelCard, BoxLayout.Y_AXIS));
        panelCard.setBackground(Color.WHITE);
        panelCard.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(35, 40, 35, 40)
        ));
        panelCard.setPreferredSize(new Dimension(380, 510));

        // Logo y Encabezado
        JLabel lblLogo = new JLabel("🎟 TuTiket");
        lblLogo.setFont(new Font("SansSerif", Font.BOLD, 30));
        lblLogo.setForeground(Theme.PRIMARY_BTN);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Ingresa tus credenciales para continuar");
        lblSub.setFont(Theme.FONT_BODY);
        lblSub.setForeground(Theme.TEXT_MUTED);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Campos de texto estilizados
        txtUsuarioEmail = new JTextField();
        estilarCampoTexto(txtUsuarioEmail);

        txtPassword = new JPasswordField();
        estilarCampoTexto(txtPassword);

        // Botón Iniciar Sesión
        JButton btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setFont(Theme.FONT_BOLD);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBackground(Theme.PRIMARY_BTN);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnLogin.setBackground(Theme.PRIMARY_BTN.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnLogin.setBackground(Theme.PRIMARY_BTN);
            }
        });

        btnLogin.addActionListener(e -> autenticarUsuario());

        // Botón Registrar Promotora
        JButton btnRegistrarPromotora = new JButton("¿Eres Promotora? Regístrate aquí");
        btnRegistrarPromotora.setFont(Theme.FONT_SMALL);
        btnRegistrarPromotora.setForeground(Theme.PRIMARY_BTN);
        btnRegistrarPromotora.setContentAreaFilled(false);
        btnRegistrarPromotora.setBorderPainted(false);
        btnRegistrarPromotora.setFocusPainted(false);
        btnRegistrarPromotora.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegistrarPromotora.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegistrarPromotora.addActionListener(e -> abrirRegistroPromotora());

        // Ensamblado de la interfaz
        panelCard.add(lblLogo);
        panelCard.add(Box.createRigidArea(new Dimension(0, 6)));
        panelCard.add(lblSub);
        panelCard.add(Box.createRigidArea(new Dimension(0, 25)));

        panelCard.add(crearEtiquetaCampo("Correo Electrónico / Usuario"));
        panelCard.add(Box.createRigidArea(new Dimension(0, 6)));
        panelCard.add(txtUsuarioEmail);
        panelCard.add(Box.createRigidArea(new Dimension(0, 15)));

        panelCard.add(crearEtiquetaCampo("Contraseña"));
        panelCard.add(Box.createRigidArea(new Dimension(0, 6)));
        panelCard.add(txtPassword);
        panelCard.add(Box.createRigidArea(new Dimension(0, 25)));

        panelCard.add(btnLogin);
        panelCard.add(Box.createRigidArea(new Dimension(0, 15)));
        panelCard.add(btnRegistrarPromotora);

        mainPanel.add(panelCard);
        setContentPane(mainPanel);
    }

    private void estilarCampoTexto(JComponent campo) {
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        campo.setFont(Theme.FONT_BODY);
        campo.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(203, 213, 225), 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
    }

    private JLabel crearEtiquetaCampo(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(Theme.FONT_BOLD);
        lbl.setForeground(Theme.TEXT_DARK);
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

            // Credenciales no encontradas en ninguna tabla
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

    private void abrirRegistroPromotora() {
        PromotoraRegistroDialog dialog = new PromotoraRegistroDialog(this);
        dialog.setVisible(true);
    }
}