package ec.edu.udla.fitsyncpro.models;

import ec.edu.udla.fitsyncpro.utils.TipoMembresia;
import ec.edu.udla.fitsyncpro.utils.TipoUsuario;

import java.time.LocalDate;
import java.util.ArrayList;

public class Socio  extends Usuario{
    private TipoMembresia tipoMembresia;
    private LocalDate fechaSuscripcion;
    private boolean          activo;
    private ArrayList<RegistroFisico> historialFisico;

    public Socio(String idUsuario, String nombre, int edad, String telefono, TipoMembresia tipoMembresia) {
        super(idUsuario, nombre, edad, telefono, TipoUsuario.USUARIO);
        this.tipoMembresia    = tipoMembresia;
        this.fechaSuscripcion = LocalDate.now();
        this.activo           = true;
        this.historialFisico  = new ArrayList<>();
    }


    public TipoMembresia getTipoMembresia()                { return tipoMembresia; }
    public void          setTipoMembresia(TipoMembresia t){ this.tipoMembresia = t; }
    public LocalDate     getFechaSuscripcion()            { return fechaSuscripcion; }
    public boolean       isActivo()                       { return activo; }
    public void          setActivo(boolean activo)        { this.activo = activo; }


    public void                      agregarRegistroFisico(RegistroFisico r) { historialFisico.add(r); }
    public ArrayList<RegistroFisico> getHistorialFisico()                    { return historialFisico; }
    public RegistroFisico            getUltimoRegistro() {
        if (historialFisico.isEmpty()) return null;
        return historialFisico.get(historialFisico.size() - 1);
    }

    @Override
    public String toString() {
        return getNombre() + " — " + tipoMembresia.name() + (activo ? "" : " [INACTIVO]");
    }
}
