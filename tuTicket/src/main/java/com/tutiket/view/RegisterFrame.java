package com.tutiket.view;

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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.sql.Connection;

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
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.tutiket.config.DatabaseConfig;
import com.tutiket.domain.Cliente;
import com.tutiket.repository.impl.JdbcClienteRepository;
import com.tutiket.repository.impl.JdbcCuentaClienteRepository;
import com.tutiket.repository.impl.JdbcOperacionCuentaRepository;
import com.tutiket.service.CuentaClienteService;
import com.tutiket.util.PasswordUtil;

public class RegisterFrame extends JFrame {

    private JTextField txtNombres;
    private JTextField txtApellidos;
    private JTextField txtFechaNacimiento;
    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JPasswordField txtConfirmarContrasena;
    private JButton btnRegistrar;
    private final JFrame parentLoginFrame;

    // Se conservan exactamente los mismos colores originales
    private static final Color COLOR_BG = new Color(244, 246, 249);
    private static final Color COLOR_PRIMARY_BTN = new Color(2, 119, 189);
    private static final Color COLOR_TEXT_DARK = new Color(30, 41, 59);
    private static final Color COLOR_TEXT_MUTED = new Color(100, 116, 139);
    private static final Color COLOR_BORDER = new Color(226, 232, 240);

    public RegisterFrame(JFrame parent) {
        this.parentLoginFrame = parent;
        initUI();
    }

