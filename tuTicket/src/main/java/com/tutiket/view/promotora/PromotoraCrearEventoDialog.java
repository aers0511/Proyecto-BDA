package com.tutiket.view.promotora;

import com.tutiket.domain.Evento;
import com.tutiket.domain.enums.EstadoEvento;
import com.tutiket.repository.impl.JdbcBoletoRepository;
import com.tutiket.repository.impl.JdbcEventoRepository;
import com.tutiket.service.EventoService;
import com.tutiket.view.Theme;
import com.tutiket.view.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
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
    private final Long idPromotora; // 👈 Guarda la promotora actual

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
        setSize(480, 580);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());
        setResizable(false);

        JPanel panelForm = new JPanel();
        panelForm.setLayout(new BoxLayout(panelForm, BoxLayout.Y_AXIS));
        panelForm.setBackground(Color.WHITE);
        panelForm.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel lblTitulo = new JLabel("Crear Nuevo Evento");
        lblTitulo.setFont(Theme.FONT_TITLE);
        lblTitulo.setForeground(Theme.TEXT_DARK);

        JLabel lblSub = new JLabel("Llena los campos para publicar el evento y generar sus boletos.");
        lblSub.setFont(Theme.FONT_SMALL);
        lblSub.setForeground(Theme.TEXT_MUTED);

        txtNombre = crearCampoTexto();
        cbCategoria = new JComboBox<>(new String[]{"Conciertos", "Festivales", "Deportes", "Teatro", "Especiales"});
        cbCategoria.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        cbClasificacion = new JComboBox<>(new String[]{"Todo Público", "+13", "+15", "+18"});
        cbClasificacion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        txtLugar = crearCampoTexto();
        txtFecha = crearCampoTexto();
        txtFecha.setToolTipText("Ejemplo: 2026-11-20 20:00");
        txtFecha.setText("2026-12-01 20:00");

        txtPrecioBase = crearCampoTexto();
        txtPrecioBase.setText("500.00");

        spBoletos = new JSpinner(new SpinnerNumberModel(50, 1, 10000, 10));
        spBoletos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        panelForm.add(lblTitulo);
        panelForm.add(Box.createRigidArea(new Dimension(0, 4)));
        panelForm.add(lblSub);
        panelForm.add(Box.createRigidArea(new Dimension(0, 15)));

        agregarCampoForm(panelForm, "Nombre del Evento", txtNombre);
        agregarCampoForm(panelForm, "Categoría", cbCategoria);
        agregarCampoForm(panelForm, "Clasificación", cbClasificacion);
        agregarCampoForm(panelForm, "Lugar / Recinto", txtLugar);
        agregarCampoForm(panelForm, "Fecha y Hora (AAAA-MM-DD HH:MM)", txtFecha);
        agregarCampoForm(panelForm, "Precio por Boleto ($ MXN)", txtPrecioBase);
        agregarCampoForm(panelForm, "Cantidad de Boletos a Generar", spBoletos);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        panelBotones.setBackground(Theme.CONTENT_BG);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(Theme.FONT_BODY);
        btnCancelar.addActionListener(e -> dispose());

        JButton btnGuardar = new JButton("Guardar y Publicar");
        btnGuardar.setFont(Theme.FONT_BOLD);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setBackground(Theme.PRIMARY_BTN);
        btnGuardar.setFocusPainted(false);
        btnGuardar.addActionListener(e -> guardarEvento());

        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);

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
        parent.add(Box.createRigidArea(new Dimension(0, 8)));
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
            evento.setIdPromotora(this.idPromotora); // 👈 SOLUCIÓN AL ERROR: Asigna el ID de la promotora
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

            // Invocar al servicio para guardar evento y generar sus boletos
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