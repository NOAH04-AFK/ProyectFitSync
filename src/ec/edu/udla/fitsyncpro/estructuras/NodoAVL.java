package ec.edu.udla.fitsyncpro.estructuras;

import ec.edu.udla.fitsyncpro.models.Socio;

/**
 * Nodo del Árbol AVL del ranking de constancia (Módulo 4).
 * Cada nodo guarda al socio y su puntaje de constancia (clave de ordenamiento).
 */
public class NodoAVL {

    Socio   socio;
    int     puntaje;
    int     altura;
    NodoAVL izquierdo;
    NodoAVL derecho;

    public NodoAVL(Socio socio, int puntaje) {
        this.socio   = socio;
        this.puntaje = puntaje;
        this.altura  = 1;        // un nodo hoja tiene altura 1
    }

    public Socio getSocio()   { return socio; }
    public int   getPuntaje() { return puntaje; }
    public int   getAltura()  { return altura; }
}
