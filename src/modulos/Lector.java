package modulos;

public class Lector extends Usuario {
    
    public Lector(String nombre, String email, String password) throws IllegalArgumentException {
        super(nombre, email, password, "Lector");
    }

    // Menú actualizado (Polimorfismo)
    @Override
    public void mostrarMenu() {
        System.out.println("\n===== Menú de Lector =====");
        System.out.println("1. Ver Catálogo de Libros (Disponibilidad)");
        System.out.println("2. Buscar Libro por Título");
        System.out.println("3. Procesar Préstamo de Libro");      // <--- NUEVO
        System.out.println("4. Procesar Devolución de Libro");   // <--- NUEVO
        System.out.println("5. Ver mis préstamos actuales");
        System.out.println("6. Salir y Cerrar Sesión");          // <--- La opción de salida es ahora 6
    }
}