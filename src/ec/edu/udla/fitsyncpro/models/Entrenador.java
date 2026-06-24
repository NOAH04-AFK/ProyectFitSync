package ec.edu.udla.fitsyncpro.models;

import ec.edu.udla.fitsyncpro.utils.TipoUsuario;

import java.util.LinkedList;

public class Entrenador extends Usuario{

    public static final int MAX_ALUMNOS = 16;

    private String  especialidad;
    private String  turno;
    private boolean activo;
    private LinkedList<Socio> alumnosACargo;

    public Entrenador(String idUsuario, String nombre, int edad, String telefono,
                      String especialidad, String turno) {
        super(idUsuario, nombre, edad, telefono, TipoUsuario.ENTRENADOR);
        this.especialidad  = especialidad;
        this.turno         = turno;
        this.activo        = true;
        this.alumnosACargo = new LinkedList<>();
    }

    public boolean asignarAlumno(Socio socio) {
        if (alumnosACargo.size() >= MAX_ALUMNOS) return false;   // saturado
        if (tieneAlumno(socio.getIdUsuario()))   return false;   // ya asignado
        alumnosACargo.add(socio);                                // O(1)
        return true;
    }

    public boolean removerAlumno(String idSocio) {
        for (Socio s : alumnosACargo) {
            if (s.getIdUsuario().equals(idSocio)) {
                alumnosACargo.remove(s);
                return true;
            }
        }
        return false;
    }

    public boolean tieneAlumno(String idSocio) {
        for (Socio s : alumnosACargo) {
            if (s.getIdUsuario().equals(idSocio)) return true;
        }
        return false;
    }

    public LinkedList<Socio> getAlumnosACargo() {
        return alumnosACargo;
    }
    public int  getCargaActual(){
        return alumnosACargo.size();
    }
    public boolean estaSaturado() {
        return alumnosACargo.size() >= MAX_ALUMNOS;
    }

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
