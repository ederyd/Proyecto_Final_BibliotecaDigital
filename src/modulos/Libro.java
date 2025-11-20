package modulos;

public class Libro {
    private String titulo;
    private String autor;
    private String genero;
    private String estado;          
    private String fechaDevolucion; 
    private String emailPrestatario; // <-- NUEVO: Guarda el email del Lector
    
    // Constructor para nuevos libros
    public Libro(String titulo, String autor, String genero) {
        // Valor por defecto para el nuevo campo
        this(titulo, autor, genero, "Disponible", "N/A", "N/A"); 
    }
    
    // Constructor completo para cargar desde archivo (6 campos)
    public Libro(String titulo, String autor, String genero, String estado, String fechaDevolucion, String emailPrestatario) {
        this.titulo = titulo;
        this.autor = autor;
        this.genero = genero;
        this.estado = estado;
        this.fechaDevolucion = fechaDevolucion;
        this.emailPrestatario = emailPrestatario; // Asignación
    }

    // Métodos de gestión
    public void prestar(String fecha, String emailPrestatario) { // <-- Método actualizado
        this.estado = "Prestado";
        this.fechaDevolucion = fecha;
        this.emailPrestatario = emailPrestatario;
    }

    public void devolver() {
        this.estado = "Disponible";
        this.fechaDevolucion = "N/A";
        this.emailPrestatario = "N/A"; // Limpiar al devolver
    }

    // Getters
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public String getEstado() { return estado; }
    public String getFechaDevolucion() { return fechaDevolucion; }
    public String getEmailPrestatario() { return emailPrestatario; } // <-- NUEVO Getter

    // Para Persistencia (6 campos separados por coma)
    @Override
    public String toString() {
        return titulo + "," + autor + "," + genero + "," + estado + "," + fechaDevolucion + "," + emailPrestatario; 
    }
}