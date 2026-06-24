package ec.edu.udla.fitsyncpro.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Módulo 5 – Reporte analítico consolidado (Business Intelligence).
 * Instancia inmutable generada por el GestorReportes; el historial de
 * reportes se conserva en un ArrayList para su trazabilidad.
 */
public class ReporteOperativo  {

    private String        idReporte;
    private String        titulo;
    private LocalDateTime fechaGeneracion;
    private String        contenido;

    public ReporteOperativo(String idReporte, String titulo, String contenido) {
        this.idReporte       = idReporte;
        this.titulo          = titulo;
        this.contenido       = contenido;
        this.fechaGeneracion = LocalDateTime.now();
    }

    public String        getIdReporte()       { return idReporte; }
    public String        getTitulo()          { return titulo; }
    public LocalDateTime getFechaGeneracion() { return fechaGeneracion; }
    public String        getContenido()       { return contenido; }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return "[" + idReporte + "] " + titulo + " — " + fechaGeneracion.format(fmt);
    }
}
