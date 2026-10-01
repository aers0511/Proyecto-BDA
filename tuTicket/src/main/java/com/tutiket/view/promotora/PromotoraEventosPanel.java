package com.tutiket.view.promotora;

import com.tutiket.config.DatabaseConfig;
import com.tutiket.domain.Evento;
import com.tutiket.repository.impl.JdbcBoletoRepository;
import com.tutiket.repository.impl.JdbcEventoRepository;
import com.tutiket.view.Theme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.util.List;

public class PromotoraEventosPanel extends JPanel {

    private final JPanel gridEventos;
    private final Long idPromotora;

    public PromotoraEventosPanel(Long idPromotora) {
        this.idPromotora = idPromotora;

        setLayout(new BorderLayout(20, 20));
        setBackground(Theme.CONTENT_BG);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // Header Superior
        JPanel panelHeader = new JPanel(new BorderLayout(15, 0));
        panelHeader.setOpaque(false);

        JPanel panelTextos = new JPanel();
        panelTextos.setLayout(new BoxLayout(panelTextos, BoxLayout.Y_AXIS));
        panelTextos.setOpaque(false);

        JLabel lblTitulo = new JLabel("Gestión de Eventos - Promotora");
        lblTitulo.setFont(Theme.FONT_TITLE);
        lblTitulo.setForeground(Theme.TEXT_DARK);

        JLabel lblSub = new JLabel("Crea nuevos eventos y administra los boletos en cartelera.");
        lblSub.setFont(Theme.FONT_BODY);
        lblSub.setForeground(Theme.TEXT_MUTED);

        panelTextos.add(lblTitulo);
        panelTextos.add(Box.createRigidArea(new Dimension(0, 4)));
        panelTextos.add(lblSub);

        // Botón "+ Crear Nuevo Evento" con hover
        JButton btnNuevoEvento = new JButton("+ Crear Nuevo Evento");
        btnNuevoEvento.setFont(Theme.FONT_BOLD);
        btnNuevoEvento.setForeground(Color.WHITE);
        btnNuevoEvento.setBackground(Theme.PRIMARY_BTN);
        btnNuevoEvento.setFocusPainted(false);
        btnNuevoEvento.setBorderPainted(false);
        btnNuevoEvento.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNuevoEvento.setBorder(new EmptyBorder(10, 18, 10, 18));

        btnNuevoEvento.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnNuevoEvento.setBackground(Theme.PRIMARY_BTN.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnNuevoEvento.setBackground(Theme.PRIMARY_BTN);
            }
        });

        btnNuevoEvento.addActionListener(e -> abrirDialogoNuevoEvento());

        panelHeader.add(panelTextos, BorderLayout.CENTER);
        panelHeader.add(btnNuevoEvento, BorderLayout.EAST);

        add(panelHeader, BorderLayout.NORTH);

        // Grid de Eventos
        gridEventos = new JPanel(new GridLayout(0, 3, 20, 20));
        gridEventos.setOpaque(false);

        // Panel contenedor para alinear el grid arriba y evitar stretch vertical feo
        JPanel wrapperGrid = new JPanel(new BorderLayout());
        wrapperGrid.setOpaque(false);
        wrapperGrid.add(gridEventos, BorderLayout.NORTH);

        JScrollPane scrollGrid = new JScrollPane(wrapperGrid);
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
        card.setBackground(Theme.CARD_BG);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(18, 18, 18, 18)
        ));

        // Título del evento
        JLabel lblNombre = new JLabel(ev.getNombre());
        lblNombre.setFont(Theme.FONT_SUBTITLE);
        lblNombre.setForeground(Theme.TEXT_DARK);
        lblNombre.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Categoría y Clasificación
        String catTexto = (ev.getCategoria() != null ? ev.getCategoria() : "Sin categoría")
                + " • " + (ev.getClasificacion() != null ? ev.getClasificacion() : "G");
        JLabel lblCategoria = new JLabel(catTexto);
        lblCategoria.setFont(Theme.FONT_BODY);
        lblCategoria.setForeground(Theme.TEXT_MUTED);
        lblCategoria.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Precio Base
        String precioTexto = String.format("$%,.2f MXN", ev.getPrecioBase() != null ? ev.getPrecioBase() : 0.0);
        JLabel lblPrecio = new JLabel("Precio: " + precioTexto);
        lblPrecio.setFont(Theme.FONT_BOLD);
        lblPrecio.setForeground(Theme.TEXT_DARK);
        lblPrecio.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Badge / Indicador de boletos disponibles
        JLabel lblDisp = new JLabel(disponibles + " boletos disponibles");
        lblDisp.setFont(Theme.FONT_BODY);
        lblDisp.setForeground(disponibles > 0 ? Theme.TAG_GREEN_TEXT : new Color(220, 38, 38));
        lblDisp.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lblNombre);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(lblCategoria);
        card.add(Box.createRigidArea(new Dimension(0, 12)));
        card.add(lblPrecio);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(lblDisp);

        return card;
    }
}