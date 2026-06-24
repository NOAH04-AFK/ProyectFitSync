package ec.edu.udla.fitsyncpro.controllers;

import ec.edu.udla.fitsyncpro.estructuras.NodoAVL;
import ec.edu.udla.fitsyncpro.models.Administrador;
import ec.edu.udla.fitsyncpro.models.Equipo;
import ec.edu.udla.fitsyncpro.models.PlantillaReporte;
import ec.edu.udla.fitsyncpro.models.ReporteOperativo;
import ec.edu.udla.fitsyncpro.models.Socio;
import ec.edu.udla.fitsyncpro.utils.EstadoEquipo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Módulo 5 – Administración y Reportes de Gestión
 *
 * Consolida la información operativa de los demás módulos para generar
 * reportes analíticos e indicadores estratégicos (retención / deserción),
 * y administra los roles, permisos y credenciales del sistema (RBAC).
 *
 * Estructuras de datos:
 * ─ ArrayList<ReporteOperativo>        → historial de reportes generados
 * ─ HashMap<String, PlantillaReporte>  → plantillas programadas (O(1), baja lógica)
 * ─ HashMap<String, Administrador>     → cuentas administrativas (RBAC, O(1))
 */
public class GestorReportes {


    private static final double CUOTA_MENSUAL_USD = 35.0;

    private ArrayList<ReporteOperativo>       historialReportes;
    private HashMap<String, PlantillaReporte> plantillas;
    private HashMap<String, Administrador>    cuentasAdministrativas;

    private GestorSocios      gestorSocios;
    private GestorPerformance gestorPerformance;
    private GestorRutinas     gestorRutinas;
    private int contadorReporte = 1;

    public GestorReportes(GestorSocios gestorSocios,
                          GestorPerformance gestorPerformance,
                          GestorRutinas gestorRutinas) {
        this.gestorSocios      = gestorSocios;
        this.gestorPerformance = gestorPerformance;
        this.gestorRutinas     = gestorRutinas;
        this.historialReportes      = new ArrayList<>();
        this.plantillas             = new HashMap<>();
        this.cuentasAdministrativas = new HashMap<>();
        cargarDatosPrueba();
    }

    private void cargarDatosPrueba() {
        registrarPlantilla(new PlantillaReporte("PL001", "Asistencia semanal",
                "Flujo de socios y ocupación en horas pico", "Semanal"));
        registrarPlantilla(new PlantillaReporte("PL002", "Retención mensual",
                "Tasa de deserción y socios en riesgo", "Mensual"));
        registrarPlantilla(new PlantillaReporte("PL003", "Estado de equipos",
                "Máquinas operativas, dañadas y en mantenimiento", "Diaria"));

        cuentasAdministrativas.put("admin", new Administrador(
                "A001", "Administrador General", 40, "0999999999",
                "admin", "admin123", "TOTAL"));
        cuentasAdministrativas.put("recepcion", new Administrador(
                "A002", "Recepción", 26, "0988888888",
                "recepcion", "rec123", "CONSULTA"));
    }

