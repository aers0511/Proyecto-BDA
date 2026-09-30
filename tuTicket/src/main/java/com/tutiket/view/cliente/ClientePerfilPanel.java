package com.tutiket.view.cliente;

import com.tutiket.domain.Cliente;
import com.tutiket.view.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ClientePerfilPanel extends JPanel {

    private Cliente clienteLogueado;

    public ClientePerfilPanel(Cliente cliente) {
        this.clienteLogueado = cliente;

        setLayout(new BorderLayout(20, 20));
        setBackground(Theme.CONTENT_BG);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // Header
        JPanel panelHeader = new JPanel(new GridLayout(2, 1));
        panelHeader.setOpaque(false);
        JLabel lblTitulo = new JLabel("Mi Perfil");
        lblTitulo.setFont(Theme.FONT_TITLE);
        JLabel lblSub = new JLabel("Consulta la información de tu cuenta.");
        lblSub.setFont(Theme.FONT_BODY);
        lblSub.setForeground(Theme.TEXT_MUTED);
        panelHeader.add(lblTitulo);
        panelHeader.add(lblSub);

        add(panelHeader, BorderLayout.NORTH);

        // Card de Información
        JPanel cardProfile = new JPanel();
        cardProfile.setBackground(Theme.CARD_BG);
        cardProfile.setLayout(new BoxLayout(cardProfile, BoxLayout.Y_AXIS));
        cardProfile.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(30, 30, 30, 30)
        ));
        cardProfile.setMaximumSize(new Dimension(500, 300));

        agregarDato(cardProfile, "Nombre Completo:", clienteLogueado.getNombre());
        agregarDato(cardProfile, "Nombre de Usuario:", clienteLogueado.getUsuario());
        agregarDato(cardProfile, "Correo Electrónico:", clienteLogueado.getCorreo());

        JPanel panelCenter = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelCenter.setOpaque(false);
        panelCenter.add(cardProfile);

        add(panelCenter, BorderLayout.CENTER);
    }

    private void agregarDato(JPanel parent, String etiqueta, String valor) {
        JLabel lblE = new JLabel(etiqueta);
        lblE.setFont(Theme.FONT_BOLD);
        lblE.setForeground(Theme.TEXT_MUTED);

        JLabel lblV = new JLabel(valor != null ? valor : "-");
        lblV.setFont(Theme.FONT_SUBTITLE);
        lblV.setForeground(Theme.TEXT_DARK);

        parent.add(lblE);
        parent.add(Box.createRigidArea(new Dimension(0, 4)));
        parent.add(lblV);
        parent.add(Box.createRigidArea(new Dimension(0, 15)));
    }
}