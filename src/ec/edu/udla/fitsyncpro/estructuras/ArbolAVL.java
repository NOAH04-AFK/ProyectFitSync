package ec.edu.udla.fitsyncpro.estructuras;

import ec.edu.udla.fitsyncpro.models.Socio;

import java.util.ArrayList;

/**
 * Árbol AVL (Árbol Binario de Búsqueda Auto-Balanceado) – Módulo 4.
 *
 * Mantiene el ranking de constancia de los socios ordenado por puntaje.
 * Tras cada inserción o eliminación se verifica el FACTOR DE EQUILIBRIO
 * (altura subárbol izquierdo − altura subárbol derecho); si sale del rango
 * [-1, 1] se aplican ROTACIONES (simples o dobles) para re-balancear.
 *
 * Gracias al balanceo, insertar, eliminar y buscar operan en O(log n)
 * INCLUSO EN EL PEOR CASO, y el recorrido in-order entrega el ranking
 * completo ya ordenado en O(n).
 *
 * Criterio de orden: puntaje; en caso de empate desempata el ID del socio
 * (así dos socios con el mismo puntaje pueden convivir en el árbol).
 */
public class ArbolAVL {


    private NodoAVL raiz;
    private int     totalNodos;

    // ════════════════════════════════════════════════════════════════════
    //  UTILIDADES DE ALTURA Y BALANCE
    // ════════════════════════════════════════════════════════════════════
    private int altura(NodoAVL nodo) {
        return (nodo == null) ? 0 : nodo.getAltura();
    }

    private int factorEquilibrio(NodoAVL nodo) {
        return (nodo == null) ? 0 : altura(nodo.izquierdo) - altura(nodo.derecho);
    }

    private void actualizarAltura(NodoAVL nodo) {
        nodo.altura = 1 + Math.max(altura(nodo.izquierdo), altura(nodo.derecho));
    }

    /** Compara (puntaje, idSocio) contra un nodo: <0 va a la izquierda, >0 a la derecha */
    private int comparar(int puntaje, String idSocio, NodoAVL nodo) {
        if (puntaje != nodo.puntaje) return Integer.compare(puntaje, nodo.puntaje);
        return idSocio.compareTo(nodo.socio.getIdUsuario());
    }

    // ════════════════════════════════════════════════════════════════════
    //  ROTACIONES
    // ════════════════════════════════════════════════════════════════════

    /** Rotación simple a la DERECHA (caso Izquierda-Izquierda) */
    private NodoAVL rotarDerecha(NodoAVL y) {
        NodoAVL x  = y.izquierdo;
        NodoAVL t2 = x.derecho;

        x.derecho   = y;
        y.izquierdo = t2;

        actualizarAltura(y);
        actualizarAltura(x);
        return x;   // nueva raíz del subárbol
    }

    /** Rotación simple a la IZQUIERDA (caso Derecha-Derecha) */
    private NodoAVL rotarIzquierda(NodoAVL x) {
        NodoAVL y  = x.derecho;
        NodoAVL t2 = y.izquierdo;

        y.izquierdo = x;
        x.derecho   = t2;

        actualizarAltura(x);
        actualizarAltura(y);
        return y;   // nueva raíz del subárbol
    }

    /** Aplica la rotación necesaria según el factor de equilibrio */
    private NodoAVL balancear(NodoAVL nodo) {
        actualizarAltura(nodo);
        int fe = factorEquilibrio(nodo);

        // Caso Izquierda-Izquierda → rotación simple derecha
        if (fe > 1 && factorEquilibrio(nodo.izquierdo) >= 0) {
            return rotarDerecha(nodo);
        }
        // Caso Izquierda-Derecha → rotación doble (izquierda + derecha)
        if (fe > 1) {
            nodo.izquierdo = rotarIzquierda(nodo.izquierdo);
            return rotarDerecha(nodo);
        }
        // Caso Derecha-Derecha → rotación simple izquierda
        if (fe < -1 && factorEquilibrio(nodo.derecho) <= 0) {
            return rotarIzquierda(nodo);
        }
        // Caso Derecha-Izquierda → rotación doble (derecha + izquierda)
        if (fe < -1) {
            nodo.derecho = rotarDerecha(nodo.derecho);
            return rotarIzquierda(nodo);
        }
        return nodo;   // ya está balanceado
    }

