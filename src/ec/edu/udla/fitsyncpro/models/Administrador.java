package ec.edu.udla.fitsyncpro.models;

import ec.edu.udla.fitsyncpro.utils.TipoUsuario;

public class Administrador  extends Usuario{
    private String  usuarioLogin;
    private String  clave;
    private String  nivelAcceso;   // TOTAL | CONSULTA
    private boolean activo;

    public Administrador(String idUsuario, String nombre, int edad, String telefono, String usuarioLogin, String clave, String nivelAcceso) {
        super(idUsuario, nombre, edad, telefono, TipoUsuario.ADMINISTRADOR);
        this.usuarioLogin = usuarioLogin;
        this.clave        = clave;
        this.nivelAcceso  = nivelAcceso;
        this.activo       = true;
    }

    public boolean validarCredenciales(String usuario, String clave) {
        return activo && this.usuarioLogin.equals(usuario) && this.clave.equals(clave);
    }

    public String  getUsuarioLogin()             { return usuarioLogin; }
    public String  getNivelAcceso()              { return nivelAcceso; }
    public void    setClave(String clave)        { this.clave = clave; }
    public void    setNivelAcceso(String nivel)  { this.nivelAcceso = nivel; }
    public boolean isActivo()                    { return activo; }
    public void    setActivo(boolean activo)     { this.activo = activo; }

    @Override
    public String toString() {
        return getNombre() + " [" + usuarioLogin + "] — Acceso: " + nivelAcceso
                + (activo ? "" : " [INACTIVO]");
    }





}
