package ec.edu.udla.fitsyncpro.controllers;

import ec.edu.udla.fitsyncpro.estructuras.ArbolAVL;
import ec.edu.udla.fitsyncpro.estructuras.NodoAVL;
import ec.edu.udla.fitsyncpro.models.Recompensa;
import ec.edu.udla.fitsyncpro.models.RegistroPerformance;
import ec.edu.udla.fitsyncpro.models.Socio;
import ec.edu.udla.fitsyncpro.utils.TipoActividad;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Módulo 4 – Monitoreo de Performance y Gamificación
 *
 * Estructuras de datos:
 * ─ ArrayList<RegistroPerformance> → bitácora histórica de asistencias y cargas
 * ─ HashMap<String, Integer>       → puntaje de constancia por socio (O(1))
 * ─ HashMap<String, Double>        → mejor carga por socio+ejercicio (sobrecarga progresiva)
 * ─ LinkedList<Recompensa>         → logros obtenidos por cada socio
 * ─ Árbol AVL                      → ranking auto-balanceado por puntaje:
 *   cada cambio de puntaje elimina y reinserta el nodo en O(log n), y el
 *   recorrido in-order inverso entrega el ranking completo ya ordenado.
 */
public class GestorPerformance {


    // ── Reglas de puntuación de la gamificación ───────────────────────────────
    public static final int PTS_ASISTENCIA      = 10;
    public static final int PTS_CARGA           = 5;
    public static final int PTS_SOBRECARGA      = 10;  // bono por superar la mejor marca
    public static final int DIAS_ALERTA         = 7;   // umbral de inactividad

    private ArrayList<RegistroPerformance>       bitacoraHistorica;
    private HashMap<String, Integer>             puntajePorSocio;
    private HashMap<String, Double>              mejorCargaPorEjercicio; // clave: idSocio|ejercicio
    private ArrayList<Recompensa>                recompensasConfiguradas;
    private HashMap<String, LinkedList<Recompensa>> logrosPorSocio;
    private ArbolAVL                             rankingConstancia;
    private HashMap<String, Boolean>             enRanking;   // tabla activa del ranking
    private GestorSocios                         gestorSocios;
    private int                                  contadorRegistro;

    public GestorPerformance(GestorSocios gestorSocios) {
        this.gestorSocios            = gestorSocios;
        this.bitacoraHistorica       = new ArrayList<>();
        this.puntajePorSocio         = new HashMap<>();
        this.mejorCargaPorEjercicio  = new HashMap<>();
        this.recompensasConfiguradas = new ArrayList<>();
        this.logrosPorSocio          = new HashMap<>();
        this.rankingConstancia       = new ArbolAVL();
        this.enRanking               = new HashMap<>();
        this.contadorRegistro        = 1;

        // Recompensas configuradas (hitos de desempeño)
        recompensasConfiguradas.add(new Recompensa("RW1", "Day Pass para un acompañante", 50));
        recompensasConfiguradas.add(new Recompensa("RW2", "10% de descuento en la renovación", 120));
        recompensasConfiguradas.add(new Recompensa("RW3", "Mes adicional con 25% de descuento", 250));

        // Inserta a todos los socios activos en el árbol con puntaje 0
        for (Socio s : gestorSocios.obtenerSociosActivos()) {
            puntajePorSocio.put(s.getIdUsuario(), 0);
            logrosPorSocio.put(s.getIdUsuario(), new LinkedList<>());
            rankingConstancia.insertar(s, 0);
            enRanking.put(s.getIdUsuario(), true);
        }

        cargarDatosPrueba();
    }

