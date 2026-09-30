package com.tutiket.view.dialog;

import com.tutiket.domain.Boleto;
import com.tutiket.domain.Cliente;
import com.tutiket.domain.Evento;
import com.tutiket.service.BoletoPdfService;
import com.tutiket.view.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BoletoImpresionDialog extends JDialog {

    private final Cliente cliente;
    private final Evento evento;
    private final String folio;
    private final BigDecimal precio;

    private final BoletoPdfService boletoPdfService = new BoletoPdfService();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private VistaPreviaBoletoPanel panelVistaPrevia;

    public BoletoImpresionDialog(Window parent, Cliente cliente, Evento evento, String folio, BigDecimal precio) {
        super(parent, "Vista Previa de Impresión — TuTiket", ModalityType.APPLICATION_MODAL);
        this.cliente = cliente;
        this.evento = evento;
        this.folio = (folio != null && !folio.trim().isEmpty()) ? folio : "TT-20261012-1842";
        this.precio = (precio != null) ? precio : BigDecimal.ZERO;

        initUI();
    }

    private void initUI() {
        setSize(560, 720);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        setLayout(new BorderLayout());

        // Header Superior
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(241, 245, 249));
        panelHeader.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel lblTitulo = new JLabel("🖨️ Vista Previa del Boleto Digital");
        lblTitulo.setFont(Theme.FONT_SUBTITLE);
        lblTitulo.setForeground(Theme.TEXT_DARK);

        JLabel lblSubtitulo = new JLabel("Documento oficial listo para enviar a imprimir o exportar a PDF.");
        lblSubtitulo.setFont(Theme.FONT_SMALL);
        lblSubtitulo.setForeground(Theme.TEXT_MUTED);

        JPanel panelTextoHeader = new JPanel();
        panelTextoHeader.setLayout(new BoxLayout(panelTextoHeader, BoxLayout.Y_AXIS));
        panelTextoHeader.setOpaque(false);
        panelTextoHeader.add(lblTitulo);
        panelTextoHeader.add(Box.createRigidArea(new Dimension(0, 3)));
        panelTextoHeader.add(lblSubtitulo);

        panelHeader.add(panelTextoHeader, BorderLayout.WEST);
        add(panelHeader, BorderLayout.NORTH);

        // Centro: Vista previa del boleto con scroll
        panelVistaPrevia = new VistaPreviaBoletoPanel();

        JPanel panelContenedorCentro = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        panelContenedorCentro.setBackground(Theme.CONTENT_BG);
        panelContenedorCentro.add(panelVistaPrevia);

        JScrollPane scrollPane = new JScrollPane(panelContenedorCentro);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Barra Inferior de Acciones
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        panelAcciones.setBackground(Color.WHITE);
        panelAcciones.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setFont(Theme.FONT_BODY);
        btnCerrar.setForeground(Theme.TEXT_DARK);
        btnCerrar.setBackground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(8, 16, 8, 16)
        ));
        btnCerrar.addActionListener(e -> dispose());

        JButton btnPdf = new JButton("📄 Guardar PDF");
        btnPdf.setFont(Theme.FONT_BOLD);
        btnPdf.setForeground(Theme.PRIMARY_BTN);
        btnPdf.setBackground(Color.WHITE);
        btnPdf.setFocusPainted(false);
        btnPdf.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPdf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.PRIMARY_BTN, 1),
                new EmptyBorder(8, 16, 8, 16)
        ));
        
        btnPdf.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnPdf.setBackground(new Color(238, 242, 255));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnPdf.setBackground(Color.WHITE);
            }
        });
        btnPdf.addActionListener(e -> descargarPdf());

        JButton btnImprimir = new JButton("🖨️ Imprimir Directo");
        btnImprimir.setFont(Theme.FONT_BOLD);
        btnImprimir.setForeground(Color.WHITE);
        btnImprimir.setBackground(Theme.PRIMARY_BTN);
        btnImprimir.setFocusPainted(false);
        btnImprimir.setBorderPainted(false);
        btnImprimir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnImprimir.setBorder(new EmptyBorder(9, 18, 9, 18));

        btnImprimir.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnImprimir.setBackground(Theme.PRIMARY_BTN_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnImprimir.setBackground(Theme.PRIMARY_BTN);
            }
        });
        btnImprimir.addActionListener(e -> ejecutarImpresion());

        panelAcciones.add(btnCerrar);
        panelAcciones.add(btnPdf);
        panelAcciones.add(btnImprimir);

        add(panelAcciones, BorderLayout.SOUTH);
    }

    private void ejecutarImpresion() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Boleto TuTiket - " + folio);

        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) {
                return Printable.NO_SUCH_PAGE;
            }

            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX() + 20, pageFormat.getImageableY() + 20);

            double scale = Math.min(
                    pageFormat.getImageableWidth() / panelVistaPrevia.getWidth(),
                    pageFormat.getImageableHeight() / panelVistaPrevia.getHeight()
            );
            if (scale < 1.0) {
                g2d.scale(scale, scale);
            }

            panelVistaPrevia.printAll(g2d);
            return Printable.PAGE_EXISTS;
        });

        boolean doPrint = job.printDialog();
        if (doPrint) {
            try {
                job.print();
                JOptionPane.showMessageDialog(this,
                        "Envío a la impresora completado con éxito.",
                        "Impresión Correcta", JOptionPane.INFORMATION_MESSAGE);
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(this,
                        "Error al imprimir el documento: " + e.getMessage(),
                        "Error de Impresión", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void descargarPdf() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Boleto Digital (PDF)");
        fileChooser.setSelectedFile(new File("Boleto_" + folio + ".pdf"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File archivoDestino = fileChooser.getSelectedFile();
            if (!archivoDestino.getName().toLowerCase().endsWith(".pdf")) {
                archivoDestino = new File(archivoDestino.getAbsolutePath() + ".pdf");
            }

            try {
                Boleto boletoObj = new Boleto();
                boletoObj.setFolio(folio);
                boletoObj.setPrecio(precio);
                boletoObj.setZona("General");

                String nombreEvento = (evento != null && evento.getNombre() != null) ? evento.getNombre() : "Evento TuTiket";
                String nombreCliente = (cliente != null && cliente.getNombre() != null) ? cliente.getNombre() : "Cliente General";

                boletoPdfService.guardarPdfEnArchivo(boletoObj, nombreEvento, nombreCliente, archivoDestino);

                JOptionPane.showMessageDialog(this,
                        "¡Boleto PDF guardado con éxito en:\n" + archivoDestino.getAbsolutePath(),
                        "Descarga Exitosa", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error al generar el PDF: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Panel Interno: Plantilla de Vista Previa del Pase
    private class VistaPreviaBoletoPanel extends JPanel {

        public VistaPreviaBoletoPanel() {
            setPreferredSize(new Dimension(440, 520));
            setBackground(Color.WHITE);
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                    new EmptyBorder(22, 22, 22, 22)
            ));

            String nombreEvento = (evento != null && evento.getNombre() != null) ? evento.getNombre() : "Evento TuTiket";
            String lugarEvento = (evento != null && evento.getLugar() != null) ? evento.getLugar() : "Ubicación General";
            String fechaHoraStr = (evento != null && evento.getFechaHora() != null)
                    ? evento.getFechaHora().format(DATE_FORMATTER)
                    : LocalDateTime.now().plusDays(15).format(DATE_FORMATTER);
            String nombreTitular = (cliente != null && cliente.getNombre() != null) ? cliente.getNombre() : "Cliente Registrado";

            // Header del Boleto
            JPanel panelTop = new JPanel(new BorderLayout());
            panelTop.setOpaque(false);

            JLabel lblBrand = new JLabel("🎟 TUTIKET OFFICIAL TICKET");
            lblBrand.setFont(Theme.FONT_BOLD);
            lblBrand.setForeground(Theme.PRIMARY_BTN);

            JLabel lblFolioTxt = new JLabel("FOLIO: " + folio);
            lblFolioTxt.setFont(Theme.FONT_SMALL);
            lblFolioTxt.setForeground(Theme.TEXT_MUTED);

            panelTop.add(lblBrand, BorderLayout.WEST);
            panelTop.add(lblFolioTxt, BorderLayout.EAST);

            // Cuerpo del Boleto
            JPanel panelCuerpo = new JPanel();
            panelCuerpo.setLayout(new BoxLayout(panelCuerpo, BoxLayout.Y_AXIS));
            panelCuerpo.setOpaque(false);
            panelCuerpo.setBorder(new EmptyBorder(15, 0, 15, 0));

            JLabel lblEv = new JLabel(nombreEvento);
            lblEv.setFont(Theme.FONT_TITLE);
            lblEv.setForeground(Theme.TEXT_DARK);
            lblEv.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel lblLug = new JLabel("📍 " + lugarEvento);
            lblLug.setFont(Theme.FONT_BODY);
            lblLug.setForeground(Theme.TEXT_MUTED);
            lblLug.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel lblFec = new JLabel("📅 " + fechaHoraStr + " HRS");
            lblFec.setFont(Theme.FONT_BOLD);
            lblFec.setForeground(Theme.TEXT_DARK);
            lblFec.setAlignmentX(Component.CENTER_ALIGNMENT);

            JSeparator sep1 = new JSeparator();
            sep1.setMaximumSize(new Dimension(380, 2));

            // Grid de Info Detallada
            JPanel panelGridInfo = new JPanel(new GridLayout(2, 2, 10, 8));
            panelGridInfo.setOpaque(false);
            panelGridInfo.setMaximumSize(new Dimension(380, 60));

            panelGridInfo.add(crearCampoData("TITULAR", nombreTitular));
            panelGridInfo.add(crearCampoData("PRECIO", String.format("$%,.2f MXN", precio)));
            panelGridInfo.add(crearCampoData("ZONA / ACCESO", "GENERAL — ACCESO A"));
            panelGridInfo.add(crearCampoData("ESTADO", "CONFIRMADO / PAGADO"));

            // Códigos (Barras y QR)
            JPanel panelCodigos = new JPanel(new BorderLayout(10, 0));
            panelCodigos.setOpaque(false);
            panelCodigos.setMaximumSize(new Dimension(380, 120));

            BarCode128MiniPanel panelBarcode = new BarCode128MiniPanel(folio);
            QRCodeMiniPanel panelQR = new QRCodeMiniPanel(folio);

            panelCodigos.add(panelBarcode, BorderLayout.CENTER);
            panelCodigos.add(panelQR, BorderLayout.EAST);

            // Instrucciones / Legal
            JLabel lblLegal = new JLabel("<html><center>Este boleto es personal e intransferible. Presente este documento impreso o digital al ingresar al recinto.</center></html>");
            lblLegal.setFont(Theme.FONT_SMALL);
            lblLegal.setForeground(Theme.TEXT_MUTED);
            lblLegal.setAlignmentX(Component.CENTER_ALIGNMENT);

            panelCuerpo.add(lblEv);
            panelCuerpo.add(Box.createRigidArea(new Dimension(0, 4)));
            panelCuerpo.add(lblLug);
            panelCuerpo.add(Box.createRigidArea(new Dimension(0, 4)));
            panelCuerpo.add(lblFec);
            panelCuerpo.add(Box.createRigidArea(new Dimension(0, 12)));
            panelCuerpo.add(sep1);
            panelCuerpo.add(Box.createRigidArea(new Dimension(0, 12)));
            panelCuerpo.add(panelGridInfo);
            panelCuerpo.add(Box.createRigidArea(new Dimension(0, 15)));
            panelCuerpo.add(panelCodigos);
            panelCuerpo.add(Box.createRigidArea(new Dimension(0, 15)));
            panelCuerpo.add(lblLegal);

            add(panelTop, BorderLayout.NORTH);
            add(panelCuerpo, BorderLayout.CENTER);
        }

        private JPanel crearCampoData(String label, String valor) {
            JPanel p = new JPanel(new GridLayout(2, 1, 0, 1));
            p.setOpaque(false);

            JLabel l = new JLabel(label);
            l.setFont(new Font("SansSerif", Font.BOLD, 9));
            l.setForeground(Theme.TEXT_MUTED);

            JLabel v = new JLabel(valor);
            v.setFont(new Font("SansSerif", Font.PLAIN, 12));
            v.setForeground(Theme.TEXT_DARK);

            p.add(l);
            p.add(v);
            return p;
        }
    }

    // Componente de Código de Barras
    private static class BarCode128MiniPanel extends JPanel {

        private final String code;

        public BarCode128MiniPanel(String code) {
            this.code = code;
            setPreferredSize(new Dimension(240, 100));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight() - 22;

            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, width, getHeight());
            g2.setColor(Color.BLACK);

            int startX = 10;
            int endX = width - 10;
            int seed = Math.abs(code.hashCode());

            g2.fillRect(startX, 5, 2, height);
            startX += 4;
            g2.fillRect(startX, 5, 1, height);
            startX += 3;

            byte[] bytes = code.getBytes();
            int idx = 0;

            while (startX < endX - 10) {
                int val = (bytes[idx % bytes.length] + seed + idx * 7) % 5;
                int barWidth = (val % 2) + 1;
                int gapWidth = ((val + 1) % 2) + 1;

                g2.fillRect(startX, 5, barWidth, height);
                startX += barWidth + gapWidth;
                idx++;
            }

            g2.fillRect(endX - 6, 5, 2, height);
            g2.fillRect(endX - 3, 5, 1, height);

            g2.setFont(new Font("Monospaced", Font.BOLD, 11));
            FontMetrics fm = g2.getFontMetrics();
            int textX = (width - fm.stringWidth(code)) / 2;
            g2.drawString(code, textX, height + 16);

            g2.dispose();
        }
    }

    // Componente de Código QR
    private static class QRCodeMiniPanel extends JPanel {

        private final String data;

        public QRCodeMiniPanel(String data) {
            this.data = data;
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
                        if (r == 0 || r == 2 || c == 0 || c == 2
                                || r == matrixSize - 1 || r == matrixSize - 3 || c == matrixSize - 1 || c == matrixSize - 3) {
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
}