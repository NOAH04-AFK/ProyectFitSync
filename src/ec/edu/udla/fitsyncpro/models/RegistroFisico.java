package ec.edu.udla.fitsyncpro.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RegistroFisico {
    private String    idRegistro;
    private String    idSocio;
    private LocalDate fechaEvaluacion;

    // ── Datos Antropométricos ─────────────────────────────────────────────────
    private double peso;               // kg
    private double estatura;           // metros
    private double porcentajeGrasa;    // %
    private double circunferenciaCintura;  // cm  (nuevo: seguimiento corporal)
    private double circunferenciaCadera;   // cm

    // ── Ficha de Salud ────────────────────────────────────────────────────────
    private String observaciones;
    private String lesiones;           // restricciones médicas activas
    private boolean activo;            // eliminación lógica

    // ── IMC (calculado automáticamente al crear) ──────────────────────────────
    private double imc;

    // ─────────────────────────────────────────────────────────────────────────
    public RegistroFisico(String idRegistro, String idSocio,
                          double peso, double estatura,
                          double porcentajeGrasa,
                          double circunferenciaCintura, double circunferenciaCadera,
                          String observaciones, String lesiones) {
        this.idRegistro             = idRegistro;
        this.idSocio                = idSocio;
        this.fechaEvaluacion        = LocalDate.now();
        this.peso                   = peso;
        this.estatura               = estatura;
        this.porcentajeGrasa        = porcentajeGrasa;
        this.circunferenciaCintura  = circunferenciaCintura;
        this.circunferenciaCadera   = circunferenciaCadera;
        this.observaciones          = observaciones;
        this.lesiones               = lesiones;
        this.activo                 = true;
        this.imc                    = calcularIMC();   // cálculo automático (Read subfunción)
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Método funcional: +calcularIMC()
    // ════════════════════════════════════════════════════════════════════════
    public double calcularIMC() {
        if (estatura <= 0) return 0;
        return Math.round((peso / (estatura * estatura)) * 100.0) / 100.0;
    }

    /** Clasificación textual del IMC según OMS */
    public String clasificacionIMC() {
        if (imc < 18.5) return "Bajo peso";
        if (imc < 25.0) return "Normal";
        if (imc < 30.0) return "Sobrepeso";
        return "Obesidad";
    }

    // ── Getters ───────────────────────────────────────────────────────────────


    public String getIdRegistro() {
        return idRegistro;
    }

    public String getIdSocio() {
        return idSocio;
    }

    public LocalDate getFechaEvaluacion() {
        return fechaEvaluacion;
    }

    public double getPeso() {
        return peso;
    }

    public double getEstatura() {
        return estatura;
    }

    public double getPorcentajeGrasa() {
        return porcentajeGrasa;
    }

    public double getCircunferenciaCintura() {
        return circunferenciaCintura;
    }

    public double getCircunferenciaCadera() {
        return circunferenciaCadera;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public String getLesiones() {
        return lesiones;
    }

    public boolean isActivo() {
        return activo;
    }

    public double getImc() {
        return imc;
    }

    // ── Setters (Update) ──────────────────────────────────────────────────────


    public void setIdRegistro(String idRegistro) {
        this.idRegistro = idRegistro;
    }

    public void setIdSocio(String idSocio) {
        this.idSocio = idSocio;
    }

    public void setFechaEvaluacion(LocalDate fechaEvaluacion) {
        this.fechaEvaluacion = fechaEvaluacion;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public void setEstatura(double estatura) {
        this.estatura = estatura;
    }

    public void setPorcentajeGrasa(double porcentajeGrasa) {
        this.porcentajeGrasa = porcentajeGrasa;
    }

    public void setCircunferenciaCintura(double circunferenciaCintura) {
        this.circunferenciaCintura = circunferenciaCintura;
    }

    public void setCircunferenciaCadera(double circunferenciaCadera) {
        this.circunferenciaCadera = circunferenciaCadera;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public void setLesiones(String lesiones) {
        this.lesiones = lesiones;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public void setImc(double imc) {
        this.imc = imc;
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return fechaEvaluacion.format(fmt)
                + " | Peso: " + peso + "kg"
                + " | IMC: " + imc + " (" + clasificacionIMC() + ")"
                + (activo ? "" : " [INACTIVO]");
    }
}
