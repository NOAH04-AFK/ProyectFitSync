package ec.edu.udla.fitsyncpro.models;

/**
 * Módulo 5 – Plantilla de reporte programado.
 * Admite eliminación lógica (inactivación) para conservar configuraciones
 * históricas sin que aparezcan entre las plantillas vigentes.
 */
public class PlantillaReporte  {


    private String  idPlantilla;
    private String  nombre;
    private String  descripcion;
    private String  frecuencia;   // Diaria | Semanal | Mensual
    private boolean activa;

    public PlantillaReporte(String idPlantilla, String nombre, String descripcion, String frecuencia) {
        this.idPlantilla = idPlantilla;
        this.nombre      = nombre;
        this.descripcion = descripcion;
        this.frecuencia  = frecuencia;
        this.activa      = true;
    }

    public String  getIdPlantilla()         { return idPlantilla; }
    public String  getNombre()              { return nombre; }
    public String  getDescripcion()         { return descripcion; }
    public String  getFrecuencia()          { return frecuencia; }
    public void    setFrecuencia(String f)  { this.frecuencia = f; }
    public boolean isActiva()               { return activa; }
    public void    setActiva(boolean a)     { this.activa = a; }

    @Override
    public String toString() {
        return nombre + " (" + frecuencia + ")" + (activa ? "" : " [INACTIVA]");
    }
}
