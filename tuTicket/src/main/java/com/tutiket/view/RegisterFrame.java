package com.tutiket.view;

import com.tutiket.config.DatabaseConfig;
import com.tutiket.domain.Cliente;
import com.tutiket.repository.impl.JdbcClienteRepository;
import com.tutiket.repository.impl.JdbcCuentaClienteRepository;
import com.tutiket.repository.impl.JdbcOperacionCuentaRepository;
import com.tutiket.service.CuentaClienteService;
import com.tutiket.util.PasswordUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.sql.Connection;

public class RegisterFrame extends JFrame {

    private JTextField txtNombreCompleto;
    private JTextField txtUsuario;
    private JTextField txtCorreo;
    private JPasswordField txtContrasena;
    private JButton btnRegistrar;
    private final JFrame parentLoginFrame;

    public RegisterFrame(JFrame parent) {
        this.parentLoginFrame = parent;
        initUI();
    }

    private void initUI() {
        setTitle("BoletosFan - Crear Cuenta");
        setSize(980, 640);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2));

        // Regresar al Login si el usuario cierra con la 'X'
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                volverAlLogin();
            }
        });

        // --- PANEL IZQUIERDO (Hero & Branding) ---
        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.setBackground(Theme.SIDEBAR_BG);
        panelIzquierdo.setBorder(new EmptyBorder(45, 45, 45, 45));

        JLabel lblLogoHead = new JLabel("🎟 BoletosFan");
        lblLogoHead.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblLogoHead.setForeground(Color.WHITE);

        JPanel panelCentroIzq = new JPanel();
        panelCentroIzq.setOpaque(false);
        panelCentroIzq.setLayout(new BoxLayout(panelCentroIzq, BoxLayout.Y_AXIS));

        JLabel lblHeroTitle = new JLabel("Únete a BoletosFan");
        lblHeroTitle.setFont(new Font("SansSerif", Font.BOLD, 34));
        lblHeroTitle.setForeground(Color.WHITE);
        lblHeroTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblHeroSub = new JLabel("<html>Crea tu cuenta para acceder a la compra de boletos para los mejores eventos en México.</html>");
        lblHeroSub.setFont(new Font("SansSerif", Font.PLAIN, 15));
        lblHeroSub.setForeground(new Color(210, 225, 245));
        lblHeroSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelCentroIzq.add(Box.createVerticalGlue());
        panelCentroIzq.add(lblHeroTitle);
        panelCentroIzq.add(Box.createRigidArea(new Dimension(0, 12)));
        panelCentroIzq.add(lblHeroSub);
        panelCentroIzq.add(Box.createVerticalGlue());

        panelIzquierdo.add(lblLogoHead, BorderLayout.NORTH);
        panelIzquierdo.add(panelCentroIzq, BorderLayout.CENTER);

        // --- PANEL DERECHO (Tarjeta de Formulario) ---
        JPanel panelDerecho = new JPanel(new GridBagLayout());
        panelDerecho.setBackground(Theme.CONTENT_BG);

        JPanel cardForm = new JPanel();
        cardForm.setBackground(Color.WHITE);
        cardForm.setLayout(new BoxLayout(cardForm, BoxLayout.Y_AXIS));
        cardForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(30, 35, 25, 35)
        ));
        cardForm.setPreferredSize(new Dimension(380, 520));

        JLabel lblTitle = new JLabel("Crea tu cuenta");
        lblTitle.setFont(Theme.FONT_TITLE);
        lblTitle.setForeground(Theme.TEXT_DARK);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Ingresa tus datos para registrarte como Cliente.");
        lblSub.setFont(Theme.FONT_SMALL);
        lblSub.setForeground(Theme.TEXT_MUTED);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtNombreCompleto = crearCampoTexto();
        txtUsuario = crearCampoTexto();
        txtCorreo = crearCampoTexto();
        txtContrasena = new JPasswordField();
        txtContrasena.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        txtContrasena.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnRegistrar = new JButton("Registrarse");
        btnRegistrar.setFont(Theme.FONT_BOLD);
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setBackground(Theme.PRIMARY_BTN);
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegistrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnRegistrar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRegistrar.addActionListener(e -> registrarCliente());

        // Permitir envío con la tecla ENTER
        getRootPane().setDefaultButton(btnRegistrar);

        JButton btnVolver = new JButton("← Volver al inicio de sesión");
        btnVolver.setFont(Theme.FONT_SMALL);
        btnVolver.setForeground(Theme.PRIMARY_BTN);
        btnVolver.setContentAreaFilled(false);
        btnVolver.setBorderPainted(false);
        btnVolver.setFocusPainted(false);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnVolver.addActionListener(e -> volverAlLogin());

        // Construcción del Card Form
        cardForm.add(lblTitle);
        cardForm.add(Box.createRigidArea(new Dimension(0, 4)));
        cardForm.add(lblSub);
        cardForm.add(Box.createRigidArea(new Dimension(0, 16)));

        agregarCampoForm(cardForm, "Nombre Completo", txtNombreCompleto);
        agregarCampoForm(cardForm, "Nombre de Usuario", txtUsuario);
        agregarCampoForm(cardForm, "Correo Electrónico", txtCorreo);
        agregarCampoForm(cardForm, "Contraseña", txtContrasena);

        cardForm.add(Box.createRigidArea(new Dimension(0, 10)));
        cardForm.add(btnRegistrar);
        cardForm.add(Box.createRigidArea(new Dimension(0, 10)));
        cardForm.add(btnVolver);

        panelDerecho.add(cardForm);

        add(panelIzquierdo);
        add(panelDerecho);
    }

    private JTextField crearCampoTexto() {
        JTextField tf = new JTextField();
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        return tf;
    }

    private void agregarCampoForm(JPanel parent, String etiqueta, JComponent campo) {
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(Theme.FONT_BOLD);
        lbl.setForeground(Theme.TEXT_DARK);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        parent.add(lbl);
        parent.add(Box.createRigidArea(new Dimension(0, 4)));
        parent.add(campo);
        parent.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private void registrarCliente() {
        String nombreCompleto = txtNombreCompleto.getText().trim();
        String usuario = txtUsuario.getText().trim();
        String correo = txtCorreo.getText().trim();
        String password = new String(txtContrasena.getPassword());

        if (nombreCompleto.isEmpty() || usuario.isEmpty() || correo.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor llena todos los campos obligatorios.", "Campos Incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        btnRegistrar.setEnabled(false);

        try (Connection conn = DatabaseConfig.getConnection()) {
            JdbcClienteRepository repo = new JdbcClienteRepository();

            Cliente nuevoCliente = new Cliente();
            nuevoCliente.setNombre(nombreCompleto);
            nuevoCliente.setUsuario(usuario);
            nuevoCliente.setCorreo(correo);
            nuevoCliente.setContrasena(PasswordUtil.hashPassword(password));

            // 1. Guardar cliente
            Cliente clienteGuardado = repo.guardar(conn, nuevoCliente);
            Long idCliente = (clienteGuardado != null && clienteGuardado.getId() != null)
                    ? clienteGuardado.getId()
                    : nuevoCliente.getId();

            // 2. Instanciar servicio e inicializar cuentas bancarias
            CuentaClienteService cuentaService = new CuentaClienteService(
                    new JdbcCuentaClienteRepository(),
                    new JdbcOperacionCuentaRepository()
            );

            cuentaService.registrarNuevaCuenta(idCliente, "BBVA", new BigDecimal("5000.00"));
            cuentaService.registrarNuevaCuenta(idCliente, "Banamex", new BigDecimal("3000.00"));
            cuentaService.registrarNuevaCuenta(idCliente, "Santander", new BigDecimal("1500.00"));

            JOptionPane.showMessageDialog(this,
                    "¡Cuenta creada exitosamente con sus cuentas asociadas!\nYa puedes iniciar sesión con tu usuario o correo.",
                    "Registro Exitoso",
                    JOptionPane.INFORMATION_MESSAGE);

            volverAlLogin();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar el usuario: " + ex.getMessage(), "Error de Registro", JOptionPane.ERROR_MESSAGE);
        } finally {
            btnRegistrar.setEnabled(true);
        }
    }

    private void volverAlLogin() {
        this.dispose();
        if (parentLoginFrame != null) {
            parentLoginFrame.setVisible(true);
        } else {
            new LoginFrame().setVisible(true);
        }
    }
}