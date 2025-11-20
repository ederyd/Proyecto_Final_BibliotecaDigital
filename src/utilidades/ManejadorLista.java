package utilidades;

import interfaces.ArreglosInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Clase ManejadorLista<T>: Implementa la interfaz genérica.
 * Cumple con el requisito de Genéricos, Interfaces y el método revertir().
 */
public class ManejadorLista<T> implements ArreglosInterface<T> {
    
    private List<T> lista; // Utilizamos ArrayList internamente para la flexibilidad

    public ManejadorLista() {
        this.lista = new ArrayList<>();
    }

    @Override
    public void agregar(T elemento) {
        lista.add(elemento);
    }

    @Override
    public T obtener(int indice) {
        if (indice >= 0 && indice < lista.size()) {
            return lista.get(indice);
        }
        return null; // Manejo simple de índice fuera de rango
    }

    @Override
    public int buscar(String identificador) {
        // En este contexto, el identificador podría ser el email (Usuario) o el título (Libro)
        for (int i = 0; i < lista.size(); i++) {
            // Se asume que T tiene un método toString que incluye el identificador
            if (lista.get(i).toString().contains(identificador)) {
                return i; // Retorna el índice de la primera coincidencia
            }
        }
        return -1; // No encontrado
    }

    @Override
    public int totalElementos() {
        return lista.size();
    }

    @Override
    public void revertir() {
        Collections.reverse(this.lista);
    }
    
    // Método auxiliar para listar todos los elementos (no requerido por la interfaz, pero útil)
    public List<T> getLista() {
        return this.lista;
    }
}