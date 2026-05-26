package ec.edu.udla.fitsyncpro.models;

import ec.edu.udla.fitsyncpro.utils.TipoUsuario;

public  abstract class Usuario {
    private String idUsuario;
    private String nombre;
    private int    edad;
    private String telefono;
    private TipoUsuario tipoUsuario;

    public Usuario(String idUsuario, String nombre, int edad, String telefono, TipoUsuario tipoUsuario) {
        this.idUsuario   = idUsuario;
        this.nombre      = nombre;
        this.edad        = edad;
        this.telefono    = telefono;
        this.tipoUsuario = tipoUsuario;
    }

    public String      getIdUsuario()   { return idUsuario; }
    public String      getNombre()      { return nombre; }
    public void        setNombre(String nombre) { this.nombre = nombre; }
    public int         getEdad()        { return edad; }
    public void        setEdad(int edad){ this.edad = edad; }
    public String      getTelefono()    { return telefono; }
    public void        setTelefono(String t){ this.telefono = t; }
    public TipoUsuario getTipoUsuario() { return tipoUsuario; }

    @Override
    public String toString() { return nombre + " (ID: " + idUsuario + ")"; }
}

