package com.tutiket.view.cliente;

import com.tutiket.config.DatabaseConfig;
import com.tutiket.domain.Boleto;
import com.tutiket.domain.Cliente;
import com.tutiket.domain.CuentaCliente;
import com.tutiket.domain.Evento;
import com.tutiket.dto.CompraRequestDTO;
import com.tutiket.exception.InsufficientBalanceException;
import com.tutiket.repository.impl.*;
import com.tutiket.service.VentaService;
import com.tutiket.view.MainLayoutFrame;
import com.tutiket.view.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.util.List;

public class ClienteEventosPanel extends JPanel {

    private final Cliente clienteLogueado;
    private final MainLayoutFrame mainFrame;
    private final JPanel gridEventos;

    public ClienteEventosPanel(Cliente cliente, MainLayoutFrame frame) {
        this.clienteLogueado = cliente;
        this.mainFrame = frame;

        setLayout(new BorderLayout(20, 20));
        setBackground(Theme.CONTENT_BG);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // Header Superior
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setOpaque(false);

        JPanel panelTextos = new JPanel();
        panelTextos.setLayout(new BoxLayout(panelTextos, BoxLayout.Y_AXIS));
        panelTextos.setOpaque(false);

        JLabel lblTitulo = new JLabel("Cartelera de Eventos");
        lblTitulo.setFont(Theme.FONT_TITLE);
        lblTitulo.setForeground(Theme.TEXT_DARK);

        JLabel lblSub = new JLabel("Encuentra conciertos, festivales, deportes y experiencias disponibles.");
        lblSub.setFont(Theme.FONT_BODY);
        lblSub.setForeground(Theme.TEXT_MUTED);

        panelTextos.add(lblTitulo);
        panelTextos.add(Box.createRigidArea(new Dimension(0, 4)));
        panelTextos.add(lblSub);

        JLabel lblTagSeguro = new JLabel("🟢 Compra segura — Sistema en línea activo");
        lblTagSeguro.setOpaque(true);
        lblTagSeguro.setBackground(Theme.TAG_GREEN_BG);
        lblTagSeguro.setForeground(Theme.TAG_GREEN_TEXT);
        lblTagSeguro.setFont(Theme.FONT_BOLD);
        lblTagSeguro.setBorder(new EmptyBorder(8, 14, 8, 14));

        panelHeader.add(panelTextos, BorderLayout.WEST);
        panelHeader.add(lblTagSeguro, BorderLayout.EAST);

        add(panelHeader, BorderLayout.NORTH);

        // Grid de Tarjetas (3 Columnas)
        gridEventos = new JPanel(new GridLayout(0, 3, 20, 20));
        gridEventos.setOpaque(false);

        JScrollPane scrollGrid = new JScrollPane(gridEventos);
        scrollGrid.setBorder(null);
        scrollGrid.getViewport().setOpaque(false);
        scrollGrid.setOpaque(false);
        scrollGrid.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollGrid, BorderLayout.CENTER);

