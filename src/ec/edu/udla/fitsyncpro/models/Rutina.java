package ec.edu.udla.fitsyncpro.models;

import ec.edu.udla.fitsyncpro.utils.NivelEntrenamiento;

import java.util.LinkedList;

/**
 * RUTINA: la sesión de entrenamiento de un día (objetivo, nivel y sus ejercicios).
 * Tiene DOS usos según el atributo idSocio:
 *   - idSocio == null  -> plantilla genérica del día (la que arma el entrenador)
 *   - idSocio != null  -> copia PERSONALIZADA de un socio (su plan propio)
 *
 * Estructura: ejercicios es una LinkedList<Ejercicio>. El segundo constructor
 * hace COPIA PROFUNDA para que el plan de cada socio sea independiente.
 */
public class Rutina {
    private String idRutina;
    private String objetivo;
    private NivelEntrenamiento nivel;
    private String dia;
    private LinkedList<Ejercicio> ejercicios;
    private boolean activa;
    private String idSocio;   // null = plantilla genérica; con valor = copia personalizada de un socio

    public Rutina(String idRutina, String objetivo, NivelEntrenamiento nivel, String dia) {
        this.idRutina = idRutina;
        this.objetivo = objetivo;
        this.nivel = nivel;
        this.dia = dia;
        this.ejercicios = new LinkedList<>();
        this.activa = true;
        this.idSocio = null;
    }

    /**
     * Copia PROFUNDA de una rutina plantilla, ya personalizada para un socio.
     * Clona la LinkedList y cada Ejercicio, de modo que editar el plan de un
     * socio NO altera la plantilla del día ni el plan de ningún otro socio.
     */
    public Rutina(Rutina original, String idSocio) {
        this.idRutina = original.idRutina + "-" + idSocio;
        this.objetivo = original.objetivo;
        this.nivel    = original.nivel;
        this.dia      = original.dia;
        this.activa   = original.activa;
        this.idSocio  = idSocio;
        this.ejercicios = new LinkedList<>();
        for (Ejercicio ej : original.ejercicios) {
            this.ejercicios.add(new Ejercicio(ej));   // copia independiente de cada ejercicio
        }
    }

    // Agrega un ejercicio a la LinkedList de la rutina
    public void agregarEjercicio(Ejercicio ej) {
        ejercicios.add(ej);
    }

    /** Quita el ejercicio en la posición indicada (personalización del plan del socio). */
    public Ejercicio removerEjercicio(int indice) {
        if (indice < 0 || indice >= ejercicios.size()) return null;
        return ejercicios.remove(indice);
    }

    public String getIdRutina() { return idRutina; }

    public String getIdSocio() { return idSocio; }
    public void setIdSocio(String idSocio) { this.idSocio = idSocio; }

    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }

    public NivelEntrenamiento getNivel() { return nivel; }
    public void setNivel(NivelEntrenamiento nivel) { this.nivel = nivel; }

    public String getDia() { return dia; }

    public LinkedList<Ejercicio> getEjercicios() { return ejercicios; }

    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }

    @Override
    public String toString() {
        return "[" + dia + "] " + objetivo + " | Nivel: " + nivel.name() + (activa ? "" : " (INACTIVA)");
    }
}