    private void initUI() {
        setTitle("BoletosFan - Crear cuenta");
        setSize(1000, 720);
        setMinimumSize(new Dimension(850, 650));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                volverAlLogin();
            }
        });

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(COLOR_BG);

        JPanel cardForm = new JPanel();
        cardForm.setLayout(new BoxLayout(cardForm, BoxLayout.Y_AXIS));
        cardForm.setBackground(Color.WHITE);
        cardForm.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(35, 40, 35, 40)
        ));
        
        // Dimensiones optimizadas para mantener la proporción limpia
        cardForm.setMinimumSize(new Dimension(500, 580));
        cardForm.setPreferredSize(new Dimension(540, 600));
        cardForm.setMaximumSize(new Dimension(580, 620));

        // Encabezado
        JLabel lblTitle = new JLabel("Crea tu cuenta");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTitle.setForeground(COLOR_TEXT_DARK);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Registra tus datos para comprar boletos de forma rápida y segura.");
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblSub.setForeground(COLOR_TEXT_MUTED);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Instanciación de campos
        txtNombres = crearCampoTexto();
        txtApellidos = crearCampoTexto();
        txtFechaNacimiento = crearCampoTexto();
        txtUsuario = crearCampoTexto();
        txtContrasena = crearCampoPassword();
        txtConfirmarContrasena = crearCampoPassword();

        // Fila Nombres y Apellidos
        JPanel panelNombreApellido = new JPanel(new GridLayout(1, 2, 16, 0));
        panelNombreApellido.setOpaque(false);
        panelNombreApellido.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelNombreApellido.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));

        panelNombreApellido.add(crearGrupoCampo("Nombre(s)", txtNombres));
        panelNombreApellido.add(crearGrupoCampo("Apellido(s)", txtApellidos));

        // Grupo Fecha de Nacimiento
        JPanel panelFechaGroup = new JPanel();
        panelFechaGroup.setLayout(new BoxLayout(panelFechaGroup, BoxLayout.Y_AXIS));
        panelFechaGroup.setOpaque(false);
        panelFechaGroup.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelFechaGroup.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

        JLabel lblFecha = crearEtiquetaCampo("Fecha de nacimiento");
        JLabel lblFechaHint = new JLabel("Formato: DD/MM/AAAA");
        lblFechaHint.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblFechaHint.setForeground(COLOR_TEXT_MUTED);

        panelFechaGroup.add(lblFecha);
        panelFechaGroup.add(Box.createRigidArea(new Dimension(0, 4)));
        panelFechaGroup.add(txtFechaNacimiento);
        panelFechaGroup.add(Box.createRigidArea(new Dimension(0, 2)));
        panelFechaGroup.add(lblFechaHint);

        // Grupo Nombre de Usuario / Correo
        JPanel panelUsuarioGroup = crearGrupoCampo("Nombre de usuario o Correo", txtUsuario);
        panelUsuarioGroup.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));

        // Fila Contraseñas
        JPanel panelPasswords = new JPanel(new GridLayout(1, 2, 16, 0));
        panelPasswords.setOpaque(false);
        panelPasswords.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelPasswords.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));

        panelPasswords.add(crearGrupoCampo("Contraseña", txtContrasena));
        panelPasswords.add(crearGrupoCampo("Confirmar contraseña", txtConfirmarContrasena));

        // Botón Registrar
        btnRegistrar = new JButton("Crear cuenta");
        btnRegistrar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setBackground(COLOR_PRIMARY_BTN);
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setBorderPainted(false);
        btnRegistrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegistrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnRegistrar.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnRegistrar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnRegistrar.setBackground(COLOR_PRIMARY_BTN.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnRegistrar.setBackground(COLOR_PRIMARY_BTN);
            }
        });

        btnRegistrar.addActionListener(e -> registrarCliente());
        getRootPane().setDefaultButton(btnRegistrar);

        // Separador sutil
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separator.setForeground(COLOR_BORDER);
        separator.setBackground(COLOR_BORDER);
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Enlace al Login
        JPanel panelLoginLink = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        panelLoginLink.setOpaque(false);
        panelLoginLink.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelLoginLink.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel lblQuestion = new JLabel("¿Ya tienes cuenta?");
        lblQuestion.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblQuestion.setForeground(COLOR_TEXT_MUTED);

        JButton btnIniciaSesion = new JButton("Inicia sesión");
        btnIniciaSesion.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnIniciaSesion.setForeground(COLOR_PRIMARY_BTN);
        btnIniciaSesion.setContentAreaFilled(false);
        btnIniciaSesion.setBorderPainted(false);
        btnIniciaSesion.setFocusPainted(false);
        btnIniciaSesion.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnIniciaSesion.addActionListener(e -> volverAlLogin());

        panelLoginLink.add(lblQuestion);
        panelLoginLink.add(btnIniciaSesion);

        // Ensamblado dentro de la tarjeta
        cardForm.add(lblTitle);
        cardForm.add(Box.createRigidArea(new Dimension(0, 4)));
        cardForm.add(lblSub);
        cardForm.add(Box.createRigidArea(new Dimension(0, 20)));

        cardForm.add(panelNombreApellido);
        cardForm.add(Box.createRigidArea(new Dimension(0, 14)));

        cardForm.add(panelFechaGroup);
        cardForm.add(Box.createRigidArea(new Dimension(0, 14)));

        cardForm.add(panelUsuarioGroup);
        cardForm.add(Box.createRigidArea(new Dimension(0, 14)));

        cardForm.add(panelPasswords);
        cardForm.add(Box.createRigidArea(new Dimension(0, 24)));

        cardForm.add(btnRegistrar);
        cardForm.add(Box.createRigidArea(new Dimension(0, 18)));
        cardForm.add(separator);
        cardForm.add(Box.createRigidArea(new Dimension(0, 14)));
        cardForm.add(panelLoginLink);

        mainPanel.add(cardForm);

        // ScrollPane transparente para pantallas reducidas
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        setContentPane(scrollPane);
    }

    private JTextField crearCampoTexto() {
        JTextField tf = new JTextField();
        estilarCampoInput(tf);
        return tf;
    }

    private JPasswordField crearCampoPassword() {
        JPasswordField pf = new JPasswordField();
        estilarCampoInput(pf);
        return pf;
    }

    private void estilarCampoInput(JComponent campo) {
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        campo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);

        LineBorder normalBorder = new LineBorder(new Color(203, 213, 225), 1, true);
        LineBorder activeBorder = new LineBorder(COLOR_PRIMARY_BTN, 1, true);

        campo.setBorder(BorderFactory.createCompoundBorder(
                normalBorder,
                new EmptyBorder(6, 10, 6, 10)
        ));

        // Evento para resaltar el campo enfocado
        campo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                        activeBorder,
                        new EmptyBorder(6, 10, 6, 10)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                        normalBorder,
                        new EmptyBorder(6, 10, 6, 10)
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

    private JPanel crearGrupoCampo(String etiqueta, JComponent campo) {
        JPanel group = new JPanel();
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setOpaque(false);
        group.setAlignmentX(Component.LEFT_ALIGNMENT);

        group.add(crearEtiquetaCampo(etiqueta));
        group.add(Box.createRigidArea(new Dimension(0, 4)));
        group.add(campo);

        return group;
    }

    private void registrarCliente() {
        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String usuario = txtUsuario.getText().trim();
        String pass1 = new String(txtContrasena.getPassword());
        String pass2 = new String(txtConfirmarContrasena.getPassword());

        if (nombres.isEmpty() || usuario.isEmpty() || pass1.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor llena todos los campos requeridos.",
                    "Campos Incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!pass1.equals(pass2)) {
            JOptionPane.showMessageDialog(this,
                    "Las contraseñas ingresadas no coinciden.",
                    "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        btnRegistrar.setEnabled(false);

        try (Connection conn = DatabaseConfig.getConnection()) {
            JdbcClienteRepository repo = new JdbcClienteRepository();

            Cliente nuevoCliente = new Cliente();
            nuevoCliente.setNombre(nombres + (apellidos.isEmpty() ? "" : " " + apellidos));
            nuevoCliente.setUsuario(usuario);
            nuevoCliente.setCorreo(usuario.contains("@") ? usuario : usuario + "@correo.com");
            nuevoCliente.setContrasena(PasswordUtil.hashPassword(pass1));

            Cliente clienteGuardado = repo.guardar(conn, nuevoCliente);
            Long idCliente = (clienteGuardado != null && clienteGuardado.getId() != null)
                    ? clienteGuardado.getId()
                    : nuevoCliente.getId();

            CuentaClienteService cuentaService = new CuentaClienteService(
                    new JdbcCuentaClienteRepository(),
                    new JdbcOperacionCuentaRepository()
            );

            cuentaService.registrarNuevaCuenta(idCliente, "BBVA", new BigDecimal("5000.00"));
            cuentaService.registrarNuevaCuenta(idCliente, "Banamex", new BigDecimal("3000.00"));
            cuentaService.registrarNuevaCuenta(idCliente, "Santander", new BigDecimal("1500.00"));

            JOptionPane.showMessageDialog(this,
                    "¡Cuenta creada exitosamente!\nYa puedes iniciar sesión con tu usuario.",
                    "Registro Exitoso",
                    JOptionPane.INFORMATION_MESSAGE);

            volverAlLogin();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al registrar el usuario: " + ex.getMessage(),
                    "Error de Registro", JOptionPane.ERROR_MESSAGE);
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