package com.tutiket.view.promotora;

import com.tutiket.domain.Evento;
import com.tutiket.domain.enums.EstadoEvento;
import com.tutiket.repository.impl.JdbcBoletoRepository;
import com.tutiket.repository.impl.JdbcEventoRepository;
import com.tutiket.service.EventoService;
import com.tutiket.view.Theme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PromotoraCrearEventoDialog extends JDialog {

    private JTextField txtNombre;
    private JComboBox<String> cbCategoria;
    private JComboBox<String> cbClasificacion;
    private JTextField txtLugar;
    private JTextField txtFecha;
    private JTextField txtPrecioBase;
    private JSpinner spBoletos;

    private boolean eventoCreado = false;
    private final EventoService eventoService;
    private final Long idPromotora;

    public PromotoraCrearEventoDialog(Window parent, Long idPromotora) {
        super(parent, "Registrar Nuevo Evento", ModalityType.APPLICATION_MODAL);
        this.idPromotora = idPromotora;
        this.eventoService = new EventoService(
                new JdbcEventoRepository(),
                new JdbcBoletoRepository()
        );

        initUI();
    }

    private void initUI() {
        setSize(520, 680);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());
        setResizable(false);

        // Content Panel contenedor con scroll por si la pantalla es reducida
        JPanel panelForm = new JPanel();
        panelForm.setLayout(new BoxLayout(panelForm, BoxLayout.Y_AXIS));
        panelForm.setBackground(Theme.CARD_BG);
        panelForm.setBorder(new EmptyBorder(25, 30, 20, 30));

        // Encabezado
        JLabel lblTitulo = new JLabel("Crear Nuevo Evento");
        lblTitulo.setFont(Theme.FONT_TITLE);
        lblTitulo.setForeground(Theme.TEXT_DARK);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Llena los campos para publicar el evento y generar sus boletos.");
        lblSub.setFont(Theme.FONT_BODY);
        lblSub.setForeground(Theme.TEXT_MUTED);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelForm.add(lblTitulo);
        panelForm.add(Box.createRigidArea(new Dimension(0, 4)));
        panelForm.add(lblSub);
        panelForm.add(Box.createRigidArea(new Dimension(0, 20)));

        // Inicialización de componentes del formulario
        txtNombre = crearCampoTexto();
        cbCategoria = crearComboBox(new String[]{"Conciertos", "Festivales", "Deportes", "Teatro", "Especiales"});
        cbClasificacion = crearComboBox(new String[]{"Todo Público", "+13", "+15", "+18"});
        txtLugar = crearCampoTexto();
        txtFecha = crearCampoTexto();
        txtFecha.setToolTipText("Ejemplo: 2026-11-20 20:00");
        txtFecha.setText("2026-12-01 20:00");

        txtPrecioBase = crearCampoTexto();
        txtPrecioBase.setText("500.00");

        spBoletos = new JSpinner(new SpinnerNumberModel(50, 1, 10000, 10));
        estilarSpinner(spBoletos);

        // Construcción de la forma
        agregarCampoForm(panelForm, "Nombre del Evento", txtNombre);
        agregarCampoForm(panelForm, "Categoría", cbCategoria);
        agregarCampoForm(panelForm, "Clasificación", cbClasificacion);
        agregarCampoForm(panelForm, "Lugar / Recinto", txtLugar);
        agregarCampoForm(panelForm, "Fecha y Hora (AAAA-MM-DD HH:MM)", txtFecha);
        agregarCampoForm(panelForm, "Precio por Boleto ($ MXN)", txtPrecioBase);
        agregarCampoForm(panelForm, "Cantidad de Boletos a Generar", spBoletos);

        JScrollPane scrollPane = new JScrollPane(panelForm);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setBackground(Theme.CARD_BG);

        // Panel de Botones Inferior
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 16));
        panelBotones.setBackground(new Color(248, 250, 252));
        panelBotones.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(Theme.FONT_BOLD);
        btnCancelar.setForeground(Theme.TEXT_MUTED);
        btnCancelar.setBackground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(203, 213, 225), 1, true),
                new EmptyBorder(8, 18, 8, 18)
        ));

        btnCancelar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnCancelar.setBackground(new Color(241, 245, 249));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnCancelar.setBackground(Color.WHITE);
            }
        });
        btnCancelar.addActionListener(e -> dispose());

        JButton btnGuardar = new JButton("Guardar y Publicar");
        btnGuardar.setFont(Theme.FONT_BOLD);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setBackground(Theme.PRIMARY_BTN);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setBorderPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.setBorder(new EmptyBorder(9, 20, 9, 20));

        btnGuardar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnGuardar.setBackground(Theme.PRIMARY_BTN.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnGuardar.setBackground(Theme.PRIMARY_BTN);
            }
        });
        btnGuardar.addActionListener(e -> guardarEvento());

        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);

        add(scrollPane, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private JTextField crearCampoTexto() {
        JTextField tf = new JTextField();
        tf.setFont(Theme.FONT_BODY);
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        tf.setPreferredSize(new Dimension(0, 36));
        tf.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        return tf;
    }

    private JComboBox<String> crearComboBox(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(Theme.FONT_BODY);
        cb.setBackground(Color.WHITE);
        cb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cb.setPreferredSize(new Dimension(0, 36));
        cb.setAlignmentX(Component.LEFT_ALIGNMENT);
        return cb;
    }

    private void estilarSpinner(JSpinner spinner) {
        spinner.setFont(Theme.FONT_BODY);
        spinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        spinner.setPreferredSize(new Dimension(0, 36));
        spinner.setAlignmentX(Component.LEFT_ALIGNMENT);
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
            tf.setFont(Theme.FONT_BODY);
            tf.setBorder(new EmptyBorder(4, 8, 4, 8));
        }
    }

    private void agregarCampoForm(JPanel parent, String etiqueta, JComponent campo) {
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(Theme.FONT_BOLD);
        lbl.setForeground(Theme.TEXT_DARK);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        parent.add(lbl);
        parent.add(Box.createRigidArea(new Dimension(0, 6)));
        parent.add(campo);
        parent.add(Box.createRigidArea(new Dimension(0, 14)));
    }

    private void guardarEvento() {
        if (idPromotora == null) {
            JOptionPane.showMessageDialog(this, "Error de sesión: No se identificó a la promotora activa.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nombre = txtNombre.getText().trim();
        String lugar = txtLugar.getText().trim();
        String fechaStr = txtFecha.getText().trim();
        String precioStr = txtPrecioBase.getText().trim();
        int cantidadBoletos = (int) spBoletos.getValue();

        if (nombre.isEmpty() || lugar.isEmpty() || fechaStr.isEmpty() || precioStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor completa todos los campos.", "Campos Incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            BigDecimal precio = new BigDecimal(precioStr);
            if (precio.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "El precio del boleto debe ser mayor a $0.00.", "Precio Inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Evento evento = new Evento();
            evento.setIdPromotora(this.idPromotora);
            evento.setNombre(nombre);
            evento.setCategoria((String) cbCategoria.getSelectedItem());
            evento.setClasificacion((String) cbClasificacion.getSelectedItem());
            evento.setLugar(lugar);
            evento.setPrecioBase(precio);
            evento.setCantidadBoletos(cantidadBoletos);
            evento.setEstado(EstadoEvento.ACTIVO);

            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                evento.setFechaHora(LocalDateTime.parse(fechaStr, formatter));
            } catch (Exception parseEx) {
                evento.setFechaHora(LocalDateTime.now().plusDays(30));
            }

            eventoService.registrarEventoConBoletos(evento, cantidadBoletos);

            eventoCreado = true;
            JOptionPane.showMessageDialog(this,
                    "¡Evento publicado exitosamente!\nSe generaron " + cantidadBoletos + " boletos en estado DISPONIBLE.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El precio ingresado no tiene un formato válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al crear el evento: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isEventoCreado() {
        return eventoCreado;
    }
}