package ec.edu.udla.fitsyncpro.models;

import ec.edu.udla.fitsyncpro.utils.GrupoMuscular;

public class Ejercicio implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private String idEjercicio;
    private String nombreEjercicio;
    private GrupoMuscular grupoMuscular;
    private int serie;
    private int repeticiones;

    public Ejercicio(String idEjercicio, String nombreEjercicio, GrupoMuscular grupoMuscular, int serie, int repeticiones) {
        this.idEjercicio = idEjercicio;
        this.nombreEjercicio = nombreEjercicio;
        this.grupoMuscular = grupoMuscular;
        this.serie = serie;
        this.repeticiones = repeticiones;
    }

    /**
     * Copia independiente de un ejercicio. Se usa al personalizar el plan de un
     * socio, de modo que cambiar sus series o repeticiones no afecte al catálogo
     * general ni a los planes de otros socios.
     */
    public Ejercicio(Ejercicio original) {
        this(original.idEjercicio, original.nombreEjercicio, original.grupoMuscular,
                original.serie, original.repeticiones);
    }

    public String getIdEjercicio() { return idEjercicio; }

    public String getNombreEjercicio() { return nombreEjercicio; }
    public void setNombreEjercicio(String nombreEjercicio) { this.nombreEjercicio = nombreEjercicio; }

    public GrupoMuscular getGrupoMuscular() { return grupoMuscular; }
    public void setGrupoMuscular(GrupoMuscular grupoMuscular) { this.grupoMuscular = grupoMuscular; }

    public int getSerie() { return serie; }
    public void setSerie(int serie) { this.serie = serie; }

    public int getRepeticiones() { return repeticiones; }
    public void setRepeticiones(int repeticiones) { this.repeticiones = repeticiones; }

    @Override
    public String toString() {
        return "[" + grupoMuscular.name() + "] " + nombreEjercicio + " - " + serie + "x" + repeticiones;
    }
}
