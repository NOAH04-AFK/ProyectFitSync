package ec.edu.udla.fitsyncpro.models;

import ec.edu.udla.fitsyncpro.utils.NivelEntrenamiento;

import java.util.LinkedList;

public class Rutina {
    private String idRutina;
    private String objetivo;
    private NivelEntrenamiento nivel;
    private String dia;
    private LinkedList<Ejercicio> ejercicios;
    private boolean activa;

    public Rutina(String idRutina, String objetivo, NivelEntrenamiento nivel, String dia) {
        this.idRutina = idRutina;
        this.objetivo = objetivo;
        this.nivel = nivel;
        this.dia = dia;
        this.ejercicios = new LinkedList<>();
        this.activa = true;
    }

    // Agrega un ejercicio a la LinkedList de la rutina
    public void agregarEjercicio(Ejercicio ej) {
        ejercicios.add(ej);
    }

    public String getIdRutina() { return idRutina; }

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
