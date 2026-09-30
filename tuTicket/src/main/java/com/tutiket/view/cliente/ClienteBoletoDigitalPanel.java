package com.tutiket.view.cliente;

import com.tutiket.domain.Boleto;
import com.tutiket.domain.Cliente;
import com.tutiket.domain.Evento;
import com.tutiket.service.BoletoPdfService;
import com.tutiket.view.dialog.BoletoImpresionDialog;
import com.tutiket.view.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClienteBoletoDigitalPanel extends JPanel {

    private final JLabel lblEvento;
    private final JLabel lblFechas;
    private final JLabel lblLugar;
    private final JLabel lblTitular;
    private final JLabel lblFolio;
    private final JLabel lblCode;
    private final JPanel panelQRContainer;
    private final JPanel ticketCard;

    private Cliente clienteActual;
    private Evento eventoActual;
    private String folioActual;
    private BigDecimal precioActual;

    private final BoletoPdfService boletoPdfService = new BoletoPdfService();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ClienteBoletoDigitalPanel() {
        this(null, null, "TT-20261012-1842", new BigDecimal("3500.00"));
    }

    public ClienteBoletoDigitalPanel(Cliente cliente, Evento evento, String folioBoleto, BigDecimal precio) {
        setLayout(new BorderLayout(20, 20));
        setBackground(Theme.CONTENT_BG);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // Header Superior
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setOpaque(false);

        JLabel lblTitulo = new JLabel("Tu Boleto Digital");
        lblTitulo.setFont(Theme.FONT_TITLE);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);

        JButton btnVerImpresion = new JButton("🖨️ Vista Previa / Imprimir");
        btnVerImpresion.setFont(Theme.FONT_BOLD);
        btnVerImpresion.setForeground(Theme.PRIMARY_BTN);
        btnVerImpresion.setBackground(Color.WHITE);
        btnVerImpresion.setFocusPainted(false);
        btnVerImpresion.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVerImpresion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.PRIMARY_BTN, 1),
                new EmptyBorder(8, 15, 8, 15)
        ));
        btnVerImpresion.addActionListener(e -> abrirDialogoImpresion());

        JButton btnDescargarPdf = new JButton("📄 Descargar PDF");
        btnDescargarPdf.setFont(Theme.FONT_BOLD);
        btnDescargarPdf.setForeground(Color.WHITE);
        btnDescargarPdf.setBackground(Theme.PRIMARY_BTN);
        btnDescargarPdf.setFocusPainted(false);
        btnDescargarPdf.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDescargarPdf.setBorder(new EmptyBorder(8, 15, 8, 15));
        btnDescargarPdf.addActionListener(e -> descargarBoletoPdfDirecto());

        panelBotones.add(btnVerImpresion);
        panelBotones.add(btnDescargarPdf);

        panelHeader.add(lblTitulo, BorderLayout.WEST);
        panelHeader.add(panelBotones, BorderLayout.EAST);

        add(panelHeader, BorderLayout.NORTH);

        // Tarjeta Digital Estilo Entrada
        JPanel panelTarjetaBoleto = new JPanel(new GridBagLayout());
        panelTarjetaBoleto.setOpaque(false);

        ticketCard = new JPanel(new BorderLayout());
        ticketCard.setPreferredSize(new Dimension(720, 240));
        ticketCard.setBackground(Color.WHITE);
        ticketCard.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));

        // Sección Izquierda: Datos del Evento
        JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
        panelInfo.setBackground(Color.WHITE);
        panelInfo.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel lblBrand = new JLabel("🎟 TuTiket Entrada Digital");
        lblBrand.setFont(Theme.FONT_BOLD);
        lblBrand.setForeground(Theme.PRIMARY_BTN);

        lblEvento = new JLabel();
        lblEvento.setFont(Theme.FONT_SUBTITLE);

        lblLugar = new JLabel();
        lblLugar.setFont(Theme.FONT_BODY);
        lblLugar.setForeground(Theme.TEXT_MUTED);

        lblFechas = new JLabel();
        lblFechas.setFont(Theme.FONT_SMALL);
        lblFechas.setForeground(Theme.TEXT_MUTED);

        lblTitular = new JLabel();
        lblTitular.setFont(Theme.FONT_BOLD);

        lblFolio = new JLabel();
        lblFolio.setFont(Theme.FONT_SMALL);
        lblFolio.setForeground(Theme.TEXT_MUTED);

        panelInfo.add(lblBrand);
        panelInfo.add(Box.createRigidArea(new Dimension(0, 8)));
        panelInfo.add(lblEvento);
        panelInfo.add(Box.createRigidArea(new Dimension(0, 4)));
        panelInfo.add(lblLugar);
        panelInfo.add(Box.createRigidArea(new Dimension(0, 6)));
        panelInfo.add(lblFechas);
        panelInfo.add(Box.createRigidArea(new Dimension(0, 12)));
        panelInfo.add(lblTitular);
        panelInfo.add(lblFolio);

        // Separador Punteado
        DottedSeparatorPanel panelSeparador = new DottedSeparatorPanel();

        // Sección Derecha: QR y Código Único
        JPanel panelQR = new JPanel();
        panelQR.setLayout(new BoxLayout(panelQR, BoxLayout.Y_AXIS));
        panelQR.setBackground(new Color(248, 250, 252));
        panelQR.setBorder(new EmptyBorder(15, 15, 15, 15));
        panelQR.setPreferredSize(new Dimension(210, 240));

        JLabel lblCodeTitle = new JLabel("CÓDIGO ÚNICO");
        lblCodeTitle.setFont(Theme.FONT_SMALL);
        lblCodeTitle.setForeground(Theme.TEXT_MUTED);
        lblCodeTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelQRContainer = new JPanel(new BorderLayout());
        panelQRContainer.setOpaque(false);
        panelQRContainer.setMaximumSize(new Dimension(100, 100));

        lblCode = new JLabel();
        lblCode.setFont(Theme.FONT_BOLD);
        lblCode.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblNote = new JLabel("Clic para vista de impresión");
        lblNote.setFont(Theme.FONT_SMALL);
        lblNote.setForeground(Theme.PRIMARY_BTN);
        lblNote.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelQR.add(lblCodeTitle);
        panelQR.add(Box.createRigidArea(new Dimension(0, 6)));
        panelQR.add(panelQRContainer);
        panelQR.add(Box.createRigidArea(new Dimension(0, 8)));
        panelQR.add(lblCode);
        panelQR.add(Box.createRigidArea(new Dimension(0, 2)));
        panelQR.add(lblNote);

        ticketCard.add(panelInfo, BorderLayout.CENTER);
        ticketCard.add(panelSeparador, BorderLayout.WEST);
        ticketCard.add(panelQR, BorderLayout.EAST);

        // Asignación de evento de clic recursivo a la tarjeta
        MouseAdapter mouseClicAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                abrirDialogoImpresion();
            }
        };
        ticketCard.setToolTipText("Haz clic aquí para abrir la vista previa de impresión");
        agregarEventoClicRecursivo(ticketCard, mouseClicAdapter);

        panelTarjetaBoleto.add(ticketCard);
        add(panelTarjetaBoleto, BorderLayout.CENTER);

        cargarDatosBoleto(cliente, evento, folioBoleto, precio);
    }

    private void agregarEventoClicRecursivo(Component comp, MouseAdapter adapter) {
        comp.setCursor(new Cursor(Cursor.HAND_CURSOR));
        comp.addMouseListener(adapter);
        if (comp instanceof Container) {
            for (Component hijo : ((Container) comp).getComponents()) {
                agregarEventoClicRecursivo(hijo, adapter);
            }
        }
    }

    public void cargarDatosBoleto(Cliente cliente, Evento evento, String folio, BigDecimal precio) {
        this.clienteActual = cliente;
        this.eventoActual = evento;
        this.folioActual = (folio != null && !folio.trim().isEmpty()) ? folio : "TT-20261012-1842";
        this.precioActual = (precio != null) ? precio : (evento != null && evento.getPrecioBase() != null ? evento.getPrecioBase() : new BigDecimal("3500.00"));

        String nombreEvento = (evento != null && evento.getNombre() != null) ? evento.getNombre() : "Ariel camacho y sus plebes del itson";
        String lugarEvento = (evento != null && evento.getLugar() != null) ? "Lugar: " + evento.getLugar() : "Lugar: ITSON";

        String fechaHoraStr = (evento != null && evento.getFechaHora() != null)
                ? evento.getFechaHora().format(DATE_FORMATTER)
                : LocalDateTime.now().plusDays(15).format(DATE_FORMATTER);

        String nombreTitular = (cliente != null && cliente.getNombre() != null)
                ? cliente.getNombre(): "Domitsu xd";

        lblEvento.setText(nombreEvento);
        lblLugar.setText(lugarEvento);
        lblFechas.setText(String.format("FECHA/HORA: %s   |   PRECIO: $%.2f MXN", fechaHoraStr, precioActual));
        lblTitular.setText("TITULAR: " + nombreTitular);
        lblFolio.setText("Folio: " + folioActual);
        lblCode.setText(folioActual);

        panelQRContainer.removeAll();
        panelQRContainer.add(new MiniQRCodePanel(folioActual), BorderLayout.CENTER);
        panelQRContainer.revalidate();
        panelQRContainer.repaint();
    }

    private void abrirDialogoImpresion() {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        BoletoImpresionDialog dialog = new BoletoImpresionDialog(
                parentWindow,
                clienteActual,
                eventoActual,
                folioActual,
                precioActual
        );
        dialog.setVisible(true);
    }

    private void descargarBoletoPdfDirecto() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Boleto Digital (PDF)");
        fileChooser.setSelectedFile(new File("Boleto_" + folioActual + ".pdf"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File archivoDestino = fileChooser.getSelectedFile();
            if (!archivoDestino.getName().toLowerCase().endsWith(".pdf")) {
                archivoDestino = new File(archivoDestino.getAbsolutePath() + ".pdf");
            }

            try {
                Boleto boleto = new Boleto();
                boleto.setFolio(folioActual);
                boleto.setPrecio(precioActual);
                boleto.setZona("General");

                String nombreEvento = (eventoActual != null && eventoActual.getNombre() != null)
                        ? eventoActual.getNombre() : lblEvento.getText();

                String nombreCliente = (clienteActual != null && clienteActual.getNombre() != null)
                        ? clienteActual.getNombre(): "Ana García López";

                boletoPdfService.guardarPdfEnArchivo(boleto, nombreEvento, nombreCliente, archivoDestino);

                JOptionPane.showMessageDialog(this,
                        "¡Boleto PDF generado exitosamente en:\n" + archivoDestino.getAbsolutePath(),
                        "Descarga Completada", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error al generar el archivo PDF: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static class MiniQRCodePanel extends JPanel {
        private final String data;

        public MiniQRCodePanel(String data) {
            this.data = data != null ? data : "TUTIKET";
            setPreferredSize(new Dimension(100, 100));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();

            int size = Math.min(getWidth(), getHeight());
            int matrixSize = 13;
            int cellSize = size / matrixSize;
            int offset = (size - (cellSize * matrixSize)) / 2;

            g2d.setColor(Color.WHITE);
            g2d.fillRect(offset, offset, cellSize * matrixSize, cellSize * matrixSize);
            g2d.setColor(Color.BLACK);

            int hash = data.hashCode();

            for (int r = 0; r < matrixSize; r++) {
                for (int c = 0; c < matrixSize; c++) {
                    if ((r < 3 && c < 3) || (r < 3 && c >= matrixSize - 3) || (r >= matrixSize - 3 && c < 3)) {
                        if (r == 0 || r == 2 || c == 0 || c == 2 ||
                            r == matrixSize - 1 || r == matrixSize - 3 || c == matrixSize - 1 || c == matrixSize - 3) {
                            g2d.fillRect(offset + c * cellSize, offset + r * cellSize, cellSize, cellSize);
                        }
                    } else {
                        boolean isBlack = ((hash ^ (r * 31 + c * 17)) & 1) == 0;
                        if (isBlack) {
                            g2d.fillRect(offset + c * cellSize, offset + r * cellSize, cellSize, cellSize);
                        }
                    }
                }
            }
            g2d.dispose();
        }
    }

    private static class DottedSeparatorPanel extends JPanel {
        public DottedSeparatorPanel() {
            setPreferredSize(new Dimension(10, 240));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(203, 213, 225));
            Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4}, 0);
            g2.setStroke(dashed);
            g2.drawLine(getWidth() / 2, 10, getWidth() / 2, getHeight() - 10);
            g2.dispose();
        }
    }
}