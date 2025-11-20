package modulos;

/**
 * Clase Libro: Representa el material de la biblioteca.
 */
public class Libro {
    private String titulo;
    private String autor;
    private String genero;
    private String estado;          // "Disponible", "Prestado"
    private String fechaDevolucion; // Formato YYYY-MM-DD

    // Constructor para nuevos libros
    public Libro(String titulo, String autor, String genero) {
        this(titulo, autor, genero, "Disponible", "N/A");
    }
    
    // Constructor completo para cargar desde archivo
    public Libro(String titulo, String autor, String genero, String estado, String fechaDevolucion) {
        this.titulo = titulo;
        this.autor = autor;
        this.genero = genero;
        this.estado = estado;
        this.fechaDevolucion = fechaDevolucion;
    }

    // Métodos de gestión
    public void prestar(String fecha) {
        this.estado = "Prestado";
        this.fechaDevolucion = fecha;
    }

    public void devolver() {
        this.estado = "Disponible";
        this.fechaDevolucion = "N/A";
    }

    // Getters
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public String getEstado() { return estado; }
    public String getFechaDevolucion() { return fechaDevolucion; }

    // Para Persistencia
    @Override
    public String toString() {
        return titulo + "," + autor + "," + genero + "," + estado + "," + fechaDevolucion;
    }
}