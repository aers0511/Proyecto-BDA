package com.tutiket.util;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.tutiket.domain.Boleto;

import java.io.ByteArrayOutputStream;

public class PdfBoletoGenerator {

    public static byte[] generarPdfBoleto(Boleto boleto, String nombreEvento, String nombreCliente) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Formato básico de comprobante
        document.add(new Paragraph("=== TUTIKET - COMPROBANTE DE ENTRADA ===").setBold().setFontSize(16));
        document.add(new Paragraph("Evento: " + (nombreEvento != null ? nombreEvento : "N/A")));
        document.add(new Paragraph("Cliente: " + (nombreCliente != null ? nombreCliente : "N/A")));
        document.add(new Paragraph("Folio: " + (boleto.getFolio() != null ? boleto.getFolio() : "S/F")));
        document.add(new Paragraph("Zona: " + (boleto.getZona() != null ? boleto.getZona() : "General")));
        document.add(new Paragraph("Asiento: " + (boleto.getAsiento() != null ? boleto.getAsiento() : "General / Libre")));
        document.add(new Paragraph("Precio Pagado: $" + (boleto.getPrecio() != null ? boleto.getPrecio() : "0.00") + " MXN"));

        document.close();
        return baos.toByteArray();
    }
}