package com.tutiket.view.promotora;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.tutiket.config.DatabaseConfig;
import com.tutiket.domain.Promotora;
import com.tutiket.repository.impl.JdbcPromotoraRepository;
import com.tutiket.util.PasswordUtil;
import com.tutiket.view.Theme;

public class PromotoraRegistroDialog extends JDialog {

    private JTextField txtEmpresa;
    private JTextField txtEmail;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JTextField txtRfc;

    private static final Color COLOR_BORDER = new Color(226, 232, 240);

    public PromotoraRegistroDialog(Window parent) {
        super(parent, "Registro de Promotora", ModalityType.APPLICATION_MODAL);
        initUI();
    }

    private void initUI() {
        setSize(440, 560);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());
        setResizable(false);

        // Panel de Formulario
        JPanel panelForm = new JPanel();
        panelForm.setLayout(new BoxLayout(panelForm, BoxLayout.Y_AXIS));
        panelForm.setBackground(Color.WHITE);
        panelForm.setBorder(new EmptyBorder(25, 30, 20, 30));

        JLabel lblTitulo = new JLabel("Registro de Promotora");
        lblTitulo.setFont(Theme.FONT_TITLE);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Crea una cuenta para gestionar y publicar eventos.");
        lblSub.setFont(Theme.FONT_SMALL);
        lblSub.setForeground(Theme.TEXT_MUTED);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtEmpresa = crearCampoTexto();
        txtEmail = crearCampoTexto();
        txtUsuario = crearCampoTexto();
        txtPassword = crearCampoPassword();
        txtRfc = crearCampoTexto();

        panelForm.add(lblTitulo);
        panelForm.add(Box.createRigidArea(new Dimension(0, 4)));
        panelForm.add(lblSub);
        panelForm.add(Box.createRigidArea(new Dimension(0, 18)));

        agregarCampoForm(panelForm, "Nombre de la Empresa", txtEmpresa);
        agregarCampoForm(panelForm, "Correo Electrónico", txtEmail);
        agregarCampoForm(panelForm, "Nombre de Usuario", txtUsuario);
        agregarCampoForm(panelForm, "Contraseña", txtPassword);
        agregarCampoForm(panelForm, "RFC", txtRfc);

        // Panel de Botones Inferior
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        panelBotones.setBackground(Theme.CONTENT_BG);
        panelBotones.setBorder(new EmptyBorder(0, 20, 5, 20));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(Theme.FONT_BODY);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.addActionListener(e -> dispose());

        JButton btnRegistrar = new JButton("Registrar Cuenta");
        btnRegistrar.setFont(Theme.FONT_BOLD);
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setBackground(Theme.PRIMARY_BTN);
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setBorderPainted(false);
        btnRegistrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegistrar.setPreferredSize(new Dimension(140, 36));

        btnRegistrar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnRegistrar.setBackground(Theme.PRIMARY_BTN.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnRegistrar.setBackground(Theme.PRIMARY_BTN);
            }
        });

        btnRegistrar.addActionListener(e -> registrarPromotora());
        getRootPane().setDefaultButton(btnRegistrar);

        panelBotones.add(btnCancelar);
        panelBotones.add(btnRegistrar);

        add(panelForm, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
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
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        campo.setFont(Theme.FONT_BODY);
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);

        campo.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
    }

    private void agregarCampoForm(JPanel parent, String etiqueta, JComponent campo) {
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(Theme.FONT_BOLD);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        parent.add(lbl);
        parent.add(Box.createRigidArea(new Dimension(0, 4)));
        parent.add(campo);
        parent.add(Box.createRigidArea(new Dimension(0, 10)));
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