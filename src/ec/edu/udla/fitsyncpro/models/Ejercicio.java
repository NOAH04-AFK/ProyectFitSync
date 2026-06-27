package ec.edu.udla.fitsyncpro.models;

import ec.edu.udla.fitsyncpro.utils.GrupoMuscular;

/**
 * EJERCICIO: un movimiento del catálogo (nombre, grupo muscular, series y reps).
 * Los ejercicios viven en el catálogo general y también dentro de cada Rutina.
 * Por eso existe un constructor de COPIA: al meter un ejercicio en el plan de un
 * socio se guarda una copia, así editar sus series/reps no toca el catálogo.
 */
public class Ejercicio {
    private String        idEjercicio;
    private String        nombreEjercicio;
    private GrupoMuscular grupoMuscular;   // PECHO, ESPALDA, PIERNAS... (enum)
    private int           serie;           // número de series
    private int           repeticiones;    // repeticiones por serie

    public Ejercicio(String idEjercicio, String nombreEjercicio, GrupoMuscular grupoMuscular, int serie, int repeticiones) {
        this.idEjercicio = idEjercicio;
        this.nombreEjercicio = nombreEjercicio;
        this.grupoMuscular = grupoMuscular;
        this.serie = serie;
        this.repeticiones = repeticiones;
    }

    /**
     * Constructor de COPIA: crea un ejercicio independiente igual al original.
     * Se usa al personalizar el plan de un socio, para no afectar el catálogo
     * general ni los planes de otros socios.
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
