package modulos;

/**
 * Clase Abstracta Usuario: Define atributos y comportamiento común (incluyendo contraseña).
 * Cumple con el requisito de Clases Abstractas y Manejo de Excepciones.
 */
public abstract class Usuario {
    protected String nombre;
    protected String email;
    protected String password; // <-- NUEVO: Contraseña
    protected String rol;

    public Usuario(String nombre, String email, String password, String rol) throws IllegalArgumentException {
        // Validar Nombre
        if (nombre == null || nombre.trim().length() < 3) {
            throw new IllegalArgumentException("Nombre inválido. Debe tener al menos 3 caracteres.");
        }
        // Validar Email
        if (email == null || !email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("Formato de email inválido. Debe contener '@' y un '.'.");
        }
        // Validar Contraseña
        if (password == null || password.length() < 4) {
             throw new IllegalArgumentException("Contraseña inválida. Debe tener al menos 4 caracteres.");
        }

        this.nombre = nombre;
        this.email = email;
        this.password = password; // Asignación de contraseña
        this.rol = rol;
    }

    // Método abstracto: Obliga a las subclases a implementar su propio menú (Polimorfismo).
    public abstract void mostrarMenu();

    // Getters
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getRol() { return rol; }
    
    // NUEVO: Método para verificar credenciales (similar al ejemplo de proyecto.zip)
    public boolean checkCredentials(String email, String password) {
        // Compara el email (ignorando mayúsculas/minúsculas) y la contraseña (estrictamente)
        return this.email.equalsIgnoreCase(email) && this.password.equals(password);
    }
    
    // Método toString para guardar en archivo (Persistencia)
    @Override
    public String toString() {
        // El formato ahora incluye la contraseña para guardarla: nombre,email,password,rol
        return nombre + "," + email + "," + password + "," + rol;
    }
}