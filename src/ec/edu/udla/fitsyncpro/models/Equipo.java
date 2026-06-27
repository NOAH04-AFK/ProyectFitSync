package ec.edu.udla.fitsyncpro.models;

import ec.edu.udla.fitsyncpro.utils.EstadoEquipo;
import ec.edu.udla.fitsyncpro.utils.GrupoMuscular;

/**
 * EQUIPO: una máquina del gimnasio. Cada equipo es un VÉRTICE del grafo de
 * dependencias (GrafoEquipos). Su estado (OPERATIVO / DAÑADO / MANTENIMIENTO)
 * es clave: cuando una máquina se daña, el grafo propaga el daño en cascada
 * (BFS) a las máquinas que dependen de ella.
 */
public class Equipo {
    private String        idEquipo;
    private String        nombreEquipo;
    private GrupoMuscular grupoMuscular;  // a qué grupo sirve la máquina
    private EstadoEquipo  estado;         // OPERATIVO | DAÑADO | MANTENIMIENTO
    private boolean       activo;         // baja lógica

    public Equipo(String idEquipo, String nombreEquipo, GrupoMuscular grupoMuscular) {
        this.idEquipo = idEquipo;
        this.nombreEquipo = nombreEquipo;
        this.grupoMuscular = grupoMuscular;
        this.estado = EstadoEquipo.OPERATIVO;   // toda máquina nueva nace operativa
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
