package ec.edu.udla.fitsyncpro.controllers;

import ec.edu.udla.fitsyncpro.estructuras.GrafoEquipos;
import ec.edu.udla.fitsyncpro.models.*;
import ec.edu.udla.fitsyncpro.utils.EstadoEquipo;
import ec.edu.udla.fitsyncpro.utils.GrupoMuscular;
import ec.edu.udla.fitsyncpro.utils.NivelEntrenamiento;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

public class GestorRutinas {
    private ArrayList<Ejercicio>           catalogoGeneral;   // todos los ejercicios disponibles
    private LinkedList<Ejercicio> rutinaTemporal;    // rutina en construcción (secuencia dinámica)
    private HashMap<String, Rutina>        historialPorDia;   // rutinas guardadas por día
    private HashMap<String, Equipo>        catalogoEquipos;   // máquinas del gimnasio
    private HashMap<String, VideoTutorial> catalogoVideos;    // tutoriales YouTube por ejercicio
    private GrafoEquipos grafoDependencias; // grafo dirigido de dependencias físicas
    private HashMap<String, PlanificacionDiaria> planesPorSocio; // despacho de planes por socio
    // ─────────────────────────────────────────────────────────────────────────

    public GestorRutinas() {
        catalogoGeneral   = new ArrayList<>();
        rutinaTemporal    = new LinkedList<>();
        historialPorDia   = new HashMap<>();
        catalogoEquipos   = new HashMap<>();
        catalogoVideos    = new HashMap<>();
        grafoDependencias = new GrafoEquipos();
        planesPorSocio    = new HashMap<>();
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
        registrarEquipo(new Equipo("EQ001", "Banco de Press",     GrupoMuscular.PECHO));
        registrarEquipo(new Equipo("EQ002", "Máquina de Remo",    GrupoMuscular.ESPALDA));
        registrarEquipo(new Equipo("EQ003", "Prensa Inclinada",   GrupoMuscular.PIERNAS));
        registrarEquipo(new Equipo("EQ004", "Barra Multipower",   GrupoMuscular.PIERNAS));
        registrarEquipo(new Equipo("EQ005", "Torre de Poleas",    GrupoMuscular.ESPALDA));
        registrarEquipo(new Equipo("EQ006", "Polea Alta",         GrupoMuscular.ESPALDA));
        registrarEquipo(new Equipo("EQ007", "Cable Cruzado",      GrupoMuscular.PECHO));

        // Aristas del grafo dirigido: origen → equipos que dependen de él
        grafoDependencias.agregarDependencia("EQ005", "EQ006"); // Polea Alta cuelga de la Torre
        grafoDependencias.agregarDependencia("EQ005", "EQ007"); // Cable Cruzado usa la Torre
        grafoDependencias.agregarDependencia("EQ004", "EQ001"); // Banco de Press se usa con la Multipower

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
    //  MÓDULO: RUTINA TEMPORAL (LinkedList – secuencia en construcción)
    //  Inserciones y eliminaciones al final en O(1), sin redimensionar memoria.
    // ════════════════════════════════════════════════════════════════════════════
    public void agregarEjercicio(Ejercicio ej)        { rutinaTemporal.add(ej); }
    public Ejercicio quitarUltimoEjercicio()          { return rutinaTemporal.isEmpty() ? null : rutinaTemporal.removeLast(); }
    public LinkedList<Ejercicio> getRutinaActual()    { return rutinaTemporal; }
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
    //  CRUD – EQUIPOS  (+ grafo de dependencias físicas)
    // ════════════════════════════════════════════════════════════════════════════

    /** CREATE – registra el equipo y lo agrega como vértice del grafo */
    public void registrarEquipo(Equipo equipo) {
        catalogoEquipos.put(equipo.getIdEquipo(), equipo);
        grafoDependencias.agregarVertice(equipo.getIdEquipo());
    }

    /** CREATE (arista) – declara que 'dependiente' necesita de 'origen' para funcionar */
    public void registrarDependencia(String idOrigen, String idDependiente) {
        grafoDependencias.agregarDependencia(idOrigen, idDependiente);
    }

    /** READ */
    public ArrayList<Equipo> obtenerEquipos() {
        return new ArrayList<>(catalogoEquipos.values());
    }

    /** UPDATE – cambia el estado operativo de una máquina (sin cascada) */
    public boolean actualizarEstadoEquipo(String idEquipo, EstadoEquipo nuevoEstado) {
        Equipo eq = catalogoEquipos.get(idEquipo);
        if (eq != null) { eq.setEstado(nuevoEstado); return true; }
        return false;
    }

    /**
     * UPDATE – Reporta una máquina como DAÑADA y ejecuta BFS sobre el grafo
     * dirigido de dependencias: todos los equipos alcanzables desde la máquina
     * averiada pasan a MANTENIMIENTO (inactivación en cascada, O(V+E)).
     *
     * @return lista de equipos afectados por la cascada (sin incluir el dañado)
     */
    public ArrayList<Equipo> reportarDanio(String idEquipo) {
        ArrayList<Equipo> afectados = new ArrayList<>();
        Equipo origen = catalogoEquipos.get(idEquipo);
        if (origen == null) return afectados;

        origen.setEstado(EstadoEquipo.DAÑADO);

        // Recorrido en anchura desde la máquina averiada
        for (String idAlcanzado : grafoDependencias.bfs(idEquipo)) {
            Equipo dependiente = catalogoEquipos.get(idAlcanzado);
            if (dependiente != null && dependiente.isActivo()
                    && dependiente.getEstado() == EstadoEquipo.OPERATIVO) {
                dependiente.setEstado(EstadoEquipo.MANTENIMIENTO);
                afectados.add(dependiente);
            }
        }
        return afectados;
    }

    /** READ – dependencias directas de un equipo (adyacentes en el grafo) */
    public LinkedList<String> obtenerDependientesDirectos(String idEquipo) {
        return grafoDependencias.obtenerAdyacentes(idEquipo);
    }

    /** Acceso al grafo (para reportes y para la UI) */
    public GrafoEquipos getGrafoDependencias() {
        return grafoDependencias;
    }

    /** DELETE lógico – inactiva el equipo */
    public boolean inactivarEquipo(String idEquipo) {
        Equipo eq = catalogoEquipos.get(idEquipo);
        if (eq != null) { eq.setActivo(false); return true; }
        return false;
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  DESPACHO DE PLANES POR SOCIO (clase intermedia PlanificacionDiaria)
    //  El entrenador vincula la rutina del día al plan personal de cada socio.
    // ════════════════════════════════════════════════════════════════════════════

    /**
     * UPDATE – vincula la rutina ACTIVA de un día al plan del socio.
     * Se guarda una COPIA PROFUNDA personalizada (no la plantilla compartida),
     * de modo que cada socio tiene su propia rutina editable de forma
     * independiente: personalizar la de uno no afecta a los demás.
     */
    public boolean asignarRutinaASocio(String idSocio, String dia) {
        Rutina plantilla = historialPorDia.get(dia);
        if (plantilla == null || !plantilla.isActiva()) return false;   // no hay rutina activa ese día
        PlanificacionDiaria plan = planesPorSocio.computeIfAbsent(idSocio, PlanificacionDiaria::new);
        plan.asignarRutina(dia, new Rutina(plantilla, idSocio));        // copia propia del socio
        return true;
    }

    /** Vincula automáticamente TODAS las rutinas activas de la semana al socio */
    public int asignarSemanaCompleta(String idSocio) {
        int asignadas = 0;
        for (String dia : historialPorDia.keySet()) {
            if (asignarRutinaASocio(idSocio, dia)) asignadas++;
        }
        return asignadas;
    }

    /** READ – rutina vinculada al socio para un día (null si no tiene o está inactiva) */
    public Rutina obtenerRutinaDeSocio(String idSocio, String dia) {
        PlanificacionDiaria plan = planesPorSocio.get(idSocio);
        if (plan == null) return null;
        Rutina r = plan.obtenerRutina(dia);
        return (r != null && r.isActiva()) ? r : null;
    }

    /** READ – plan semanal completo del socio (null si nunca se le asignó nada) */
    public PlanificacionDiaria obtenerPlanDeSocio(String idSocio) {
        return planesPorSocio.get(idSocio);
    }

    /** DELETE – quita la vinculación de un día del plan del socio */
    public boolean desasignarRutinaDeSocio(String idSocio, String dia) {
        PlanificacionDiaria plan = planesPorSocio.get(idSocio);
        return (plan != null) && plan.removerRutina(dia);
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  PERSONALIZACIÓN POR SOCIO
    //  Editan SOLO la copia propia del socio (la plantilla del día no se toca).
    // ════════════════════════════════════════════════════════════════════════════

    /**
     * Agrega un ejercicio únicamente al plan de ESE socio para el día indicado.
     * Inserta una copia del ejercicio del catálogo, así la edición posterior de
     * series/repeticiones no afecta al catálogo ni a otros socios.
     *
     * @return false si el socio no tiene una rutina asignada ese día.
     */
    public boolean agregarEjercicioARutinaDeSocio(String idSocio, String dia, Ejercicio ejercicio) {
        if (ejercicio == null) return false;
        PlanificacionDiaria plan = planesPorSocio.get(idSocio);
        if (plan == null) return false;
        Rutina r = plan.obtenerRutina(dia);
        if (r == null) return false;
        r.agregarEjercicio(new Ejercicio(ejercicio));   // copia independiente
        return true;
    }

    /**
     * Quita el ejercicio en la posición 'indice' del plan de ESE socio para el
     * día indicado, sin afectar la plantilla ni a otros socios.
     *
     * @return false si no hay rutina ese día o el índice es inválido.
     */
    public boolean quitarEjercicioDeRutinaDeSocio(String idSocio, String dia, int indice) {
        PlanificacionDiaria plan = planesPorSocio.get(idSocio);
        if (plan == null) return false;
        Rutina r = plan.obtenerRutina(dia);
        if (r == null) return false;
        return r.removerEjercicio(indice) != null;
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  VIDEO TUTORIALES
    // ════════════════════════════════════════════════════════════════════════════
    public VideoTutorial obtenerVideoPorEjercicio(String idEjercicio) {
        return catalogoVideos.get(idEjercicio);
    }

    /** Un grupo muscular queda inhabilitado si alguna de sus máquinas activas está DAÑADA o en MANTENIMIENTO */
    public boolean isGrupoDisponible(GrupoMuscular grupo) {
        for (Equipo eq : catalogoEquipos.values()) {
            if (eq.getGrupoMuscular() == grupo && eq.isActivo()
                    && eq.getEstado() != EstadoEquipo.OPERATIVO) {
                return false;
            }
        }
        return true;
    }
}
