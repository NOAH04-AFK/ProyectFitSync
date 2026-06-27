package ec.edu.udla.fitsyncpro.models;

import ec.edu.udla.fitsyncpro.utils.TipoUsuario;

import java.util.LinkedList;

/**
 * ENTRENADOR: hereda de Usuario. Su responsabilidad clave es manejar la lista
 * de alumnos (socios) que tiene a cargo y respetar el LÍMITE 16:1.
 *
 * Estructura de datos: alumnosACargo es una LinkedList<Socio>. Se usa lista
 * enlazada porque las altas/bajas de alumnos son frecuentes y al final de la
 * lista cuestan O(1), sin tener que redimensionar memoria como un arreglo.
 */
public class Entrenador extends Usuario {

    /** Tope de alumnos por entrenador (regla de negocio 16:1 de las horas pico). */
    public static final int MAX_ALUMNOS = 16;

    private String  especialidad;            // ej. "Fuerza e Hipertrofia"
    private String  turno;                   // Matutino | Vespertino | Nocturno
    private boolean activo;                  // baja lógica
    private LinkedList<Socio> alumnosACargo; // socios asignados a este entrenador

    public Entrenador(String idUsuario, String nombre, int edad, String telefono,
                      String especialidad, String turno) {
        super(idUsuario, nombre, edad, telefono, TipoUsuario.ENTRENADOR);
        this.especialidad  = especialidad;
        this.turno         = turno;
        this.activo        = true;
        this.alumnosACargo = new LinkedList<>();
    }

    /**
     * Asigna un alumno SOLO si: (1) no se pasa del tope 16 y (2) no estaba ya
     * asignado. Devuelve true si lo agregó, false si rechazó.
     */
    public boolean asignarAlumno(Socio socio) {
        if (alumnosACargo.size() >= MAX_ALUMNOS) return false;   // entrenador saturado
        if (tieneAlumno(socio.getIdUsuario()))   return false;   // ya estaba asignado
        alumnosACargo.add(socio);                                // inserción O(1)
        return true;
    }

    /** Quita un alumno buscándolo por su ID. Devuelve true si lo encontró y lo sacó. */
    public boolean removerAlumno(String idSocio) {
        for (Socio s : alumnosACargo) {
            if (s.getIdUsuario().equals(idSocio)) {
                alumnosACargo.remove(s);
                return true;
            }
        }
        return false;
    }

    /** ¿Este entrenador ya tiene a ese socio? Recorre la lista comparando IDs. */
    public boolean tieneAlumno(String idSocio) {
        for (Socio s : alumnosACargo) {
            if (s.getIdUsuario().equals(idSocio)) return true;
        }
        return false;
    }

    public LinkedList<Socio> getAlumnosACargo() {
        return alumnosACargo;
    }
    /** Cuántos alumnos tiene ahora (para mostrar la carga X/16). */
    public int  getCargaActual(){
        return alumnosACargo.size();
    }
    /** ¿Ya llegó al tope de 16 alumnos? Lo usa el gestor antes de asignar. */
    public boolean estaSaturado() {
        return alumnosACargo.size() >= MAX_ALUMNOS;
    }

    // ── Getters / setters de los datos del entrenador ───────────────────────
    public String  getEspecialidad(){
        return especialidad;
    }
    public void    setEspecialidad(String e){
        this.especialidad = e;
    }
    public String  getTurno(){
        return turno;
    }
    public void    setTurno(String turno){
        this.turno = turno;
    }
    public boolean isActivo(){
        return activo;
    }
    public void    setActivo(boolean activo){
        this.activo = activo;
    }

    @Override
    public String toString() {
        return getNombre() + " — " + especialidad + " | Turno " + turno
                + " (" + getCargaActual() + "/" + MAX_ALUMNOS + " alumnos)"
                + (activo ? "" : " [INACTIVO]");
    }
}
