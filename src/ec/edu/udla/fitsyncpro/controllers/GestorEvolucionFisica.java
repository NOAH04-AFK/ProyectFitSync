package ec.edu.udla.fitsyncpro.controllers;

import ec.edu.udla.fitsyncpro.models.RegistroFisico;
import ec.edu.udla.fitsyncpro.models.Socio;
import ec.edu.udla.fitsyncpro.utils.TipoMembresia;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Módulo 3 – Validación de Condición Física y Salud
 *
 * Gestiona el CRUD de evaluaciones físicas y el directorio de socios.
 * ─ Socios indexados en HashMap<idSocio, Socio>  → O(1) en búsqueda
 * ─ Historial por socio en ArrayList<RegistroFisico> dentro de Socio
 */
public class GestorEvolucionFisica {

    // ── Estructura principal ──────────────────────────────────────────────────
    private HashMap<String, Socio> directorioSocios;   // O(1) acceso por ID
    private int contadorRegistro;   // para generar IDs únicos


    private static final String[] RESTRICCIONES_ALTO_IMPACTO =
            { "rodilla", "columna", "hernia", "lumbar", "tobillo", "cadera fractura" };

    public GestorEvolucionFisica() {
        directorioSocios = new HashMap<>();
        contadorRegistro = 1;
        cargarSociosDePrueba();
    }

    /** Directorio compartido: lo usan los módulos 1, 4 y 5 a través de GestorSocios */
    public HashMap<String, Socio> getDirectorio() {
        return directorioSocios;
    }