    // ── Datos de prueba (historial simulado de días anteriores) ───────────────
    private void cargarDatosPrueba() {
        LocalDate hoy = LocalDate.now();
        registrarAsistencia("S001", hoy.minusDays(3));
        registrarAsistencia("S001", hoy.minusDays(2));
        registrarAsistencia("S001", hoy.minusDays(1));
        registrarCarga("S001", "Press de Banca", 60, hoy.minusDays(2));
        registrarCarga("S001", "Press de Banca", 65, hoy.minusDays(1));   // sobrecarga progresiva

        registrarAsistencia("S002", hoy.minusDays(5));
        registrarAsistencia("S002", hoy.minusDays(4));
        registrarAsistencia("S002", hoy.minusDays(2));
        registrarAsistencia("S002", hoy.minusDays(1));
        registrarCarga("S002", "Sentadilla Libre", 80, hoy.minusDays(4));
        registrarCarga("S002", "Sentadilla Libre", 90, hoy.minusDays(1)); // sobrecarga progresiva

        registrarAsistencia("S003", hoy.minusDays(10));   // inactivo → alerta de retención
        registrarAsistencia("S004", hoy.minusDays(1));
        // S005 nunca ha asistido → alerta de retención
    }

    // ════════════════════════════════════════════════════════════════════════
    //  CREATE – Registrar Asistencia y Cargas (bitácora)
    // ════════════════════════════════════════════════════════════════════════

    /** Registra la asistencia de HOY (uso normal desde la interfaz) */
    public String registrarAsistenciaHoy(String idSocio) {
        return registrarAsistencia(idSocio, LocalDate.now());
    }

    /** Registra una asistencia en una fecha dada. Solo una asistencia por día. */
    public String registrarAsistencia(String idSocio, LocalDate fecha) {
        Socio socio = gestorSocios.buscarSocioPorId(idSocio);
        if (socio == null || !socio.isActivo()) return "Socio no válido o inactivo.";

        // Validación: una sola asistencia por día
        for (RegistroPerformance r : bitacoraHistorica) {
            if (r.isActivo() && r.getIdSocio().equals(idSocio)
                    && r.getTipo() == TipoActividad.ASISTENCIA
                    && r.getFecha().equals(fecha)) {
                return "La asistencia de ese día ya estaba registrada.";
            }
        }

        RegistroPerformance reg = new RegistroPerformance(
                generarIdRegistro(), idSocio, fecha,
                TipoActividad.ASISTENCIA, "Acceso al gimnasio", 0, PTS_ASISTENCIA);
        bitacoraHistorica.add(reg);
        sumarPuntos(socio, PTS_ASISTENCIA);
        return "Asistencia registrada (+" + PTS_ASISTENCIA + " pts).";
    }

    /** Registra una carga de HOY */
    public String registrarCargaHoy(String idSocio, String ejercicio, double kg) {
        return registrarCarga(idSocio, ejercicio, kg, LocalDate.now());
    }

