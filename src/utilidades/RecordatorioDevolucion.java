package utilidades;

import modulos.Libro;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

/**
 * Clase RecordatorioDevolucion: Implementa Runnable para ejecución concurrente.
 * Cumple con el requisito de Hilos/Concurrencia.
 */
public class RecordatorioDevolucion implements Runnable {
    
    private final ManejadorLista<Libro> libros;
    private volatile boolean ejecutar;

    public RecordatorioDevolucion(ManejadorLista<Libro> libros) {
        this.libros = libros;
        this.ejecutar = true;
    }
    
    // Método para detener el hilo de forma segura
    public void detener() {
        this.ejecutar = false;
    }
    
    @Override
    public void run() {
        System.out.println("\n[HILO DE RECORDATORIOS INICIADO] Se revisarán vencimientos periódicamente.");
        
        while (ejecutar) { 
            try {
                // Dormir el hilo 30 segundos (simula una revisión cada X tiempo)
                TimeUnit.SECONDS.sleep(60); 
                
                revisarVencimientos();
                
            } catch (InterruptedException e) {
                System.out.println("[HILO] Recordatorio interrumpido.");
                Thread.currentThread().interrupt(); 
                return;
            }
        }
        System.out.println("[HILO DE RECORDATORIOS DETENIDO]");
    }
    
    private void revisarVencimientos() {
        LocalDate hoy = LocalDate.now();
        boolean alerta = false;

        for (int i = 0; i < libros.totalElementos(); i++) {
            Libro libro = libros.obtener(i);
            
            if (libro.getEstado().equals("Prestado")) {
                try {
                    LocalDate fechaLimite = LocalDate.parse(libro.getFechaDevolucion());
                    
                    if (fechaLimite.isBefore(hoy)) {
                        System.out.println("\n*** ⚠️ ALERTA DE VENCIMIENTO (HILO) ⚠️ ***");
                        System.out.printf("El libro '%s' del autor %s estaba programado para devolverse el %s y está VENCIDO.\n", 
                            libro.getTitulo(), libro.getAutor(), libro.getFechaDevolucion());
                        alerta = true;
                    }
                } catch (Exception e) {
                    // Manejo de excepción si el formato de fecha es incorrecto
                    // Se ignora para no detener el hilo por un mal dato
                }
            }
        }
        
        if (alerta) {
             System.out.println("******************************************\n");
        }
    }
}