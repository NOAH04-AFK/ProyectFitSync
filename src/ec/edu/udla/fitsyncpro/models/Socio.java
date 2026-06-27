package ec.edu.udla.fitsyncpro.models;

import ec.edu.udla.fitsyncpro.utils.TipoMembresia;
import ec.edu.udla.fitsyncpro.utils.TipoUsuario;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * SOCIO: cliente del gimnasio. HEREDA de Usuario (por eso "extends Usuario"),
 * asi que ya tiene id, nombre, edad y telefono; aqui solo se agrega lo propio
 * del socio (membresia, estado y su historial fisico).
 *
 * Estructura de datos: cada socio guarda su propio historial de evaluaciones
 * en un ArrayList<RegistroFisico> (lista que crece dinamicamente).
 */
public class Socio extends Usuario {
    private TipoMembresia tipoMembresia;     // MENSUAL | ANUAL (enum)
    private LocalDate     fechaSuscripcion;  // fecha en que se inscribio
    private boolean       activo;            // baja logica: false = dado de baja (no se borra)
    private ArrayList<RegistroFisico> historialFisico; // todas sus evaluaciones fisicas

    public Socio(String idUsuario, String nombre, int edad, String telefono, TipoMembresia tipoMembresia) {
        // super(...) llama al constructor de Usuario y fija el tipo USUARIO automaticamente
        super(idUsuario, nombre, edad, telefono, TipoUsuario.USUARIO);
        this.tipoMembresia    = tipoMembresia;
        this.fechaSuscripcion = LocalDate.now();   // se registra con la fecha de hoy
        this.activo           = true;              // todo socio nuevo nace activo
        this.historialFisico  = new ArrayList<>(); // historial vacio al inicio
    }

    // ── Getters / setters de los datos del socio ────────────────────────────
    public TipoMembresia getTipoMembresia()                { return tipoMembresia; }
    public void          setTipoMembresia(TipoMembresia t){ this.tipoMembresia = t; }
    public LocalDate     getFechaSuscripcion()            { return fechaSuscripcion; }
    public boolean       isActivo()                       { return activo; }
    public void          setActivo(boolean activo)        { this.activo = activo; }

    // ── Manejo del historial fisico (ArrayList) ─────────────────────────────
    /** Agrega una evaluacion fisica al final del historial del socio. */
    public void                      agregarRegistroFisico(RegistroFisico r) { historialFisico.add(r); }
    public ArrayList<RegistroFisico> getHistorialFisico()                    { return historialFisico; }

    /** Devuelve la evaluacion mas reciente (la ultima de la lista), o null si no tiene ninguna. */
    public RegistroFisico            getUltimoRegistro() {
        if (historialFisico.isEmpty()) return null;
        return historialFisico.get(historialFisico.size() - 1);
    }

    @Override
    public String toString() {
        return getNombre() + " — " + tipoMembresia.name() + (activo ? "" : " [INACTIVO]");
    }
}
