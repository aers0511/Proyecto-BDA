package com.tutiket.service;

import com.tutiket.domain.Boleto;
import com.tutiket.util.PdfBoletoGenerator;

import java.io.File;
import java.io.FileOutputStream;

public class BoletoPdfService {

    /**
     * Genera el arreglo de bytes del PDF del boleto.
     */
    public byte[] obtenerBytesPdf(Boleto boleto, String nombreEvento, String nombreCliente) throws Exception {
        if (boleto == null) {
            throw new IllegalArgumentException("El boleto no puede ser nulo para generar el PDF.");
        }
        return PdfBoletoGenerator.generarPdfBoleto(
                boleto, 
                nombreEvento != null ? nombreEvento : "Evento Desconocido", 
                nombreCliente != null ? nombreCliente : "Cliente General"
        );
    }

    /**
     * Guarda el archivo PDF generado en una ruta/archivo específico seleccionado por el usuario.
     */
    public void guardarPdfEnArchivo(Boleto boleto, String nombreEvento, String nombreCliente, File archivoDestino) throws Exception {
        byte[] pdfBytes = obtenerBytesPdf(boleto, nombreEvento, nombreCliente);
        
        try (FileOutputStream fos = new FileOutputStream(archivoDestino)) {
            fos.write(pdfBytes);
            fos.flush();
        }
    }
}