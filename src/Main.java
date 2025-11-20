
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

    private static Usuario usuarioActual = null;

    public static void main(String[] args) {
        // 1. Cargar datos iniciales
        cargarDatos();
        
        // 2. Iniciar el Hilo de Recordatorios
        recordatorio = new RecordatorioDevolucion(libros);
        Thread hiloRecordatorio = new Thread(recordatorio);
        hiloRecordatorio.setDaemon(true); 
        hiloRecordatorio.start();

        // 3. Loop de autenticación (Login)
        
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
    private static void ejecutarMenu(Usuario usuarioActual) {
        int opcion;
        do {
            usuarioActual.mostrarMenu(); // Polimorfismo: llama al menú específico
            System.out.print("Seleccione una opción: ");
            
            try {
                opcion = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Opción no válida. Ingrese un número.");
                opcion = -1;
                continue;
            }

            if (usuarioActual instanceof Bibliotecario) {
                manejarMenuBibliotecario(opcion);
            } else if (usuarioActual instanceof Lector) {
                manejarMenuLector(opcion);
            }
            
        } while (opcion != 9 && !(usuarioActual instanceof Lector && opcion == 6));
    }
    // NUEVO MÉTODO: Solo busca la existencia del usuario por email
    private static Usuario buscarUsuarioPorEmail(String email) {
    for (int i = 0; i < usuarios.totalElementos(); i++) {
        Usuario user = usuarios.obtener(i);
        if (user.getEmail().equalsIgnoreCase(email)) {
            return user; // Retorna el usuario si el email coincide, ignorando la contraseña
        }
    }
    return null; // No encontrado
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
            case 5: listarLibros(usuarioActual.getRol()); break;
            case 6: procesarPrestamo(); break;
            case 7: procesarDevolucion(); break;
            case 8: revertirListaLibros(); break;
            case 9: System.out.println("Cerrando sesión de Bibliotecario..."); break;
            default: System.out.println("Opción inválida.");
        }
    }
    
    private static void manejarMenuLector(int opcion) {
        switch (opcion) {
            case 1: listarLibros(usuarioActual.getRol()); break;
            case 2: buscarLibroPorTitulo(); break;
            case 3: procesarPrestamo(); break;   // <--- NUEVA FUNCIÓN
            case 4: procesarDevolucion(); break; // <--- NUEVA FUNCIÓN
            case 5: verMisPrestamos(); break; 
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
    
    private static void listarLibros(String rol) {
        System.out.println("\n==== CATÁLOGO DE LIBROS ====");
        if (libros.totalElementos() == 0) {
            System.out.println("(Catálogo vacío)");
            return;
        }
        boolean esBibliotecario = rol.equalsIgnoreCase("Bibliotecario");

        for (int i = 0; i < libros.totalElementos(); i++) {
            Libro libro = libros.obtener(i);
            String info = String.format("%d. %s, por %s. [Estado: %s]", 
                                        i + 1, libro.getTitulo(), libro.getAutor(), libro.getEstado());
            if (libro.getEstado().equals("Prestado")) {
                if (esBibliotecario){
                info += String.format(" (Vence: %s, Prestatario: %s)", libro.getFechaDevolucion(),libro.getEmailPrestatario());
            } else {
                info += String.format(" (Vence: %s)", 
                                          libro.getFechaDevolucion());
            }
        }
            System.out.println(info);
        }
    }
    
    private static void procesarPrestamo() {
        listarLibros(usuarioActual.getRol());
        System.out.print("Título del libro a prestar: ");
        String tituloBuscado = scanner.nextLine();

        String emailPrestatario;

        if (usuarioActual instanceof Lector) {
            // El Lector presta solo a sí mismo
            emailPrestatario = usuarioActual.getEmail();
            System.out.println("Préstamo registrado a su cuenta: " + emailPrestatario);
        } else {
            // El Bibliotecario debe indicar a quién se presta
            System.out.print("Ingrese Email del Lector que tomará prestado el libro: ");
            emailPrestatario = scanner.nextLine();
            // Una verificación simple para evitar errores
            if (buscarUsuarioPorEmail(emailPrestatario) == null) { 
             System.out.println("❌ Error: El email del Lector no fue encontrado en el sistema.");
             return;
        }
        }
        
        int indice = libros.buscar(tituloBuscado);
        if (indice != -1) {
            Libro libro = libros.obtener(indice);
            if (libro.getEstado().equals("Disponible")) {
                System.out.print("Ingrese fecha límite de devolución (YYYY-MM-DD): ");
                String fechaStr = scanner.nextLine();
                try {
                    // Validar formato de fecha
                    LocalDate.parse(fechaStr); 
                    libro.prestar(fechaStr, emailPrestatario);
                    ManejadorArchivos.actualizarArchivo(libros, "Libros.txt");
                    System.out.println("✅ Libro '" + libro.getTitulo() + "' prestado hasta " + fechaStr + " a " + emailPrestatario + ".");
                } catch (DateTimeParseException e) {
                    System.out.println("❌ ERROR: Formato de fecha incorrecto, o la fecha ya paso. Use YYYY-MM-DD.");
                }
            } else {
                System.out.println("❌ El libro ya está prestado (Prestatario: " + libro.getEmailPrestatario() + ", Vence: " + libro.getFechaDevolucion() + ").");
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
            if (usuarioActual instanceof Lector && !libro.getEmailPrestatario().equalsIgnoreCase(usuarioActual.getEmail())) {
                 System.out.println("❌ Error: Solo puedes devolver libros prestados a tu cuenta.");
                 return;
            }
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
        listarLibros(usuarioActual.getRol());
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

    private static void verMisPrestamos() {
        System.out.println("\n==== MIS PRÉSTAMOS ACTUALES ====");
        String emailLector = usuarioActual.getEmail();
        int contador = 0;
    
        for (int i = 0; i < libros.totalElementos(); i++) {
            Libro libro = libros.obtener(i);
        
            // PRIMERA CORRECCIÓN: Saltamos si el objeto Libro es nulo (por si hay un hueco en la lista)
            if (libro == null) {
              continue; 
            }
        
            // El préstamo solo se verifica si el estado es "Prestado"
            if (libro.getEstado().equals("Prestado")) { 
                String prestatario = libro.getEmailPrestatario();
                
                // SEGUNDA CORRECCIÓN: Verificamos que el email del prestatario NO sea nulo
                // antes de llamar a equalsIgnoreCase, lo que evita la NullPointerException.
                if (prestatario != null && prestatario.equalsIgnoreCase(emailLector)) {
                    System.out.printf("- '%s', por %s. (Devolución límite: %s)\n", 
                                    libro.getTitulo(), libro.getAutor(), libro.getFechaDevolucion());
                    contador++;
                }
            }
        }
    
        if (contador == 0) {
            System.out.println("No tienes libros prestados actualmente.");
        }
    }
}