package ec.edu.udla.fitsyncpro.models;

import ec.edu.udla.fitsyncpro.utils.TipoUsuario;

/**
 * ADMINISTRADOR: hereda de Usuario. Además de los datos comunes, guarda las
 * CREDENCIALES y el NIVEL DE ACCESO que usa el control de acceso (RBAC) del
 * Módulo 5. Solo el administrador entra con usuario y clave.
 */
public class Administrador extends Usuario {
    private String  usuarioLogin;  // nombre de usuario para iniciar sesión
    private String  clave;         // contraseña
    private String  nivelAcceso;   // TOTAL | CONSULTA (qué tanto puede hacer)
    private boolean activo;        // baja lógica

    public Administrador(String idUsuario, String nombre, int edad, String telefono, String usuarioLogin, String clave, String nivelAcceso) {
        super(idUsuario, nombre, edad, telefono, TipoUsuario.ADMINISTRADOR);
        this.usuarioLogin = usuarioLogin;
        this.clave        = clave;
        this.nivelAcceso  = nivelAcceso;
        this.activo       = true;
    }

    /**
     * Valida el login: la cuenta debe estar activa y el usuario y la clave
     * deben coincidir. Lo usa GestorReportes.validarAcceso() en el inicio de sesión.
     */
    public boolean validarCredenciales(String usuario, String clave) {
        return activo && this.usuarioLogin.equals(usuario) && this.clave.equals(clave);
    }

    // ── Getters / setters ───────────────────────────────────────────────────
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
