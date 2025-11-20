
import modulos.*;
import utilidades.*;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Main {
    
    // ... (atributos estáticos permanecen iguales) ...
    private static ManejadorLista<Usuario> usuarios = new ManejadorLista<>();
    private static ManejadorLista<Libro> libros = new ManejadorLista<>();
    private static Scanner scanner = new Scanner(System.in);
    private static RecordatorioDevolucion recordatorio;

    public static void main(String[] args) {
        // 1. Cargar datos iniciales
        cargarDatos();
        
        // 2. Iniciar el Hilo de Recordatorios
        recordatorio = new RecordatorioDevolucion(libros);
        Thread hiloRecordatorio = new Thread(recordatorio);
        hiloRecordatorio.setDaemon(true); 
        hiloRecordatorio.start();

        // 3. Loop de autenticación (Login)
        Usuario usuarioActual = null;
        while (usuarioActual == null) {
            System.out.println("\n=== Sistema de Biblioteca Digital - LOGIN ===");
            System.out.print("Ingrese su Email (o 'salir'): ");
            String email = scanner.nextLine().trim();

            if (email.equalsIgnoreCase("salir")) {
                recordatorio.detener();
                break;
            }
            
            // NUEVO: Pedir contraseña
            System.out.print("Ingrese su Contraseña: ");
            String password = scanner.nextLine().trim(); 

            // Autenticación con email y contraseña
            usuarioActual = autenticar(email, password);

            if (usuarioActual != null) {
                System.out.println("\n✅ Bienvenido(a), " + usuarioActual.getNombre() + " (" + usuarioActual.getRol() + ")!");
                ejecutarMenu(usuarioActual);
                usuarioActual = null; // Volver al login después de salir del menú
            } else {
                System.out.println("❌ Credenciales incorrectas. Intente nuevamente.");
            }
        }

        // 4. Cierre del sistema
        scanner.close();
        System.out.println("\nSistema de Biblioteca cerrado.");
    }
    
    // --------------------------------------------------------------------------
    // Métodos de Inicialización y Autenticación
    // --------------------------------------------------------------------------

    private static void cargarDatos() {
        ManejadorLista<Usuario> bibliotecariosCargados = ManejadorArchivos.cargarUsuarios("Bibliotecarios.txt");
        
        // Unir todos los usuarios en la lista principal 'usuarios'
        usuarios.getLista().addAll(bibliotecariosCargados.getLista());
        usuarios.getLista().addAll(ManejadorArchivos.cargarUsuarios("Lectores.txt").getLista());
        libros = ManejadorArchivos.cargarLibros();
        
        // MEJORA 1: Crear administrador por defecto si no se cargó ninguno
        if (bibliotecariosCargados.totalElementos() == 0) {
            try {
                Bibliotecario defaultAdmin = new Bibliotecario("Admin General", "admin@biblioteca.com", "1234");
                usuarios.agregar(defaultAdmin);
                
                // Usamos una lista temporal para guardar SÓLO el nuevo admin en Bibliotecarios.txt
                ManejadorLista<Usuario> tempBiblios = new ManejadorLista<>();
                tempBiblios.agregar(defaultAdmin);
                
                // **Usamos actualizarArchivo() para sobrescribir/crear el archivo correctamente.**
                ManejadorArchivos.actualizarArchivo(tempBiblios, "Bibliotecarios.txt");
                
                System.out.println("Se creó un Bibliotecario por defecto: admin@biblioteca.com / Contraseña: 1234");
            } catch (IllegalArgumentException e) {
                // No debería ocurrir
            }
        }
    }

    // Método de autenticación actualizado para recibir y verificar la contraseña
    private static Usuario autenticar(String email, String password) {
        for (int i = 0; i < usuarios.totalElementos(); i++) {
            Usuario user = usuarios.obtener(i);
            // Uso del método checkCredentials implementado en Usuario
            if (user.checkCredentials(email, password)) { 
                return user;
            }
        }
        return null;
    }
    
    // ... (ejecutarMenu, manejarMenuBibliotecario y manejarMenuLector permanecen iguales) ...
    private static void ejecutarMenu(Usuario usuario) {
        int opcion;
        do {
            usuario.mostrarMenu(); // Polimorfismo: llama al menú específico
            System.out.print("Seleccione una opción: ");
            
            try {
                opcion = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Opción no válida. Ingrese un número.");
                opcion = -1;
                continue;
            }

            if (usuario instanceof Bibliotecario) {
                manejarMenuBibliotecario(opcion);
            } else if (usuario instanceof Lector) {
                manejarMenuLector(opcion);
            }
            
        } while (opcion != 9 && !(usuario instanceof Lector && opcion == 6));
    }
    
    // --------------------------------------------------------------------------
    // Lógica de Menús
    // --------------------------------------------------------------------------

    private static void manejarMenuBibliotecario(int opcion) {
        switch (opcion) {
            case 1: registrarNuevoLector(); break;
            case 2: agregarNuevoLibro(); break;
            case 3: eliminarLibro(); break;
            case 4: listarUsuarios(); break;
            case 5: listarLibros(); break;
            case 6: procesarPrestamo(); break;
            case 7: procesarDevolucion(); break;
            case 8: revertirListaLibros(); break;
            case 9: System.out.println("Cerrando sesión de Bibliotecario..."); break;
            default: System.out.println("Opción inválida.");
        }
    }
    
    private static void manejarMenuLector(int opcion) {
        switch (opcion) {
            case 1: listarLibros(); break;
            case 2: buscarLibroPorTitulo(); break;
            case 3: procesarPrestamo(); break;   // <--- NUEVA FUNCIÓN
            case 4: procesarDevolucion(); break; // <--- NUEVA FUNCIÓN
            case 5: 
                System.out.println("Función de visualización de mis préstamos no implementada. Vuelve pronto!"); break; 
            case 6: System.out.println("Cerrando sesión de Lector..."); break;
            default: System.out.println("Opción inválida.");
        }
    }
    
    // --------------------------------------------------------------------------
    // Funciones del Sistema (Implementación de Prácticas)
    // --------------------------------------------------------------------------
    
    private static void registrarNuevoLector() {
        System.out.print("Nombre del nuevo Lector: ");
        String nombre = scanner.nextLine();
        System.out.print("Email del nuevo Lector: ");
        String email = scanner.nextLine();
        // NUEVO: Pedir y validar contraseña
        System.out.print("Contraseña para el Lector (min 4 caracteres): ");
        String password = scanner.nextLine();
        
        try {
            Lector nuevoLector = new Lector(nombre, email, password);
            usuarios.agregar(nuevoLector);
            ManejadorArchivos.escribirLineaAlFinal(nuevoLector.toString(), "Lectores.txt");
            System.out.println("✅ Lector '" + nombre + "' registrado con éxito.");
        } catch (IllegalArgumentException e) {
            System.out.println("❌ ERROR al registrar Lector: " + e.getMessage()); // Excepciones
        }
    }
    
    // ... (resto de métodos como agregarNuevoLibro, listarLibros, etc., permanecen iguales) ...
    private static void agregarNuevoLibro() {
        System.out.print("Título del Libro: ");
        String titulo = scanner.nextLine();
        System.out.print("Autor: ");
        String autor = scanner.nextLine();
        System.out.print("Género: ");
        String genero = scanner.nextLine();
        
        Libro nuevoLibro = new Libro(titulo, autor, genero);
        libros.agregar(nuevoLibro);
        ManejadorArchivos.escribirLineaAlFinal(nuevoLibro.toString(), "Libros.txt");
        System.out.println("✅ Libro '" + titulo + "' agregado al catálogo.");
    }
    
    private static void listarLibros() {
        System.out.println("\n==== CATÁLOGO DE LIBROS ====");
        if (libros.totalElementos() == 0) {
            System.out.println("(Catálogo vacío)");
            return;
        }
        for (int i = 0; i < libros.totalElementos(); i++) {
            Libro libro = libros.obtener(i);
            String info = String.format("%d. %s, por %s. [Estado: %s]", 
                                        i + 1, libro.getTitulo(), libro.getAutor(), libro.getEstado());
            if (libro.getEstado().equals("Prestado")) {
                info += String.format(" (Vence: %s)", libro.getFechaDevolucion());
            }
            System.out.println(info);
        }
    }
    
    private static void procesarPrestamo() {
        listarLibros();
        System.out.print("Título del libro a prestar: ");
        String tituloBuscado = scanner.nextLine();
        
        int indice = libros.buscar(tituloBuscado);
        if (indice != -1) {
            Libro libro = libros.obtener(indice);
            if (libro.getEstado().equals("Disponible")) {
                System.out.print("Ingrese fecha límite de devolución (YYYY-MM-DD): ");
                String fechaStr = scanner.nextLine();
                try {
                    // Validar formato de fecha
                    LocalDate.parse(fechaStr); 
                    libro.prestar(fechaStr);
                    ManejadorArchivos.actualizarArchivo(libros, "Libros.txt");
                    System.out.println("✅ Libro '" + libro.getTitulo() + "' prestado hasta " + fechaStr + ".");
                } catch (DateTimeParseException e) {
                    System.out.println("❌ ERROR: Formato de fecha incorrecto. Use YYYY-MM-DD.");
                }
            } else {
                System.out.println("❌ El libro ya está prestado (Vence: " + libro.getFechaDevolucion() + ").");
            }
        } else {
            System.out.println("❌ Libro no encontrado.");
        }
    }
    
    private static void procesarDevolucion() {
        System.out.print("Título del libro a devolver: ");
        String tituloBuscado = scanner.nextLine();
        
        int indice = libros.buscar(tituloBuscado);
        if (indice != -1) {
            Libro libro = libros.obtener(indice);
            if (libro.getEstado().equals("Prestado")) {
                libro.devolver();
                ManejadorArchivos.actualizarArchivo(libros, "Libros.txt");
                System.out.println("✅ Libro '" + libro.getTitulo() + "' devuelto con éxito.");
            } else {
                System.out.println("❌ El libro ya estaba disponible.");
            }
        } else {
            System.out.println("❌ Libro no encontrado.");
        }
    }
    
    private static void eliminarLibro() {
        System.out.print("Título del libro a eliminar: ");
        String tituloBuscado = scanner.nextLine();
        
        int indice = libros.buscar(tituloBuscado);
        if (indice != -1) {
            Libro libro = libros.obtener(indice);
            if (libro.getEstado().equals("Prestado")) {
                 System.out.println("❌ No se puede eliminar un libro que está prestado.");
            } else {
                // La clase ManejadorLista (que usa ArrayList) soporta remover por índice
                libros.getLista().remove(indice); 
                ManejadorArchivos.actualizarArchivo(libros, "Libros.txt");
                System.out.println("✅ Libro '" + libro.getTitulo() + "' eliminado del catálogo.");
            }
        } else {
            System.out.println("❌ Libro no encontrado.");
        }
    }
    
    private static void listarUsuarios() {
        System.out.println("\n==== LISTA DE USUARIOS ====");
        if (usuarios.totalElementos() == 0) {
            System.out.println("(No hay usuarios registrados)");
            return;
        }
        for (int i = 0; i < usuarios.totalElementos(); i++) {
            Usuario user = usuarios.obtener(i);
            System.out.printf("%d. Nombre: %s, Email: %s, Rol: %s\n", 
                              i + 1, user.getNombre(), user.getEmail(), user.getRol());
        }
    }
    
    private static void revertirListaLibros() {
        libros.revertir();
        ManejadorArchivos.actualizarArchivo(libros, "Libros.txt");
        System.out.println("✅ Lista de libros revertida y guardada.");
        listarLibros();
    }
    
    private static void buscarLibroPorTitulo() {
        System.out.print("Ingrese el título a buscar: ");
        String tituloBuscado = scanner.nextLine();
        
        int indice = libros.buscar(tituloBuscado);
        if (indice != -1) {
            Libro libro = libros.obtener(indice);
            System.out.printf("✅ Libro encontrado: '%s', Autor: %s, Estado: %s\n", 
                              libro.getTitulo(), libro.getAutor(), libro.getEstado());
        } else {
            System.out.println("❌ Libro no encontrado con ese título.");
        }
    }
}