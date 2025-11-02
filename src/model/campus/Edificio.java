package model.campus;

public class Edificio {
    private final String id;              // Ej: "A", "B", "C", "D"
    private final String nombre;          // Ej: "Edificio de Ingeniería"
    private final int capacidadVehiculos; // Máximo de vehículos que puede almacenar
    private final boolean tieneCentroCarga;
    private int vehiculosEstacionados;    // Vehículos actualmente en el edificio

    // Constructor principal
    public Edificio(String id, String nombre, int capacidadVehiculos, boolean tieneCentroCarga) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID no puede estar vacío");
        }
        if (capacidadVehiculos < 0) {
            throw new IllegalArgumentException("La capacidad no puede ser negativa");
        }

        this.id = id;
        this.nombre = nombre;
        this.capacidadVehiculos = capacidadVehiculos;
        this.tieneCentroCarga = tieneCentroCarga;
        this.vehiculosEstacionados = 0;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCapacidadVehiculos() {
        return capacidadVehiculos;
    }

    public boolean isTieneCentroCarga() {
        return tieneCentroCarga;
    }

    public int getVehiculosEstacionados() {
        return vehiculosEstacionados;
    }

    // Metodos para gestión de vehículos
    public boolean tieneCapacidad() {
        return vehiculosEstacionados < capacidadVehiculos;
    }

    public int getCapacidadDisponible() {
        return capacidadVehiculos - vehiculosEstacionados;
    }

    public boolean estacionarVehiculo() {
        if (tieneCapacidad()) {
            vehiculosEstacionados++;
            return true;
        }
        return false;
    }

    public boolean retirarVehiculo() {
        if (vehiculosEstacionados > 0) {
            vehiculosEstacionados--;
            return true;
        }
        return false;
    }

    // Metodos para centros de carga
    public boolean puedeCargarVehiculos() {
        return tieneCentroCarga;
    }

    // Representación textual
    @Override
    public String toString() {
        return nombre + " (" + id + ")" +
                (tieneCentroCarga ? " ⚡" : "") +
                " [" + vehiculosEstacionados + "/" + capacidadVehiculos + " vehículos]";
    }

    // Comparación (dos edificios son iguales si tienen el mismo ID)
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Edificio edificio = (Edificio) obj;
        return id.equals(edificio.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    // Metodo estándar útil (opcionales)
    public static boolean esIdValido(String id) {
        return id != null && id.matches("[A-Z]"); // Solo letras mayúsculas
    }

    public static String generarNombreDesdeId(String id) {
        return "Edificio " + id;
    }
}