    private void cargarSociosDePrueba() {
        if (!directorioSocios.isEmpty()) return;   // evita duplicar si el directorio es compartido
        Socio s1 = new Socio("S001", "Carlos Andrade",   28, "0991234567", TipoMembresia.MENSUAL);
        Socio s2 = new Socio("S002", "María Gómez",      35, "0987654321", TipoMembresia.ANUAL);
        Socio s3 = new Socio("S003", "Andrés Villacís",  22, "0998877665", TipoMembresia.MENSUAL);

        // Pre-cargar un registro físico inicial a s1 para mostrar historial
        RegistroFisico r0 = new RegistroFisico(
                "RF000", "S001",
                78.5, 1.75, 18.0, 82.0, 95.0,
                "Evaluación inicial", "Ninguna");
        s1.agregarRegistroFisico(r0);

        directorioSocios.put(s1.getIdUsuario(), s1);
        directorioSocios.put(s2.getIdUsuario(), s2);
        directorioSocios.put(s3.getIdUsuario(), s3);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  CRUD – SOCIOS  (necesario para que la UI pueda seleccionar a quien evaluar)
    // ════════════════════════════════════════════════════════════════════════

    /** CREATE socio */
    public boolean registrarSocio(Socio socio) {
        if (directorioSocios.containsKey(socio.getIdUsuario())) return false;
        directorioSocios.put(socio.getIdUsuario(), socio);
        return true;
    }

    /** READ – todos los socios activos */
    public ArrayList<Socio> obtenerSociosActivos() {
        ArrayList<Socio> activos = new ArrayList<>();
        for (Socio s : directorioSocios.values()) {
            if (s.isActivo()) activos.add(s);
        }
        return activos;
    }

    /** READ – buscar por ID (O(1)) */
    public Socio buscarSocio(String idSocio) {
        return directorioSocios.get(idSocio);
    }

    /** DELETE lógico – socio */
    public boolean inactivarSocio(String idSocio) {
        Socio s = directorioSocios.get(idSocio);
        if (s != null) { s.setActivo(false); return true; }
        return false;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  CRUD – REGISTROS FÍSICOS
    // ════════════════════════════════════════════════════════════════════════

    /**
     * CREATE – Registro de Evaluación.
     * Calcula el IMC automáticamente dentro del constructor de RegistroFisico.
     */
    public RegistroFisico crearRegistro(String idSocio,
                                        double peso, double estatura,
                                        double porcentajeGrasa,
                                        double cintura, double cadera,
                                        String observaciones, String lesiones) {

        Socio socio = directorioSocios.get(idSocio);
        if (socio == null || !socio.isActivo()) return null;

        String idReg = "RF" + String.format("%03d", contadorRegistro++);
        RegistroFisico nuevo = new RegistroFisico(
                idReg, idSocio,
                peso, estatura, porcentajeGrasa,
                cintura, cadera,
                observaciones, lesiones);

        socio.agregarRegistroFisico(nuevo);
        return nuevo;
    }

    /**
     * READ – Historial activo de un socio (para mostrar en JList/JTable).
     * Solo devuelve registros con activo == true.
     */
    public ArrayList<RegistroFisico> obtenerHistorial(String idSocio) {
        Socio socio = directorioSocios.get(idSocio);
        if (socio == null) return new ArrayList<>();
        ArrayList<RegistroFisico> activos = new ArrayList<>();
        for (RegistroFisico r : socio.getHistorialFisico()) {
            if (r.isActivo()) activos.add(r);
        }
        return activos;
    }

    /**
     * READ – Último registro activo de un socio (para panel de resumen).
     */
    public RegistroFisico obtenerUltimoRegistro(String idSocio) {
        ArrayList<RegistroFisico> historial = obtenerHistorial(idSocio);
        if (historial.isEmpty()) return null;
        return historial.get(historial.size() - 1);
    }

    /**
     * UPDATE – Actualizar estado antropométrico en un registro existente.
     */
    public boolean actualizarRegistro(String idSocio, String idRegistro,
                                      double nuevoPeso, double nuevoPorcentajeGrasa,
                                      double nuevaCintura, double nuevaCadera,
                                      String nuevasObs, String nuevasLesiones) {
        Socio socio = directorioSocios.get(idSocio);
        if (socio == null) return false;
        for (RegistroFisico r : socio.getHistorialFisico()) {
            if (r.getIdRegistro().equals(idRegistro) && r.isActivo()) {
                r.setPeso(nuevoPeso);
                r.setPorcentajeGrasa(nuevoPorcentajeGrasa);
                r.setCircunferenciaCintura(nuevaCintura);
                r.setCircunferenciaCadera(nuevaCadera);
                r.setObservaciones(nuevasObs);
                r.setLesiones(nuevasLesiones);
                return true;
            }
        }
        return false;
    }

    /**
     * DELETE lógico – Inactivar registro erróneo o duplicado.
     * Preserva la trazabilidad histórica (no elimina del ArrayList).
     */
    public boolean inactivarRegistro(String idSocio, String idRegistro) {
        Socio socio = directorioSocios.get(idSocio);
        if (socio == null) return false;
        for (RegistroFisico r : socio.getHistorialFisico()) {
            if (r.getIdRegistro().equals(idRegistro) && r.isActivo()) {
                r.setActivo(false);
                return true;
            }
        }
        return false;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  VALIDACIÓN DE SALUD – filtro preventivo de seguridad
    // ════════════════════════════════════════════════════════════════════════

    public String verificarContraindicacion(String idSocio) {
        RegistroFisico ultimo = obtenerUltimoRegistro(idSocio);
        if (ultimo == null || ultimo.getLesiones() == null
                || ultimo.getLesiones().isBlank()
                || ultimo.getLesiones().equalsIgnoreCase("ninguna")) {
            return null;
        }
        String lesionesLower = ultimo.getLesiones().toLowerCase();
        for (String keyword : RESTRICCIONES_ALTO_IMPACTO) {
            if (lesionesLower.contains(keyword)) {
                return "⚠ Restricción detectada: \"" + ultimo.getLesiones()
                        + "\". Evitar ejercicios de alto impacto articular.";
            }
        }
        return null;
    }

    public String generarIdSocio() {
        return "S" + String.format("%03d", directorioSocios.size() + 1);
    }
}
