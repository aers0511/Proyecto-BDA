package com.tutiket.view.promotora;

import com.tutiket.config.DatabaseConfig;
import com.tutiket.domain.Promotora;
import com.tutiket.repository.impl.JdbcPromotoraRepository;
import com.tutiket.util.PasswordUtil;
import com.tutiket.view.Theme;
import com.tutiket.view.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;

public class PromotoraRegistroDialog extends JDialog {

    private JTextField txtEmpresa;
    private JTextField txtEmail;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JTextField txtRfc;

    public PromotoraRegistroDialog(Window parent) {
        super(parent, "Registro de Promotora", ModalityType.APPLICATION_MODAL);
        initUI();
    }

    private void initUI() {
        setSize(420, 500);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());
        setResizable(false);

        JPanel panelForm = new JPanel();
        panelForm.setLayout(new BoxLayout(panelForm, BoxLayout.Y_AXIS));
        panelForm.setBackground(Color.WHITE);
        panelForm.setBorder(new EmptyBorder(25, 25, 20, 25));

        JLabel lblTitulo = new JLabel("Registro de Promotora");
        lblTitulo.setFont(Theme.FONT_TITLE);

        JLabel lblSub = new JLabel("Crea una cuenta para gestionar y publicar eventos.");
        lblSub.setFont(Theme.FONT_SMALL);
        lblSub.setForeground(Theme.TEXT_MUTED);

        txtEmpresa = crearCampoTexto();
        txtEmail = crearCampoTexto();
        txtUsuario = crearCampoTexto();
        txtPassword = new JPasswordField();
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        txtRfc = crearCampoTexto();

        panelForm.add(lblTitulo);
        panelForm.add(Box.createRigidArea(new Dimension(0, 4)));
        panelForm.add(lblSub);
        panelForm.add(Box.createRigidArea(new Dimension(0, 15)));

        agregarCampoForm(panelForm, "Nombre de la Empresa", txtEmpresa);
        agregarCampoForm(panelForm, "Correo Electrónico", txtEmail);
        agregarCampoForm(panelForm, "Nombre de Usuario", txtUsuario);
        agregarCampoForm(panelForm, "Contraseña", txtPassword);
        agregarCampoForm(panelForm, "RFC", txtRfc);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        panelBotones.setBackground(Theme.CONTENT_BG);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(Theme.FONT_BODY);
        btnCancelar.addActionListener(e -> dispose());

        JButton btnRegistrar = new JButton("Registrar Cuenta");
        btnRegistrar.setFont(Theme.FONT_BOLD);
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setBackground(Theme.PRIMARY_BTN);
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.addActionListener(e -> registrarPromotora());

        panelBotones.add(btnCancelar);
        panelBotones.add(btnRegistrar);

        add(panelForm, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private JTextField crearCampoTexto() {
        JTextField tf = new JTextField();
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        return tf;
    }

    private void agregarCampoForm(JPanel parent, String etiqueta, JComponent campo) {
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(Theme.FONT_BOLD);
        parent.add(lbl);
        parent.add(campo);
        parent.add(Box.createRigidArea(new Dimension(0, 6)));
    }

    private void registrarPromotora() {
        String empresa = txtEmpresa.getText().trim();
        String correo = txtEmail.getText().trim();
        String usuario = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String rfc = txtRfc.getText().trim();

        if (empresa.isEmpty() || correo.isEmpty() || usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa todos los campos obligatorios.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConfig.getConnection()) {
            Promotora p = new Promotora();
            p.setNombreEmpresa(empresa);
            p.setCorreo(correo);
            p.setUsuario(usuario);
            p.setContrasena(PasswordUtil.hashPassword(password));
            p.setRfc(rfc);

            JdbcPromotoraRepository repo = new JdbcPromotoraRepository();
            repo.guardar(conn, p);

            JOptionPane.showMessageDialog(this,
                    "¡Promotora registrada exitosamente!\nYa puedes iniciar sesión.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar la promotora: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}