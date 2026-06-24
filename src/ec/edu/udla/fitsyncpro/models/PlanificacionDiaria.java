package ec.edu.udla.fitsyncpro.models;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Módulo 2 – Clase intermedia PlanificacionDiaria.
 *
 * Vincula a UN socio con sus rutinas de entrenamiento por día de la semana:
 * es el "despacho" del plan, de modo que cada rutina diseñada por el
 * entrenador deja de ser genérica y pasa a pertenecer al plan de un socio.
 *
 * Estructura: HashMap<String dia, Rutina> → consulta del plan del día en O(1).
 */
public class PlanificacionDiaria{

    private String idSocio;
    private HashMap<String, Rutina> rutinasPorDia;

    public PlanificacionDiaria(String idSocio) {
        this.idSocio       = idSocio;
        this.rutinasPorDia = new HashMap<>();
    }

    /** Vincula (o reemplaza) la rutina de un día del plan del socio */
    public void asignarRutina(String dia, Rutina rutina) {
        rutinasPorDia.put(dia, rutina);
    }

    /** Rutina vinculada a un día (null si no hay) — O(1) */
    public Rutina obtenerRutina(String dia) {
        return rutinasPorDia.get(dia);
    }

    /** Quita la vinculación de un día (la Rutina sigue existiendo en el historial) */
    public boolean removerRutina(String dia) {
        return rutinasPorDia.remove(dia) != null;
    }

    /** Días que tienen rutina vinculada */
    public ArrayList<String> diasAsignados() {
        return new ArrayList<>(rutinasPorDia.keySet());
    }

    public int totalDiasAsignados() {
        return rutinasPorDia.size();
    }

    public String getIdSocio() {
        return idSocio;
    }

    @Override
    public String toString() {
        return "Plan de " + idSocio + " (" + rutinasPorDia.size() + " días asignados)";
    }
}
