package interfaces;

/**
 * Interfaz Genérica <T> que define las operaciones de manejo de una lista.
 * Requisito de la Práctica 5.
 */
public interface ArreglosInterface<T> {
    
    /** Agrega un elemento a la lista. */
    void agregar(T elemento);
    
    /** Obtiene un elemento por su índice. */
    T obtener(int indice);
    
    /** Busca un elemento basado en una cadena de texto (ej. título, email). */
    int buscar(String identificador); 
    
    /** Retorna el número total de elementos. */
    int totalElementos();
    
    /** Revierte el orden de los elementos de la lista. Requisito de la Práctica 5. */
    void revertir();
}