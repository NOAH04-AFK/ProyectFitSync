package ec.edu.udla.fitsyncpro.estructuras;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Grafo Dirigido implementado con LISTA DE ADYACENCIA.
 *
 * Modela las dependencias físicas entre las máquinas del gimnasio:
 * una arista  A → B  significa "B depende de A para funcionar".
 *
 * Estructuras internas:
 * ─ HashMap<String, LinkedList<String>> : vértice → lista de adyacentes (O(1) acceso al vértice)
 * ─ LinkedList como Cola FIFO            : soporte del recorrido en anchura (BFS)
 *
 * Complejidad del BFS: O(V + E), donde V = vértices (equipos) y E = aristas (dependencias).
 */
public class GrafoEquipos{

    private HashMap<String, LinkedList<String>> listaAdyacencia;

    public GrafoEquipos() {
        listaAdyacencia = new HashMap<>();
    }

    /** Agrega un vértice (equipo) si aún no existe */
    public void agregarVertice(String idEquipo) {
        if (!listaAdyacencia.containsKey(idEquipo)) {
            listaAdyacencia.put(idEquipo, new LinkedList<>());
        }
    }

    /** Agrega una arista dirigida: idOrigen → idDependiente */
    public void agregarDependencia(String idOrigen, String idDependiente) {
        agregarVertice(idOrigen);
        agregarVertice(idDependiente);
        LinkedList<String> adyacentes = listaAdyacencia.get(idOrigen);
        if (!adyacentes.contains(idDependiente)) {
            adyacentes.add(idDependiente);
        }
    }

    /** Adyacentes directos de un vértice (dependientes de primer nivel) */
    public LinkedList<String> obtenerAdyacentes(String idEquipo) {
        LinkedList<String> ady = listaAdyacencia.get(idEquipo);
        return (ady != null) ? ady : new LinkedList<>();
    }

    /**
     * Recorrido en Anchura (BFS) desde un vértice origen.
     * Devuelve TODOS los vértices alcanzables (dependencias directas e
     * indirectas), sin incluir el origen. Usa una Cola FIFO y un registro
     * de visitados para no procesar dos veces el mismo vértice.
     */
    public ArrayList<String> bfs(String idOrigen) {
        ArrayList<String> alcanzados = new ArrayList<>();
        if (!listaAdyacencia.containsKey(idOrigen)) return alcanzados;

        HashMap<String, Boolean> visitados = new HashMap<>();
        LinkedList<String> cola = new LinkedList<>();   // Cola FIFO del BFS

        visitados.put(idOrigen, true);
        cola.addLast(idOrigen);                          // encolar

        while (!cola.isEmpty()) {
            String actual = cola.removeFirst();          // desencolar
            for (String vecino : obtenerAdyacentes(actual)) {
                if (!visitados.containsKey(vecino)) {
                    visitados.put(vecino, true);
                    alcanzados.add(vecino);
                    cola.addLast(vecino);
                }
            }
        }
        return alcanzados;
    }

    /** Número de vértices del grafo */
    public int getTotalVertices() {
        return listaAdyacencia.size();
    }

    /** Número de aristas del grafo */
    public int getTotalAristas() {
        int total = 0;
        for (LinkedList<String> ady : listaAdyacencia.values()) total += ady.size();
        return total;
    }
}