    // ════════════════════════════════════════════════════════════════════════
    //  CREATE – Consolidación de Reportes Operativos
    // ════════════════════════════════════════════════════════════════════════
    public ReporteOperativo generarReporteOperativo() {
        ArrayList<Socio> todos   = gestorSocios.obtenerTodosLosSocios();
        ArrayList<Socio> activos = gestorSocios.obtenerSociosActivos();

        int operativos = 0, danados = 0, mantenimiento = 0, inactivosEq = 0;
        for (Equipo eq : gestorRutinas.obtenerEquipos()) {
            if (!eq.isActivo())                                  inactivosEq++;
            else if (eq.getEstado() == EstadoEquipo.OPERATIVO)   operativos++;
            else if (eq.getEstado() == EstadoEquipo.DAÑADO)      danados++;
            else                                                 mantenimiento++;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("════ REPORTE OPERATIVO CONSOLIDADO ════\n\n");
        sb.append("■ SOCIOS\n");
        sb.append("  Registrados: ").append(todos.size())
                .append(" | Activos: ").append(activos.size())
                .append(" | Inactivos: ").append(todos.size() - activos.size()).append("\n");
        sb.append("  Entrenadores activos: ").append(gestorSocios.obtenerEntrenadoresActivos().size()).append("\n\n");

        sb.append("■ CONSTANCIA (Árbol AVL)\n");
        sb.append("  ").append(gestorPerformance.infoArbol()).append("\n");
        sb.append("  Top 3 del ranking:\n");
        ArrayList<NodoAVL> ranking = gestorPerformance.obtenerRanking();
        for (int i = 0; i < Math.min(3, ranking.size()); i++) {
            sb.append("    ").append(i + 1).append(". ")
                    .append(ranking.get(i).getSocio().getNombre())
                    .append(" — ").append(ranking.get(i).getPuntaje()).append(" pts\n");
        }

        sb.append("\n■ INFRAESTRUCTURA (Grafo de dependencias)\n");
        sb.append("  Vértices: ").append(gestorRutinas.getGrafoDependencias().getTotalVertices())
                .append(" | Aristas: ").append(gestorRutinas.getGrafoDependencias().getTotalAristas()).append("\n");
        sb.append("  Equipos → Operativos: ").append(operativos)
                .append(" | Dañados: ").append(danados)
                .append(" | En mantenimiento: ").append(mantenimiento)
                .append(" | De baja: ").append(inactivosEq).append("\n");

        sb.append("\n■ ALERTAS DE RETENCIÓN\n");
        ArrayList<String> alertas = gestorPerformance.obtenerAlertasInactividad();
        if (alertas.isEmpty()) sb.append("  Sin socios en riesgo.\n");
        for (String a : alertas) sb.append("  ⚠ ").append(a).append("\n");

        ReporteOperativo reporte = new ReporteOperativo(
                "REP" + String.format("%03d", contadorReporte++),
                "Reporte Operativo Consolidado", sb.toString());
        historialReportes.add(reporte);
        return reporte;
    }

    public ArrayList<ReporteOperativo> obtenerHistorialReportes() {
        return historialReportes;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  READ – Indicadores de Retención
    // ════════════════════════════════════════════════════════════════════════
    public String obtenerIndicadoresRetencion() {
        ArrayList<Socio> todos   = gestorSocios.obtenerTodosLosSocios();
        ArrayList<Socio> activos = gestorSocios.obtenerSociosActivos();
        int inactivos = todos.size() - activos.size();
        double tasaDesercion = todos.isEmpty() ? 0 : (inactivos * 100.0) / todos.size();

        ArrayList<String> enRiesgo = gestorPerformance.obtenerAlertasInactividad();
        double perdidaActual    = inactivos * CUOTA_MENSUAL_USD;
        double perdidaPotencial = enRiesgo.size() * CUOTA_MENSUAL_USD;

        StringBuilder sb = new StringBuilder();
        sb.append("════ INDICADORES DE RETENCIÓN ════\n\n");
        sb.append("Tasa de deserción: ").append(String.format("%.1f", tasaDesercion)).append(" %\n");
        sb.append("Socios dados de baja: ").append(inactivos)
                .append("  → pérdida recurrente: $").append(String.format("%.2f", perdidaActual)).append("/mes\n\n");
        sb.append("Socios en riesgo de abandono (").append(enRiesgo.size()).append("):\n");
        if (enRiesgo.isEmpty()) sb.append("  Ninguno. ✔\n");
        for (String s : enRiesgo) sb.append("  ⚠ ").append(s).append("\n");
        sb.append("\nPérdida potencial si abandonan: $")
                .append(String.format("%.2f", perdidaPotencial)).append("/mes\n");
        sb.append("\nRecomendación: aplicar campañas de fidelización (Day Pass, retos grupales)\n");
        sb.append("a los socios en riesgo antes de su fecha de renovación.");
        return sb.toString();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  UPDATE – Roles, Permisos y Credenciales (RBAC)
    // ════════════════════════════════════════════════════════════════════════

    /** Valida el acceso de una cuenta administrativa; null si las credenciales fallan */
    public Administrador validarAcceso(String usuario, String clave) {
        Administrador cuenta = cuentasAdministrativas.get(usuario);
        return (cuenta != null && cuenta.validarCredenciales(usuario, clave)) ? cuenta : null;
    }

    public boolean actualizarCredenciales(String usuario, String nuevaClave, String nuevoNivel) {
        Administrador cuenta = cuentasAdministrativas.get(usuario);
        if (cuenta == null || !cuenta.isActivo()) return false;
        if (nuevaClave != null && !nuevaClave.trim().isEmpty()) cuenta.setClave(nuevaClave.trim());
        if (nuevoNivel != null && !nuevoNivel.trim().isEmpty()) cuenta.setNivelAcceso(nuevoNivel.trim());
        return true;
    }

    public ArrayList<Administrador> obtenerCuentas() {
        return new ArrayList<>(cuentasAdministrativas.values());
    }

    // ════════════════════════════════════════════════════════════════════════
    //  DELETE – Inactivar Plantillas de Reportes (baja lógica)
    // ════════════════════════════════════════════════════════════════════════
    public void registrarPlantilla(PlantillaReporte plantilla) {
        plantillas.put(plantilla.getIdPlantilla(), plantilla);
    }

    public boolean inactivarPlantilla(String idPlantilla) {
        PlantillaReporte p = plantillas.get(idPlantilla);
        if (p == null || !p.isActiva()) return false;
        p.setActiva(false);
        return true;
    }

    public ArrayList<PlantillaReporte> obtenerPlantillas(boolean soloActivas) {
        ArrayList<PlantillaReporte> lista = new ArrayList<>();
        for (PlantillaReporte p : plantillas.values()) {
            if (!soloActivas || p.isActiva()) lista.add(p);
        }
        return lista;
    }

    public String generarIdPlantilla() {
        int n = plantillas.size() + 1;
        while (plantillas.containsKey("PL" + String.format("%03d", n))) n++;
        return "PL" + String.format("%03d", n);
    }
}