    /**
     * Registra un levantamiento. Si supera la mejor marca previa del socio en
     * ese ejercicio, se detecta SOBRECARGA PROGRESIVA y se otorga un bono.
     */
    public String registrarCarga(String idSocio, String ejercicio, double kg, LocalDate fecha) {
        Socio socio = gestorSocios.buscarSocioPorId(idSocio);
        if (socio == null || !socio.isActivo()) return "Socio no válido o inactivo.";
        if (kg <= 0 || ejercicio == null || ejercicio.trim().isEmpty()) return "Datos de carga inválidos.";

        String clave = idSocio + "|" + ejercicio.trim().toLowerCase();
        Double mejorMarca = mejorCargaPorEjercicio.get(clave);

        int puntos = PTS_CARGA;
        String detalle = "Carga registrada (+" + PTS_CARGA + " pts).";
        if (mejorMarca == null || kg > mejorMarca) {
            if (mejorMarca != null) {
                puntos += PTS_SOBRECARGA;
                detalle = "¡Sobrecarga progresiva! Superó su marca de " + mejorMarca
                        + " kg (+" + puntos + " pts).";
            }
            mejorCargaPorEjercicio.put(clave, kg);
        }

        RegistroPerformance reg = new RegistroPerformance(
                generarIdRegistro(), idSocio, fecha,
                TipoActividad.CARGA, ejercicio.trim(), kg, puntos);
        bitacoraHistorica.add(reg);
        sumarPuntos(socio, puntos);
        return detalle;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  READ – Monitorear y Evaluar Progreso
    // ════════════════════════════════════════════════════════════════════════

    /** Resumen del rendimiento del socio + alerta automática de inactividad */
    public String evaluarProgreso(String idSocio) {
        Socio socio = gestorSocios.buscarSocioPorId(idSocio);
        if (socio == null) return "Socio no encontrado.";

        int asistencias = 0;
        int cargas      = 0;
        LocalDate ultima = null;
        for (RegistroPerformance r : bitacoraHistorica) {
            if (!r.isActivo() || !r.getIdSocio().equals(idSocio)) continue;
            if (r.getTipo() == TipoActividad.ASISTENCIA) {
                asistencias++;
                if (ultima == null || r.getFecha().isAfter(ultima)) ultima = r.getFecha();
            } else {
                cargas++;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("── Progreso de ").append(socio.getNombre()).append(" ──\n");
        sb.append("Asistencias registradas: ").append(asistencias).append("\n");
        sb.append("Levantamientos en bitácora: ").append(cargas).append("\n");
        sb.append("Puntaje de constancia: ").append(getPuntaje(idSocio)).append(" pts\n");

        int pos = posicionEnRanking(idSocio);
        sb.append("Posición en ranking: ").append(pos > 0 ? "#" + pos : "fuera de la tabla activa").append("\n");

        if (ultima == null) {
            sb.append("\n⚠ ALERTA: nunca ha registrado asistencia. Activar protocolo de retención.");
        } else {
            long dias = ChronoUnit.DAYS.between(ultima, LocalDate.now());
            sb.append("Última asistencia: ").append(ultima).append(" (hace ").append(dias).append(" días)\n");
            if (dias >= DIAS_ALERTA) {
                sb.append("\n⚠ ALERTA: ").append(dias)
                        .append(" días de inactividad. Activar protocolo de retención.");
            } else {
                sb.append("\n✔ Cumplimiento dentro del rango esperado.");
            }
        }
        return sb.toString();
    }

    /** Alertas de inactividad de todos los socios activos (las usa el Módulo 5) */
    public ArrayList<String> obtenerAlertasInactividad() {
        ArrayList<String> alertas = new ArrayList<>();
        for (Socio s : gestorSocios.obtenerSociosActivos()) {
            LocalDate ultima = ultimaAsistencia(s.getIdUsuario());
            if (ultima == null) {
                alertas.add(s.getNombre() + " (" + s.getIdUsuario() + "): sin asistencias registradas");
            } else {
                long dias = ChronoUnit.DAYS.between(ultima, LocalDate.now());
                if (dias >= DIAS_ALERTA) {
                    alertas.add(s.getNombre() + " (" + s.getIdUsuario() + "): " + dias + " días sin asistir");
                }
            }
        }
        return alertas;
    }

    public LocalDate ultimaAsistencia(String idSocio) {
        LocalDate ultima = null;
        for (RegistroPerformance r : bitacoraHistorica) {
            if (r.isActivo() && r.getIdSocio().equals(idSocio)
                    && r.getTipo() == TipoActividad.ASISTENCIA) {
                if (ultima == null || r.getFecha().isAfter(ultima)) ultima = r.getFecha();
            }
        }
        return ultima;
    }

    /** Bitácora activa de un socio (para la JList de la interfaz) */
    public ArrayList<RegistroPerformance> obtenerBitacora(String idSocio) {
        ArrayList<RegistroPerformance> lista = new ArrayList<>();
        for (RegistroPerformance r : bitacoraHistorica) {
            if (r.isActivo() && r.getIdSocio().equals(idSocio)) lista.add(r);
        }
        return lista;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  UPDATE – Actualizar Logros y Beneficios (automático por hitos)
    // ════════════════════════════════════════════════════════════════════════

    /** Suma puntos y REORDENA el nodo del socio en el Árbol AVL: O(log n) */
    private void sumarPuntos(Socio socio, int puntos) {
        String id     = socio.getIdUsuario();
        int    actual = getPuntaje(id);
        int    nuevo  = actual + puntos;

        if (Boolean.TRUE.equals(enRanking.get(id))) {
            rankingConstancia.eliminar(socio, actual);   // saca el nodo viejo  O(log n)
            rankingConstancia.insertar(socio, nuevo);    // inserta re-balanceando O(log n)
        }
        puntajePorSocio.put(id, nuevo);
        revisarRecompensas(socio, nuevo);
    }

    /** Asigna automáticamente las recompensas cuyo umbral se acaba de alcanzar */
    private void revisarRecompensas(Socio socio, int puntaje) {
        LinkedList<Recompensa> logros =
                logrosPorSocio.computeIfAbsent(socio.getIdUsuario(), k -> new LinkedList<>());
        for (Recompensa r : recompensasConfiguradas) {
            if (r.isActiva() && puntaje >= r.getPuntosRequeridos() && !logros.contains(r)) {
                logros.add(r);
            }
        }
    }

    public LinkedList<Recompensa> obtenerLogros(String idSocio) {
        return logrosPorSocio.computeIfAbsent(idSocio, k -> new LinkedList<>());
    }

    public ArrayList<Recompensa> getRecompensasConfiguradas() {
        return recompensasConfiguradas;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  DELETE / Inactivar – Gestión de Clasificaciones (Árbol AVL)
    // ════════════════════════════════════════════════════════════════════════

    /** Remueve lógicamente al socio de la tabla activa del ranking (nodo fuera del AVL) */
    public boolean removerDelRanking(String idSocio) {
        Socio socio = gestorSocios.buscarSocioPorId(idSocio);
        if (socio == null || !Boolean.TRUE.equals(enRanking.get(idSocio))) return false;
        rankingConstancia.eliminar(socio, getPuntaje(idSocio));
        enRanking.put(idSocio, false);
        return true;
    }

    /** Reincorpora al socio al ranking con su puntaje acumulado */
    public boolean reincorporarAlRanking(String idSocio) {
        Socio socio = gestorSocios.buscarSocioPorId(idSocio);
        if (socio == null || Boolean.TRUE.equals(enRanking.get(idSocio))) return false;
        rankingConstancia.insertar(socio, getPuntaje(idSocio));
        enRanking.put(idSocio, true);
        return true;
    }

    /** Asegura que un socio recién creado participe del ranking */
    public void incorporarSocioNuevo(String idSocio) {
        Socio socio = gestorSocios.buscarSocioPorId(idSocio);
        if (socio != null && enRanking.get(idSocio) == null) {
            puntajePorSocio.put(idSocio, 0);
            logrosPorSocio.put(idSocio, new LinkedList<>());
            rankingConstancia.insertar(socio, 0);
            enRanking.put(idSocio, true);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  RANKING (recorrido del árbol)
    // ════════════════════════════════════════════════════════════════════════

    /** Ranking completo en orden descendente: recorrido in-order inverso O(n) */
    public ArrayList<NodoAVL> obtenerRanking() {
        return rankingConstancia.obtenerRankingDescendente();
    }

    public int posicionEnRanking(String idSocio) {
        ArrayList<NodoAVL> ranking = obtenerRanking();
        for (int i = 0; i < ranking.size(); i++) {
            if (ranking.get(i).getSocio().getIdUsuario().equals(idSocio)) return i + 1;
        }
        return -1;
    }

    public int getPuntaje(String idSocio) {
        Integer p = puntajePorSocio.get(idSocio);
        return (p != null) ? p : 0;
    }

    /** Datos del árbol para mostrar en la interfaz (defensa técnica) */
    public String infoArbol() {
        int n = rankingConstancia.getTotalNodos();
        return "Árbol AVL → nodos: " + n
                + " | altura: " + rankingConstancia.getAlturaArbol()
                + " | óptimo ≈ log2(n): " + (n > 0 ? (int) Math.ceil(Math.log(n + 1) / Math.log(2)) : 0);
    }

    private String generarIdRegistro() {
        return "P" + String.format("%04d", contadorRegistro++);
    }
}
