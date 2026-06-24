package ec.edu.udla.fitsyncpro.models;

import ec.edu.udla.fitsyncpro.utils.TipoActividad;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Módulo 4 – Entrada de la bitácora de performance.
 *
 * Cada registro representa una asistencia diaria o un levantamiento de carga
 * (sobrecarga progresiva). La bitácora completa se almacena en un
 * ArrayList<RegistroPerformance> histórico dentro del GestorPerformance.
 */
public class RegistroPerformance {


    private String        idRegistro;
    private String        idSocio;
    private LocalDate     fecha;
    private TipoActividad tipo;
    private String        descripcion;     // ejercicio (si es CARGA) o detalle
    private double        cargaKg;         // 0 si es ASISTENCIA
    private int           puntosOtorgados;
    private boolean       activo;          // eliminación lógica

    public RegistroPerformance(String idRegistro, String idSocio, LocalDate fecha,
                               TipoActividad tipo, String descripcion,
                               double cargaKg, int puntosOtorgados) {
        this.idRegistro      = idRegistro;
        this.idSocio         = idSocio;
        this.fecha           = fecha;
        this.tipo            = tipo;
        this.descripcion     = descripcion;
        this.cargaKg         = cargaKg;
        this.puntosOtorgados = puntosOtorgados;
        this.activo          = true;
    }

    public String        getIdRegistro()      { return idRegistro; }
    public String        getIdSocio()         { return idSocio; }
    public LocalDate     getFecha()           { return fecha; }
    public TipoActividad getTipo()            { return tipo; }
    public String        getDescripcion()     { return descripcion; }
    public double        getCargaKg()         { return cargaKg; }
    public int           getPuntosOtorgados() { return puntosOtorgados; }
    public boolean       isActivo()           { return activo; }
    public void          setActivo(boolean a) { this.activo = a; }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String base = fecha.format(fmt) + " | " + tipo.name() + " | " + descripcion;
        if (tipo == TipoActividad.CARGA) base += " (" + cargaKg + " kg)";
        return base + " → +" + puntosOtorgados + " pts" + (activo ? "" : " [INACTIVO]");
    }
}
