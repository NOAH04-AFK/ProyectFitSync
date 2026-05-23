package ec.edu.udla.fitsyncpro.models;

import ec.edu.udla.fitsyncpro.utils.EstadoEquipo;
import ec.edu.udla.fitsyncpro.utils.GrupoMuscular;

public class Equipo {
    private String idEquipo;
    private String nombreEquipo;
    private GrupoMuscular grupoMuscular;
    private EstadoEquipo estado;
    private boolean activo;

    public Equipo(String idEquipo, String nombreEquipo, GrupoMuscular grupoMuscular) {
        this.idEquipo = idEquipo;
        this.nombreEquipo = nombreEquipo;
        this.grupoMuscular = grupoMuscular;
        this.estado = EstadoEquipo.OPERATIVO;
        this.activo = true;
    }

    public String getIdEquipo() { return idEquipo; }
    public String getNombreEquipo() { return nombreEquipo; }
    public GrupoMuscular getGrupoMuscular() { return grupoMuscular; }
    public EstadoEquipo getEstado() { return estado; }
    public void setEstado(EstadoEquipo estado) { this.estado = estado; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        String estadoStr = activo ? estado.name() : "INACTIVO";
        return "[" + grupoMuscular.name() + "] " + nombreEquipo + " — " + estadoStr;
    }
}
