package ec.edu.udla.fitsyncpro.models;

/**
 * Módulo 4 – Recompensa configurada de la mecánica de gamificación.
 * Se asigna automáticamente al socio cuando su puntaje de constancia
 * alcanza el umbral requerido (hito de desempeño).
 */
public class Recompensa {


    private String  idRecompensa;
    private String  descripcion;
    private int     puntosRequeridos;
    private boolean activa;

    public Recompensa(String idRecompensa, String descripcion, int puntosRequeridos) {
        this.idRecompensa     = idRecompensa;
        this.descripcion      = descripcion;
        this.puntosRequeridos = puntosRequeridos;
        this.activa           = true;
    }

    public String  getIdRecompensa()     { return idRecompensa; }
    public String  getDescripcion()      { return descripcion; }
    public void    setDescripcion(String d) { this.descripcion = d; }
    public int     getPuntosRequeridos() { return puntosRequeridos; }
    public void    setPuntosRequeridos(int p) { this.puntosRequeridos = p; }
    public boolean isActiva()            { return activa; }
    public void    setActiva(boolean a)  { this.activa = a; }

    @Override
    public String toString() {
        return descripcion + " (" + puntosRequeridos + " pts)";
    }
}
