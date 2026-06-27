package ec.edu.udla.fitsyncpro.models;

import ec.edu.udla.fitsyncpro.utils.TipoUsuario;

/**
 * Clase BASE ABSTRACTA de la jerarquia de usuarios.
 *
 * De ella heredan Socio, Entrenador y Administrador (relacion de HERENCIA).
 * Guarda los datos comunes a cualquier persona del sistema. Es "abstract":
 * NO se puede crear un Usuario directamente (new Usuario(...) daria error);
 * siempre se crea una de sus subclases. Gracias a esto una variable de tipo
 * Usuario puede apuntar a un Socio, un Entrenador, etc. (polimorfismo).
 */
public abstract class Usuario {
    // ── Atributos comunes a todos los usuarios ──────────────────────────────
    private String      idUsuario;   // identificador unico (S001, T001, A001...)
    private String      nombre;
    private int         edad;
    private String      telefono;
    private TipoUsuario tipoUsuario; // ADMINISTRADOR | ENTRENADOR | USUARIO (enum)

    /**
     * Constructor comun. Lo llaman las subclases con super(...) para no repetir
     * la inicializacion de estos campos en cada hija.
     */
    public Usuario(String idUsuario, String nombre, int edad, String telefono, TipoUsuario tipoUsuario) {
        this.idUsuario   = idUsuario;
        this.nombre      = nombre;
        this.edad        = edad;
        this.telefono    = telefono;
        this.tipoUsuario = tipoUsuario;
    }

    // ── Getters / setters: dan acceso controlado a los atributos privados ────
    public String      getIdUsuario()   { return idUsuario; }
    public String      getNombre()      { return nombre; }
    public void        setNombre(String nombre) { this.nombre = nombre; }
    public int         getEdad()        { return edad; }
    public void        setEdad(int edad){ this.edad = edad; }
    public String      getTelefono()    { return telefono; }
    public void        setTelefono(String t){ this.telefono = t; }
    public TipoUsuario getTipoUsuario() { return tipoUsuario; }

    /** Texto legible del usuario (lo usan las listas y combos de la interfaz). */
    @Override
    public String toString() { return nombre + " (ID: " + idUsuario + ")"; }
}
