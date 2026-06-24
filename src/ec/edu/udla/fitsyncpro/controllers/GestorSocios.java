package ec.edu.udla.fitsyncpro.controllers;

import ec.edu.udla.fitsyncpro.models.Entrenador;
import ec.edu.udla.fitsyncpro.models.Socio;
import ec.edu.udla.fitsyncpro.utils.TipoMembresia;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Módulo 1 – Gestión de Socios y Entrenadores
 *
 * Estructuras de datos:
 * ─ HashMap<String, Socio>      → directorio de socios, búsqueda O(1) por ID
 * ─ HashMap<String, Entrenador> → directorio de entrenadores, búsqueda O(1) por ID
 * ─ LinkedList<Socio> (dentro de Entrenador) → alumnos a cargo, inserciones O(1)
 *
 * Controla la asignación interna socio→entrenador para equilibrar la carga
 * operativa (relación crítica 16:1 de las horas pico).
 */
public class GestorSocios {

    private HashMap<String, Socio>      directorioSocios;
    private HashMap<String, Entrenador> directorioEntrenadores;

    /** Uso independiente: crea su propio directorio con datos de prueba */
    public GestorSocios() {
        this(new HashMap<>());
    }

    /**
     * Uso integrado: recibe el MISMO HashMap de socios que usa el Módulo 3
     * (GestorEvolucionFisica), de modo que ambos módulos trabajen sobre los
     * mismos objetos Socio.
     */
    public GestorSocios(HashMap<String, Socio> directorioCompartido) {
        this.directorioSocios       = directorioCompartido;
        this.directorioEntrenadores = new HashMap<>();
        cargarDatosPrueba();
    }

