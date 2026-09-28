package presentacion;

import controlador.ClienteControlador;
import entidad.Cliente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class FrmLogin extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JButton btnIngresar;
    private JButton btnRegistrarse;
    private final ClienteControlador clienteControlador;

    public FrmLogin() {
        this.clienteControlador = new ClienteControlador();
        initComponents();
    }

    private void initComponents() {
        setTitle("tuTiket - Iniciar Sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(380, 260);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel("¡Bienvenido a tuTiket!", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        gbc.gridx = 0; 
        gbc.gridy = 0; 
        gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; 
        gbc.gridy = 1;
        panel.add(new JLabel("Usuario:"), gbc);

        txtUsuario = new JTextField(15);
        gbc.gridx = 1; 
        gbc.gridy = 1;
        panel.add(txtUsuario, gbc);

        gbc.gridx = 0; 
        gbc.gridy = 2;
        panel.add(new JLabel("Contraseña:"), gbc);

        txtContrasena = new JPasswordField(15);
        gbc.gridx = 1; 
        gbc.gridy = 2;
        panel.add(txtContrasena, gbc);

        btnIngresar = new JButton("Ingresar");
        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnIngresar.addActionListener(this::ejecutarLogin);

        btnRegistrarse = new JButton("Crear Cuenta");
        btnRegistrarse.addActionListener(e -> abrirRegistro());

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panelBotones.add(btnIngresar);
        panelBotones.add(btnRegistrarse);

        gbc.gridx = 0; 
        gbc.gridy = 3; 
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 6, 6, 6);
        panel.add(panelBotones, gbc);

        add(panel);
    }

    private void ejecutarLogin(ActionEvent e) {
        String usuario = txtUsuario.getText().trim();
        String contrasena = new String(txtContrasena.getPassword());

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor complete todos los campos.", 
                "Campos incompletos", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Cliente cliente = clienteControlador.login(usuario, contrasena);
            JOptionPane.showMessageDialog(this, 
                "¡Bienvenido/a, " + cliente.getNombres() + "!", 
                "Acceso Concedido", 
                JOptionPane.INFORMATION_MESSAGE);

            // Abrir la pantalla principal del cliente pasando el objeto de sesión
            // new FrmPrincipalCliente(cliente).setVisible(true);
            this.dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                ex.getMessage(), 
                "Error de Autenticación", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirRegistro() {
        // new FrmRegistroCliente(this).setVisible(true);
        this.setVisible(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmLogin().setVisible(true));
    }
}