    // ════════════════════════════════════════════════════════════════════
    //  INSERCIÓN  – O(log n)
    // ════════════════════════════════════════════════════════════════════
    public void insertar(Socio socio, int puntaje) {
        raiz = insertarRec(raiz, socio, puntaje);
        totalNodos++;
    }

    private NodoAVL insertarRec(NodoAVL nodo, Socio socio, int puntaje) {
        if (nodo == null) return new NodoAVL(socio, puntaje);

        int cmp = comparar(puntaje, socio.getIdUsuario(), nodo);
        if (cmp < 0)      nodo.izquierdo = insertarRec(nodo.izquierdo, socio, puntaje);
        else if (cmp > 0) nodo.derecho   = insertarRec(nodo.derecho,   socio, puntaje);
        else              return nodo;   // duplicado exacto: no se inserta

        return balancear(nodo);
    }

    // ════════════════════════════════════════════════════════════════════
    //  ELIMINACIÓN  – O(log n)
    // ════════════════════════════════════════════════════════════════════
    public void eliminar(Socio socio, int puntaje) {
        raiz = eliminarRec(raiz, socio.getIdUsuario(), puntaje);
    }

    private NodoAVL eliminarRec(NodoAVL nodo, String idSocio, int puntaje) {
        if (nodo == null) return null;

        int cmp = comparar(puntaje, idSocio, nodo);
        if (cmp < 0) {
            nodo.izquierdo = eliminarRec(nodo.izquierdo, idSocio, puntaje);
        } else if (cmp > 0) {
            nodo.derecho = eliminarRec(nodo.derecho, idSocio, puntaje);
        } else {
            // Nodo encontrado
            totalNodos--;
            if (nodo.izquierdo == null || nodo.derecho == null) {
                // 0 o 1 hijo: el hijo (o null) reemplaza al nodo
                nodo = (nodo.izquierdo != null) ? nodo.izquierdo : nodo.derecho;
            } else {
                // 2 hijos: se reemplaza por el SUCESOR in-order (mínimo del subárbol derecho)
                NodoAVL sucesor = minimo(nodo.derecho);
                nodo.socio   = sucesor.socio;
                nodo.puntaje = sucesor.puntaje;
                totalNodos++; // compensa el descuento que hará la llamada recursiva
                nodo.derecho = eliminarRec(nodo.derecho, sucesor.socio.getIdUsuario(), sucesor.puntaje);
            }
        }
        if (nodo == null) return null;
        return balancear(nodo);
    }

    private NodoAVL minimo(NodoAVL nodo) {
        while (nodo.izquierdo != null) nodo = nodo.izquierdo;
        return nodo;
    }

    // ════════════════════════════════════════════════════════════════════
    //  RECORRIDOS  – O(n)
    // ════════════════════════════════════════════════════════════════════

    /** Ranking DESCENDENTE (mayor puntaje primero): in-order inverso (derecha–raíz–izquierda) */
    public ArrayList<NodoAVL> obtenerRankingDescendente() {
        ArrayList<NodoAVL> ranking = new ArrayList<>();
        if (estaVacio()) return ranking;   // árbol sin nodos → ranking vacío
        inOrdenInverso(raiz, ranking);
        return ranking;
    }

    private void inOrdenInverso(NodoAVL nodo, ArrayList<NodoAVL> lista) {
        if (nodo == null) return;
        inOrdenInverso(nodo.derecho, lista);
        lista.add(nodo);
        inOrdenInverso(nodo.izquierdo, lista);
    }

    // ════════════════════════════════════════════════════════════════════
    //  CONSULTAS
    // ════════════════════════════════════════════════════════════════════
    public boolean estaVacio()    { return raiz == null; }
    public int     getTotalNodos(){ return totalNodos; }
    public int     getAlturaArbol(){ return altura(raiz); }
}