    // ── Datos de prueba ───────────────────────────────────────────────────────
    private void cargarDatosPrueba() {
        // Socios base (si el directorio compartido ya los tiene, no se duplican)
        registrarSocio(new Socio("S001", "Carlos Andrade",  28, "0991234567", TipoMembresia.MENSUAL));
        registrarSocio(new Socio("S002", "María Gómez",     35, "0987654321", TipoMembresia.ANUAL));
        registrarSocio(new Socio("S003", "Andrés Villacís", 22, "0998877665", TipoMembresia.MENSUAL));
        registrarSocio(new Socio("S004", "Paola Tipán",     31, "0993322110", TipoMembresia.ANUAL));
        registrarSocio(new Socio("S005", "Roberto Cruz",    45, "0987112233", TipoMembresia.MENSUAL));

        // Entrenadores
        Entrenador t1 = new Entrenador("T001", "Pedro Salazar", 34, "0984455667", "Fuerza e Hipertrofia", "Matutino");
        Entrenador t2 = new Entrenador("T002", "Lucía Herrera", 29, "0996655443", "Funcional y Cardio",   "Vespertino");
        registrarEntrenador(t1);
        registrarEntrenador(t2);

        // Asignaciones iniciales
        Socio s1 = directorioSocios.get("S001");
        Socio s2 = directorioSocios.get("S002");
        if (s1 != null) t1.asignarAlumno(s1);
        if (s2 != null) t2.asignarAlumno(s2);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  CREATE – Registro de Usuarios
    // ════════════════════════════════════════════════════════════════════════
    public boolean registrarSocio(Socio socio) {
        if (directorioSocios.containsKey(socio.getIdUsuario())) return false;
        directorioSocios.put(socio.getIdUsuario(), socio);
        return true;
    }

    public boolean registrarEntrenador(Entrenador entrenador) {
        if (directorioEntrenadores.containsKey(entrenador.getIdUsuario())) return false;
        directorioEntrenadores.put(entrenador.getIdUsuario(), entrenador);
        return true;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  READ – Consultar Usuarios
    // ════════════════════════════════════════════════════════════════════════

    /** Búsqueda por identificación en la estructura indexada: O(1) */
    public Socio buscarSocioPorId(String idSocio) {
        return directorioSocios.get(idSocio);
    }

    public Entrenador buscarEntrenadorPorId(String idEntrenador) {
        return directorioEntrenadores.get(idEntrenador);
    }

    /** Búsqueda por nombre (recorrido lineal, coincidencia parcial) */
    public ArrayList<Socio> buscarSociosPorNombre(String nombre) {
        ArrayList<Socio> resultado = new ArrayList<>();
        String filtro = nombre.toLowerCase().trim();
        for (Socio s : directorioSocios.values()) {
            if (s.getNombre().toLowerCase().contains(filtro)) resultado.add(s);
        }
        return resultado;
    }

    public ArrayList<Socio> obtenerSociosActivos() {
        ArrayList<Socio> activos = new ArrayList<>();
        for (Socio s : directorioSocios.values()) {
            if (s.isActivo()) activos.add(s);
        }
        return activos;
    }

    public ArrayList<Socio> obtenerTodosLosSocios() {
        return new ArrayList<>(directorioSocios.values());
    }

    public ArrayList<Entrenador> obtenerEntrenadoresActivos() {
        ArrayList<Entrenador> activos = new ArrayList<>();
        for (Entrenador t : directorioEntrenadores.values()) {
            if (t.isActivo()) activos.add(t);
        }
        return activos;
    }

    /** Entrenador que tiene a cargo a un socio (null si no está asignado) */
    public Entrenador entrenadorDeSocio(String idSocio) {
        for (Entrenador t : directorioEntrenadores.values()) {
            if (t.isActivo() && t.tieneAlumno(idSocio)) return t;
        }
        return null;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  UPDATE – Actualizar y Asignar Datos
    // ════════════════════════════════════════════════════════════════════════
    public boolean actualizarSocio(String idSocio, String nuevoTelefono, TipoMembresia nuevaMembresia) {
        Socio s = directorioSocios.get(idSocio);
        if (s == null) return false;
        s.setTelefono(nuevoTelefono);
        s.setTipoMembresia(nuevaMembresia);
        return true;
    }

    public boolean actualizarEntrenador(String idEntrenador, String nuevaEspecialidad, String nuevoTurno) {
        Entrenador t = directorioEntrenadores.get(idEntrenador);
        if (t == null) return false;
        t.setEspecialidad(nuevaEspecialidad);
        t.setTurno(nuevoTurno);
        return true;
    }

    /**
     * Vincula dinámicamente un socio a un entrenador.
     * Si el socio ya tenía entrenador, primero se desvincula (re-asignación).
     * Respeta el límite 16:1 — devuelve false si el entrenador está saturado.
     */
    public boolean asignarSocioAEntrenador(String idSocio, String idEntrenador) {
        Socio s      = directorioSocios.get(idSocio);
        Entrenador t = directorioEntrenadores.get(idEntrenador);
        if (s == null || t == null || !s.isActivo() || !t.isActivo()) return false;
        if (t.estaSaturado()) return false;   // control de la relación 16:1

        Entrenador anterior = entrenadorDeSocio(idSocio);
        if (anterior != null) anterior.removerAlumno(idSocio);
        return t.asignarAlumno(s);
    }

    public boolean desvincularSocio(String idSocio) {
        Entrenador t = entrenadorDeSocio(idSocio);
        return (t != null) && t.removerAlumno(idSocio);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  DELETE – Inactivar Perfil (eliminación lógica)
    // ════════════════════════════════════════════════════════════════════════
    public boolean inactivarSocio(String idSocio) {
        Socio s = directorioSocios.get(idSocio);
        if (s == null || !s.isActivo()) return false;
        s.setActivo(false);
        desvincularSocio(idSocio);   // libera el cupo de su entrenador
        return true;
    }

    /** Inactiva al entrenador y libera a todos sus alumnos */
    public boolean inactivarEntrenador(String idEntrenador) {
        Entrenador t = directorioEntrenadores.get(idEntrenador);
        if (t == null || !t.isActivo()) return false;
        t.setActivo(false);
        t.getAlumnosACargo().clear();
        return true;
    }

    // ── Utilitarios ───────────────────────────────────────────────────────────
    public String generarIdSocio() {
        int n = directorioSocios.size() + 1;
        while (directorioSocios.containsKey("S" + String.format("%03d", n))) n++;
        return "S" + String.format("%03d", n);
    }

    public String generarIdEntrenador() {
        int n = directorioEntrenadores.size() + 1;
        while (directorioEntrenadores.containsKey("T" + String.format("%03d", n))) n++;
        return "T" + String.format("%03d", n);
    }

    /** Directorio compartido (lo usan los módulos 3, 4 y 5) */
    public HashMap<String, Socio> getDirectorio() {
        return directorioSocios;
    }
}
