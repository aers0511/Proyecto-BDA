package com.tutiket.view.promotora;

import com.tutiket.config.DatabaseConfig;
import com.tutiket.domain.Evento;
import com.tutiket.repository.impl.JdbcBoletoRepository;
import com.tutiket.repository.impl.JdbcEventoRepository;
import com.tutiket.view.Theme;
import com.tutiket.view.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.util.List;

public class PromotoraEventosPanel extends JPanel {

    private JPanel gridEventos;
    private final Long idPromotora;

    public PromotoraEventosPanel(Long idPromotora) {
        this.idPromotora = idPromotora;

        setLayout(new BorderLayout(20, 20));
        setBackground(Theme.CONTENT_BG);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // Header Superior
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setOpaque(false);

        JPanel panelTextos = new JPanel(new GridLayout(2, 1));
        panelTextos.setOpaque(false);
        JLabel lblTitulo = new JLabel("Gestión de Eventos - Promotora");
        lblTitulo.setFont(Theme.FONT_TITLE);
        JLabel lblSub = new JLabel("Crea nuevos eventos y administra los boletos en cartelera.");
        lblSub.setFont(Theme.FONT_BODY);
        lblSub.setForeground(Theme.TEXT_MUTED);
        panelTextos.add(lblTitulo);
        panelTextos.add(lblSub);

        JButton btnNuevoEvento = new JButton("+ Crear Nuevo Evento");
        btnNuevoEvento.setFont(Theme.FONT_BOLD);
        btnNuevoEvento.setForeground(Color.WHITE);
        btnNuevoEvento.setBackground(Theme.PRIMARY_BTN);
        btnNuevoEvento.setFocusPainted(false);
        btnNuevoEvento.addActionListener(e -> abrirDialogoNuevoEvento());

        panelHeader.add(panelTextos, BorderLayout.WEST);
        panelHeader.add(btnNuevoEvento, BorderLayout.EAST);

        add(panelHeader, BorderLayout.NORTH);

        // Grid de Eventos
        gridEventos = new JPanel(new GridLayout(0, 3, 20, 20));
        gridEventos.setOpaque(false);

        JScrollPane scrollGrid = new JScrollPane(gridEventos);
        scrollGrid.setBorder(null);
        scrollGrid.getViewport().setOpaque(false);
        scrollGrid.setOpaque(false);

        add(scrollGrid, BorderLayout.CENTER);

        cargarEventos();
    }

    private void abrirDialogoNuevoEvento() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        PromotoraCrearEventoDialog dialog = new PromotoraCrearEventoDialog(parent, idPromotora);
        dialog.setVisible(true);

        if (dialog.isEventoCreado()) {
            cargarEventos();
        }
    }

    public void cargarEventos() {
        gridEventos.removeAll();
        try (Connection conn = DatabaseConfig.getConnection()) {
            JdbcEventoRepository repoEv = new JdbcEventoRepository();
            JdbcBoletoRepository repoBol = new JdbcBoletoRepository();
            
            List<Evento> eventos = (idPromotora != null)
                    ? repoEv.listarPorPromotora(conn, idPromotora)
                    : repoEv.listarActivos(conn);

            for (Evento ev : eventos) {
                int disponibles = repoBol.buscarDisponiblesPorEvento(conn, ev.getId()).size();
                gridEventos.add(crearCardEventoPromotora(ev, disponibles));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        gridEventos.revalidate();
        gridEventos.repaint();
    }

    private JPanel crearCardEventoPromotora(Evento ev, int disponibles) {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblNombre = new JLabel(ev.getNombre());
        lblNombre.setFont(Theme.FONT_SUBTITLE);

        JLabel lblCategoria = new JLabel(ev.getCategoria() + " • " + ev.getClasificacion());
        lblCategoria.setFont(Theme.FONT_SMALL);
        lblCategoria.setForeground(Theme.TEXT_MUTED);

        JLabel lblPrecio = new JLabel("Precio: $" + ev.getPrecioBase() + " MXN");
        lblPrecio.setFont(Theme.FONT_BOLD);

        JLabel lblDisp = new JLabel(disponibles + " boletos disponibles");
        lblDisp.setFont(Theme.FONT_SMALL);
        lblDisp.setForeground(Theme.TAG_GREEN_TEXT);

        card.add(lblNombre);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(lblCategoria);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(lblPrecio);
        card.add(lblDisp);

        return card;
    }
}