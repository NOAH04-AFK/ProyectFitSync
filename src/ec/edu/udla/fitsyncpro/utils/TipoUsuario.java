package ec.edu.udla.fitsyncpro.utils;

/**
 * Rol del usuario en el sistema. Es la base del control de acceso (RBAC):
 * segun el rol, VentanaPrincipal muestra unas pestañas u otras.
 *  - ADMINISTRADOR: ve todo (modulos 1 a 5 + gestion de equipos).
 *  - ENTRENADOR: modulos 1, 2 y 3.
 *  - USUARIO (socio): solo "Mi Plan".
 */
public enum TipoUsuario {
    ADMINISTRADOR,
    ENTRENADOR,
    USUARIO
}
