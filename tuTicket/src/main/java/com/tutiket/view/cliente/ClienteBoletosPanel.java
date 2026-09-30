package com.tutiket.view.cliente;

import com.tutiket.config.DatabaseConfig;
import com.tutiket.domain.Boleto;
import com.tutiket.domain.Cliente;
import com.tutiket.domain.Evento;
import com.tutiket.repository.impl.JdbcBoletoRepository;
import com.tutiket.repository.impl.JdbcEventoRepository;
import com.tutiket.view.dialog.BoletoImpresionDialog;
import com.tutiket.view.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ClienteBoletosPanel extends JPanel {

    private final Cliente clienteLogueado;
    private final JPanel gridBoletos;

    public ClienteBoletosPanel(Cliente cliente) {
        this.clienteLogueado = cliente;

        setLayout(new BorderLayout(20, 20));
        setBackground(Theme.CONTENT_BG);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // Header Superior
        JPanel panelHeader = new JPanel();
        panelHeader.setLayout(new BoxLayout(panelHeader, BoxLayout.Y_AXIS));
        panelHeader.setOpaque(false);

        JLabel lblTitulo = new JLabel("Mis Boletos Digitales");
        lblTitulo.setFont(Theme.FONT_TITLE);
        lblTitulo.setForeground(Theme.TEXT_DARK);

        JLabel lblSub = new JLabel("Haz clic en cualquier tarjeta para ver e imprimir tu boleto con código QR.");
        lblSub.setFont(Theme.FONT_BODY);
        lblSub.setForeground(Theme.TEXT_MUTED);

        panelHeader.add(lblTitulo);
        panelHeader.add(Box.createRigidArea(new Dimension(0, 4)));
        panelHeader.add(lblSub);

        add(panelHeader, BorderLayout.NORTH);

        // Grid contenedor de boletos (2 columnas)
        gridBoletos = new JPanel(new GridLayout(0, 2, 20, 20));
        gridBoletos.setOpaque(false);

        JScrollPane scrollGrid = new JScrollPane(gridBoletos);
        scrollGrid.setBorder(null);
        scrollGrid.getViewport().setOpaque(false);
        scrollGrid.setOpaque(false);
        scrollGrid.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollGrid, BorderLayout.CENTER);

        cargarBoletosComprados();

        // Listener para recargar los boletos automáticamente cada vez que la pantalla se vuelva visible
        addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                cargarBoletosComprados();
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {}

            @Override
            public void ancestorMoved(AncestorEvent event) {}
        });
    }

    public void cargarBoletosComprados() {
        gridBoletos.removeAll();

        try (Connection conn = DatabaseConfig.getConnection()) {
            JdbcBoletoRepository repoBol = new JdbcBoletoRepository();
            JdbcEventoRepository repoEv = new JdbcEventoRepository();

            List<Boleto> boletos = repoBol.buscarPorCliente(conn, clienteLogueado.getId());

            if (boletos == null || boletos.isEmpty()) {
                gridBoletos.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 60));
                mostrarMensajeVacio();
            } else {
                gridBoletos.setLayout(new GridLayout(0, 2, 20, 20));
                for (Boleto bol : boletos) {
                    Evento evento = repoEv.buscarPorId(conn, bol.getIdEvento()).orElse(null);
                    gridBoletos.add(crearCardBoleto(bol, evento));
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar tus boletos: " + ex.getMessage(),
                    "Error de Carga",
                    JOptionPane.ERROR_MESSAGE);
        }

        gridBoletos.revalidate();
        gridBoletos.repaint();
    }

    private JPanel crearCardBoleto(Boleto boleto, Evento evento) {
        JPanel card = new JPanel(new BorderLayout(15, 10));
        card.setBackground(Theme.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(18, 20, 18, 20)
        ));

        // Panel de información central
        JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
        panelInfo.setOpaque(false);

        String nombreEvento = (evento != null && evento.getNombre() != null) ? evento.getNombre() : "Evento Desconocido";
        String lugar = (evento != null && evento.getLugar() != null) ? evento.getLugar() : "Ubicación no especificada";
        String fecha = (evento != null && evento.getFechaHora() != null)
                ? evento.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : "Fecha por confirmar";

        JLabel lblNombre = new JLabel("🎟 " + nombreEvento);
        lblNombre.setFont(Theme.FONT_SUBTITLE);
        lblNombre.setForeground(Theme.TEXT_DARK);

        JLabel lblLugar = new JLabel("📍 " + lugar);
        lblLugar.setFont(Theme.FONT_SMALL);
        lblLugar.setForeground(Theme.TEXT_MUTED);

        JLabel lblFecha = new JLabel("📅 " + fecha);
        lblFecha.setFont(Theme.FONT_SMALL);
        lblFecha.setForeground(Theme.TEXT_MUTED);

        JLabel lblFolio = new JLabel("Folio: " + (boleto.getFolio() != null ? boleto.getFolio() : "N/A"));
        lblFolio.setFont(Theme.FONT_BOLD);
        lblFolio.setForeground(Theme.PRIMARY_BTN);

        panelInfo.add(lblNombre);
        panelInfo.add(Box.createRigidArea(new Dimension(0, 6)));
        panelInfo.add(lblLugar);
        panelInfo.add(Box.createRigidArea(new Dimension(0, 2)));
        panelInfo.add(lblFecha);
        panelInfo.add(Box.createRigidArea(new Dimension(0, 10)));
        panelInfo.add(lblFolio);

        // Panel lateral derecho (Precio y Estado)
        JPanel panelDerecho = new JPanel();
        panelDerecho.setLayout(new BoxLayout(panelDerecho, BoxLayout.Y_AXIS));
        panelDerecho.setOpaque(false);

        JLabel lblPrecio = new JLabel(String.format("$%,.2f MXN", boleto.getPrecio()));
        lblPrecio.setFont(Theme.FONT_SUBTITLE);
        lblPrecio.setForeground(Theme.TEXT_DARK);
        lblPrecio.setAlignmentX(Component.RIGHT_ALIGNMENT);

        String textoEstado = (boleto.getEstado() != null) ? boleto.getEstado().name() : "COMPRADO";
        JLabel lblEstado = new JLabel(" " + textoEstado + " ");
        lblEstado.setOpaque(true);
        lblEstado.setBackground(Theme.TAG_GREEN_BG);
        lblEstado.setForeground(Theme.TAG_GREEN_TEXT);
        lblEstado.setFont(Theme.FONT_BOLD);
        lblEstado.setAlignmentX(Component.RIGHT_ALIGNMENT);
        lblEstado.setBorder(new EmptyBorder(4, 8, 4, 8));

        panelDerecho.add(lblPrecio);
        panelDerecho.add(Box.createRigidArea(new Dimension(0, 8)));
        panelDerecho.add(lblEstado);

        card.add(panelInfo, BorderLayout.CENTER);
        card.add(panelDerecho, BorderLayout.EAST);

        // Tooltip y eventos de interacción (Hover y Clic)
        card.setToolTipText("Haz clic para ver/imprimir el boleto digital con QR");

        MouseAdapter clickAdapter = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(248, 250, 252));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(Theme.CARD_BG);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                Window parentWindow = SwingUtilities.getWindowAncestor(card);
                BoletoImpresionDialog dialog = new BoletoImpresionDialog(
                        parentWindow,
                        clienteLogueado,
                        evento,
                        boleto.getFolio(),
                        boleto.getPrecio()
                );
                dialog.setLocationRelativeTo(parentWindow);
                dialog.setVisible(true);
            }
        };

        agregarClickRecursivo(card, clickAdapter);

        return card;
    }

    private void agregarClickRecursivo(Component comp, MouseAdapter adapter) {
        comp.setCursor(new Cursor(Cursor.HAND_CURSOR));
        comp.addMouseListener(adapter);
        if (comp instanceof Container) {
            for (Component hijo : ((Container) comp).getComponents()) {
                agregarClickRecursivo(hijo, adapter);
            }
        }
    }

    private void mostrarMensajeVacio() {
        JPanel panelVacio = new JPanel();
        panelVacio.setOpaque(false);
        panelVacio.setLayout(new BoxLayout(panelVacio, BoxLayout.Y_AXIS));

        JLabel lblIcono = new JLabel("🎟");
        lblIcono.setFont(new Font("SansSerif", Font.PLAIN, 48));
        lblIcono.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblVacio = new JLabel("Aún no tienes boletos comprados.");
        lblVacio.setFont(Theme.FONT_SUBTITLE);
        lblVacio.setForeground(Theme.TEXT_DARK);
        lblVacio.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Explora la cartelera de eventos para adquirir tus primeros accesos.");
        lblSub.setFont(Theme.FONT_BODY);
        lblSub.setForeground(Theme.TEXT_MUTED);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelVacio.add(lblIcono);
        panelVacio.add(Box.createRigidArea(new Dimension(0, 10)));
        panelVacio.add(lblVacio);
        panelVacio.add(Box.createRigidArea(new Dimension(0, 5)));
        panelVacio.add(lblSub);

        gridBoletos.add(panelVacio);
    }
}