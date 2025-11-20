package utilidades;

import modulos.*;
import java.io.*;
import java.util.Scanner;

public class ManejadorArchivos {
    
    // RUTA MODIFICADA: La base de datos ahora se busca dentro del directorio 'src'
    private static final String DIR_BD = "src/BD/"; 

    // ----------------------------------------------------
    // Métodos genéricos de escritura
    // ----------------------------------------------------
    
    /** Añade una nueva línea al final del archivo */
    public static void escribirLineaAlFinal(String linea, String nombreArchivo) {
        // Se utiliza la nueva ruta: "src/BD/" + nombreArchivo
        try (PrintWriter pw = new PrintWriter(new FileWriter(DIR_BD + nombreArchivo, true))) {
            pw.println(linea); 
        } catch (IOException e) {
            System.err.println("❌ Error de I/O al escribir en el archivo " + nombreArchivo + ": " + e.getMessage());
        }
    }
    
    /** Sobrescribe el archivo completo con el contenido de la lista */
    public static <T> void actualizarArchivo(ManejadorLista<T> lista, String nombreArchivo) {
        // Se utiliza la nueva ruta: "src/BD/" + nombreArchivo
        try (PrintWriter pw = new PrintWriter(new FileWriter(DIR_BD + nombreArchivo))) {
            for (T elemento : lista.getLista()) {
                pw.println(elemento.toString());
            }
        } catch (IOException e) {
            System.err.println("❌ Error de I/O al actualizar el archivo " + nombreArchivo + ": " + e.getMessage());
        }
    }

    // ----------------------------------------------------
    // Métodos de carga al inicio del sistema
    // ----------------------------------------------------
    
    /** Carga todos los usuarios de tipo Lector/Bibliotecario */
    public static ManejadorLista<Usuario> cargarUsuarios(String nombreArchivo) {
        ManejadorLista<Usuario> lista = new ManejadorLista<>();
        // Se utiliza la nueva ruta
        try (Scanner sc = new Scanner(new File(DIR_BD + nombreArchivo))) { 
            while (sc.hasNextLine()) {
                String linea = sc.nextLine();
                String[] partes = linea.split(",");
                if (partes.length == 4) { 
                    String nombre = partes[0].trim();
                    String email = partes[1].trim();
                    String password = partes[2].trim(); 
                    String rol = partes[3].trim();
                    
                    try {
                        if (rol.equalsIgnoreCase("Lector")) {
                            lista.agregar(new Lector(nombre, email, password));
                        } else if (rol.equalsIgnoreCase("Bibliotecario")) {
                            lista.agregar(new Bibliotecario(nombre, email, password));
                        }
                    } catch (IllegalArgumentException e) {
                        System.err.println("⚠️ Dato de usuario inválido en archivo " + nombreArchivo + ": " + e.getMessage() + " (Línea: " + linea + ")");
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("❌ Archivo de usuarios no encontrado: " + DIR_BD + nombreArchivo + ". Se creará uno al guardar.");
        }
        return lista;
    }
    
    /** Carga todos los libros */
    public static ManejadorLista<Libro> cargarLibros() {
        ManejadorLista<Libro> lista = new ManejadorLista<>();
        // Se utiliza la nueva ruta
        try (Scanner sc = new Scanner(new File(DIR_BD + "Libros.txt"))) {
            while (sc.hasNextLine()) {
                String linea = sc.nextLine();
                String[] partes = linea.split(",");
                if (partes.length == 5) {
                    String titulo = partes[0].trim();
                    String autor = partes[1].trim();
                    String genero = partes[2].trim();
                    String estado = partes[3].trim();
                    String fecha = partes[4].trim();
                    
                    lista.agregar(new Libro(titulo, autor, genero, estado, fecha));
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("❌ Archivo de libros no encontrado: " + DIR_BD + "Libros.txt" + ". Se creará uno al guardar.");
        }
        return lista;
    }
}