package ec.edu.udla.fitsyncpro.utils;

/**
 * Estado operativo de una maquina del gimnasio.
 * Cuando una pasa a DAÑADO, el grafo de dependencias manda a las que dependen
 * de ella a MANTENIMIENTO (propagacion en cascada por BFS).
 */
public enum EstadoEquipo {
    OPERATIVO, DAÑADO, MANTENIMIENTO
}