        cargarEventosCartelera();
    }

    public void cargarEventosCartelera() {
        gridEventos.removeAll();

        try (Connection conn = DatabaseConfig.getConnection()) {
            JdbcEventoRepository repoEv = new JdbcEventoRepository();
            JdbcBoletoRepository repoBol = new JdbcBoletoRepository();

            List<Evento> eventos = repoEv.listarActivos(conn);

            if (eventos == null || eventos.isEmpty()) {
                gridEventos.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 50));
                JLabel lblEmpty = new JLabel("No hay eventos activos en este momento.");
                lblEmpty.setFont(Theme.FONT_SUBTITLE);
                lblEmpty.setForeground(Theme.TEXT_MUTED);
                gridEventos.add(lblEmpty);
            } else {
                gridEventos.setLayout(new GridLayout(0, 3, 20, 20));
                for (Evento ev : eventos) {
                    int disponibles = repoBol.buscarDisponiblesPorEvento(conn, ev.getId()).size();
                    gridEventos.add(crearCardEventoCliente(ev, disponibles));
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar la cartelera de eventos: " + ex.getMessage(),
                    "Error de Carga",
                    JOptionPane.ERROR_MESSAGE);
        }

        gridEventos.revalidate();
        gridEventos.repaint();
    }

    private JPanel crearCardEventoCliente(Evento ev, int disponibles) {
        JPanel card = new JPanel();
        card.setBackground(Theme.CARD_BG);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(18, 18, 18, 18)
        ));

        // Título del evento
        JLabel lblNombre = new JLabel(ev.getNombre());
        lblNombre.setFont(Theme.FONT_SUBTITLE);
        lblNombre.setForeground(Theme.TEXT_DARK);
        lblNombre.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Categoría y Clasificación
        String catTexto = (ev.getCategoria() != null ? ev.getCategoria() : "General")
                + " • " + (ev.getClasificacion() != null ? ev.getClasificacion() : "Toda Familia");
        JLabel lblCategoria = new JLabel(catTexto);
        lblCategoria.setFont(Theme.FONT_SMALL);
        lblCategoria.setForeground(Theme.TEXT_MUTED);
        lblCategoria.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Precio base
        JLabel lblPrecio = new JLabel(String.format("Precio desde $%,.2f MXN", ev.getPrecioBase()));
        lblPrecio.setFont(Theme.FONT_BOLD);
        lblPrecio.setForeground(Theme.PRIMARY_BTN);
        lblPrecio.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Indicador de disponibilidad
        JLabel lblDisp = new JLabel(disponibles > 0
                ? "✔ " + disponibles + " boletos disponibles"
                : "✖ Boletos agotados");
        lblDisp.setFont(Theme.FONT_SMALL);
        lblDisp.setForeground(disponibles > 0 ? Theme.TAG_GREEN_TEXT : Color.decode("#DC2626"));
        lblDisp.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Botón Comprar
        JButton btnComprar = new JButton("Comprar boleto");
        btnComprar.setFont(Theme.FONT_BOLD);
        btnComprar.setForeground(Color.WHITE);
        btnComprar.setBackground(disponibles > 0 ? Theme.PRIMARY_BTN : Color.decode("#94A3B8"));
        btnComprar.setFocusPainted(false);
        btnComprar.setBorderPainted(false);
        btnComprar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnComprar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnComprar.setCursor(disponibles > 0 ? new Cursor(Cursor.HAND_CURSOR) : new Cursor(Cursor.DEFAULT_CURSOR));
        btnComprar.setEnabled(disponibles > 0);

        if (disponibles > 0) {
            btnComprar.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    btnComprar.setBackground(Theme.PRIMARY_BTN_HOVER);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    btnComprar.setBackground(Theme.PRIMARY_BTN);
                }
            });
            btnComprar.addActionListener(e -> procesarCompraBoleto(ev));
        }

        card.add(lblNombre);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(lblCategoria);
        card.add(Box.createRigidArea(new Dimension(0, 12)));
        card.add(lblPrecio);
        card.add(Box.createRigidArea(new Dimension(0, 2)));
        card.add(lblDisp);
        card.add(Box.createRigidArea(new Dimension(0, 16)));
        card.add(btnComprar);

        return card;
    }

    private void procesarCompraBoleto(Evento ev) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            JdbcBoletoRepository repoBol = new JdbcBoletoRepository();
            JdbcCuentaClienteRepository repoCta = new JdbcCuentaClienteRepository();

            List<Boleto> disp = repoBol.buscarDisponiblesPorEvento(conn, ev.getId());
            List<CuentaCliente> cuentas = repoCta.buscarPorClienteId(conn, clienteLogueado.getId());

            if (disp.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Ya no hay boletos disponibles para este evento.",
                        "Boletos Agotados",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (cuentas.isEmpty()) {
                int resp = JOptionPane.showConfirmDialog(this,
                        "No tienes cuentas bancarias asociadas.\n¿Deseas ir a la sección 'Cuentas bancarias' para agregar una?",
                        "Sin Método de Pago",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (resp == JOptionPane.YES_OPTION && mainFrame != null) {
                    mainFrame.navegarA("CUENTAS");
                }
                return;
            }

            CuentaItem[] opcionesCuentas = cuentas.stream()
                    .map(CuentaItem::new)
                    .toArray(CuentaItem[]::new);

            CuentaItem cuentaSeleccionada = (CuentaItem) JOptionPane.showInputDialog(
                    this,
                    String.format("Evento: %s\nPrecio Total: $%,.2f MXN\n\nSelecciona la cuenta con la que deseas pagar:",
                            ev.getNombre(), ev.getPrecioBase()),
                    "Confirmar Compra de Boleto",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opcionesCuentas,
                    opcionesCuentas[0]
            );

            if (cuentaSeleccionada == null) {
                return;
            }

            Boleto boletoAComprar = disp.get(0);

            VentaService ventaService = new VentaService(
                    repoBol,
                    repoCta,
                    new JdbcCompraRepository(),
                    new JdbcOperacionCuentaRepository()
            );

            CompraRequestDTO dto = new CompraRequestDTO();
            dto.setIdCliente(clienteLogueado.getId());
            dto.setIdCuentaCliente(cuentaSeleccionada.getCuenta().getId());
            dto.setIdBoleto(boletoAComprar.getId());

            ventaService.procesarCompra(dto);

            JOptionPane.showMessageDialog(this,
                    "¡Compra realizada con éxito!\nTu boleto digital ya está disponible.",
                    "Compra Exitosa",
                    JOptionPane.INFORMATION_MESSAGE);

            cargarEventosCartelera();

            if (mainFrame != null) {
                mainFrame.navegarA("MIS_BOLETOS");
            }

        } catch (InsufficientBalanceException ex) {
            JOptionPane.showMessageDialog(this,
                    "Saldo insuficiente en la cuenta seleccionada.\nPor favor elige otra tarjeta o recarga saldo.",
                    "Saldo Insuficiente",
                    JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al procesar la compra: " + ex.getMessage(),
                    "Error en Transacción",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class CuentaItem {

        private final CuentaCliente cuenta;

        public CuentaItem(CuentaCliente cuenta) {
            this.cuenta = cuenta;
        }

        public CuentaCliente getCuenta() {
            return cuenta;
        }

        @Override
        public String toString() {
            String num = cuenta.getNumeroCuenta();
            String ultimos4 = (num != null && num.length() >= 4) ? num.substring(num.length() - 4) : "****";
            String banco = cuenta.getBanco() != null ? cuenta.getBanco() : "Banco";
            return String.format("%s (**** %s) — Saldo: $%,.2f MXN", banco, ultimos4, cuenta.getSaldo());
        }
    }
}
