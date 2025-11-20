package modulos;

public class Bibliotecario extends Usuario {
    
    // Constructor recibe la contraseña y la pasa al padre
    public Bibliotecario(String nombre, String email, String password) throws IllegalArgumentException {
        super(nombre, email, password, "Bibliotecario"); // Invoca constructor padre
    }

    // ... (El resto de la clase, incluyendo mostrarMenu(), no cambia) ...
    @Override
    public void mostrarMenu() {
        System.out.println("\n=== Menú de Bibliotecario ===");
        System.out.println("1. Registrar nuevo Lector");
        System.out.println("2. Agregar nuevo Libro al Catálogo");
        System.out.println("3. Eliminar Libro del Catálogo");
        System.out.println("4. Listar todos los Usuarios");
        System.out.println("5. Listar Catálogo de Libros (Disponibilidad)");
        System.out.println("6. Procesar Préstamo de Libro");
        System.out.println("7. Procesar Devolución de Libro");
        System.out.println("8. Revertir Lista de Libros (Práctica 5)");
        System.out.println("9. Salir y Cerrar Sesión");
    }
}