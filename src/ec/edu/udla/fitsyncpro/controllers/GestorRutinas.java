package ec.edu.udla.fitsyncpro.controllers;

import ec.edu.udla.fitsyncpro.models.Ejercicio;
import ec.edu.udla.fitsyncpro.models.Equipo;
import ec.edu.udla.fitsyncpro.models.Rutina;
import ec.edu.udla.fitsyncpro.models.VideoTutorial;
import ec.edu.udla.fitsyncpro.utils.EstadoEquipo;
import ec.edu.udla.fitsyncpro.utils.GrupoMuscular;
import ec.edu.udla.fitsyncpro.utils.NivelEntrenamiento;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class GestorRutinas {
    private ArrayList<Ejercicio>          catalogoGeneral;   // todos los ejercicios disponibles
    private Stack<Ejercicio>              rutinaTemporal;    // construcción con undo (LIFO)
    private HashMap<String, Rutina>       historialPorDia;   // rutinas guardadas por día
    private HashMap<String, Equipo>       catalogoEquipos;   // máquinas del gimnasio
    private HashMap<String, VideoTutorial> catalogoVideos;   // tutoriales YouTube por ejercicio
    // ─────────────────────────────────────────────────────────────────────────

    public GestorRutinas() {
        catalogoGeneral  = new ArrayList<>();
        rutinaTemporal   = new Stack<>();
        historialPorDia  = new HashMap<>();
        catalogoEquipos  = new HashMap<>();
        catalogoVideos   = new HashMap<>();
        cargarDatosPrueba();
    }

    // ── Datos de prueba ───────────────────────────────────────────────────────
    private void cargarDatosPrueba() {
        // Ejercicios
        catalogoGeneral.add(new Ejercicio("E001", "Press de Banca",           GrupoMuscular.PECHO,   4, 12));
        catalogoGeneral.add(new Ejercicio("E002", "Aperturas con Mancuernas", GrupoMuscular.PECHO,   3, 15));
        catalogoGeneral.add(new Ejercicio("E003", "Dominadas",                GrupoMuscular.ESPALDA, 4, 10));
        catalogoGeneral.add(new Ejercicio("E004", "Remo en Máquina",          GrupoMuscular.ESPALDA, 3, 12));
        catalogoGeneral.add(new Ejercicio("E005", "Sentadilla Libre",         GrupoMuscular.PIERNAS, 4, 10));
        catalogoGeneral.add(new Ejercicio("E006", "Prensa Inclinada",         GrupoMuscular.PIERNAS, 4, 15));
        catalogoGeneral.add(new Ejercicio("E007", "Curl de Bíceps",           GrupoMuscular.BRAZOS,  3, 12));

        // Equipos
        catalogoEquipos.put("EQ001", new Equipo("EQ001", "Banco de Press",     GrupoMuscular.PECHO));
        catalogoEquipos.put("EQ002", new Equipo("EQ002", "Máquina de Remo",    GrupoMuscular.ESPALDA));
        catalogoEquipos.put("EQ003", new Equipo("EQ003", "Prensa Inclinada",   GrupoMuscular.PIERNAS));
        catalogoEquipos.put("EQ004", new Equipo("EQ004", "Barra Multipower",   GrupoMuscular.PIERNAS));

        // Videos tutoriales (Read → despacho autónomo)
        catalogoVideos.put("E001", new VideoTutorial("V001", "E001", "Press de Banca – Técnica correcta",   "https://youtube.com/watch?v=rT7DgGymasU"));
        catalogoVideos.put("E002", new VideoTutorial("V002", "E002", "Aperturas con Mancuernas – Tutorial", "https://youtube.com/watch?v=eozdVDA78K0"));
        catalogoVideos.put("E003", new VideoTutorial("V003", "E003", "Dominadas – Guía completa",           "https://youtube.com/watch?v=eGo4IYlbE5g"));
        catalogoVideos.put("E004", new VideoTutorial("V004", "E004", "Remo en Máquina – Tutorial",          "https://youtube.com/watch?v=GZbfZ033f74"));
        catalogoVideos.put("E005", new VideoTutorial("V005", "E005", "Sentadilla Libre – Forma perfecta",   "https://youtube.com/watch?v=ultWZbUMPL8"));
        catalogoVideos.put("E006", new VideoTutorial("V006", "E006", "Prensa Inclinada – Tutorial",         "https://youtube.com/watch?v=IZxyjW7MPJQ"));
        catalogoVideos.put("E007", new VideoTutorial("V007", "E007", "Curl de Bíceps – Técnica",            "https://youtube.com/watch?v=ykJmrZ5v0Oo"));
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  MÓDULO: EJERCICIOS  (catálogo)
    // ════════════════════════════════════════════════════════════════════════════
    public ArrayList<Ejercicio> obtenerCatalogo(GrupoMuscular filtro) {
        if (filtro == null) return catalogoGeneral;
        ArrayList<Ejercicio> filtrados = new ArrayList<>();
        for (Ejercicio ej : catalogoGeneral) {
            if (ej.getGrupoMuscular() == filtro) filtrados.add(ej);
        }
        return filtrados;
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  MÓDULO: RUTINA TEMPORAL (Stack – undo/redo)
    // ════════════════════════════════════════════════════════════════════════════
    public void agregarEjercicio(Ejercicio ej)       { rutinaTemporal.push(ej); }
    public Ejercicio deshacerUltimoEjercicio()        { return rutinaTemporal.isEmpty() ? null : rutinaTemporal.pop(); }
    public Stack<Ejercicio> getRutinaActual()         { return rutinaTemporal; }
    public void limpiarRutina()                       { rutinaTemporal.clear(); }

    // ════════════════════════════════════════════════════════════════════════════
    //  CRUD – RUTINAS
    // ════════════════════════════════════════════════════════════════════════════

    /** CREATE – guarda la rutina temporal en el HashMap por día usando una LinkedList interna */
    public void guardarRutinaEnDia(String dia, String objetivo, NivelEntrenamiento nivel) {
        String id = "R-" + dia + "-" + System.currentTimeMillis();
        Rutina nueva = new Rutina(id, objetivo, nivel, dia);
        for (Ejercicio ej : rutinaTemporal) {
            nueva.agregarEjercicio(ej);   // llena la LinkedList de Rutina
        }
        historialPorDia.put(dia, nueva);
        rutinaTemporal.clear();
    }

    /** READ – obtiene el objeto Rutina completo de un día */
    public Rutina obtenerRutinaPorDia(String dia) {
        return historialPorDia.get(dia);
    }

    /** READ – obtiene solo los ejercicios activos de un día (para la UI) */
    public ArrayList<Ejercicio> obtenerEjerciciosPorDia(String dia) {
        Rutina r = historialPorDia.get(dia);
        if (r != null && r.isActiva()) return new ArrayList<>(r.getEjercicios());
        return null;
    }

    /** UPDATE – modifica objetivo y nivel de una rutina ya guardada */
    public boolean actualizarRutina(String dia, String nuevoObjetivo, NivelEntrenamiento nuevoNivel) {
        Rutina r = historialPorDia.get(dia);
        if (r != null) { r.setObjetivo(nuevoObjetivo); r.setNivel(nuevoNivel); return true; }
        return false;
    }

    /** DELETE lógico – inactiva la rutina de un día sin borrarla del HashMap */
    public boolean inactivarRutina(String dia) {
        Rutina r = historialPorDia.get(dia);
        if (r != null && r.isActiva()) { r.setActiva(false); return true; }
        return false;
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  CRUD – EQUIPOS
    // ════════════════════════════════════════════════════════════════════════════

    /** CREATE */
    public void registrarEquipo(Equipo equipo) {
        catalogoEquipos.put(equipo.getIdEquipo(), equipo);
    }

    /** READ */
    public ArrayList<Equipo> obtenerEquipos() {
        return new ArrayList<>(catalogoEquipos.values());
    }

    /** UPDATE – cambia el estado operativo de una máquina */
    public boolean actualizarEstadoEquipo(String idEquipo, EstadoEquipo nuevoEstado) {
        Equipo eq = catalogoEquipos.get(idEquipo);
        if (eq != null) { eq.setEstado(nuevoEstado); return true; }
        return false;
    }

    /** DELETE lógico – inactiva el equipo */
    public boolean inactivarEquipo(String idEquipo) {
        Equipo eq = catalogoEquipos.get(idEquipo);
        if (eq != null) { eq.setActivo(false); return true; }
        return false;
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  VIDEO TUTORIALES
    // ════════════════════════════════════════════════════════════════════════════
    public VideoTutorial obtenerVideoPorEjercicio(String idEjercicio) {
        return catalogoVideos.get(idEjercicio);
    }

    /** Verifica si algún equipo del grupo muscular está DAÑADO (inhabilita ejercicios) */
    public boolean isGrupoDisponible(GrupoMuscular grupo) {
        for (Equipo eq : catalogoEquipos.values()) {
            if (eq.getGrupoMuscular() == grupo && eq.isActivo()
                    && eq.getEstado() == EstadoEquipo.DAÑADO) {
                return false;
            }
        }
        return true;
    }
}
