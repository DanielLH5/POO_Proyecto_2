package model.campus;

public class Edificio {
<<<<<<< HEAD
    private final String id;              // Ej: "A", "B", "C", "D"
    private final String nombre;          // Ej: "Edificio de Ingeniería"
    private final int capacidadVehiculos; // Máximo de vehículos que puede almacenar
    private final boolean tieneCentroCarga;
    private int vehiculosEstacionados;    // Vehículos actualmente en el edificio
=======
<<<<<<< Updated upstream
}
=======
    private final String id;
    private String nombre;
    private int capacidadVehiculos;
    private boolean tieneCentroCarga;
    private int vehiculosEstacionados;
    private boolean esCentroPrincipal;    // para modo centralizado
>>>>>>> Aportes_Elian

    // Constructor principal
    public Edificio(String id, String nombre, int capacidadVehiculos, boolean tieneCentroCarga) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID no puede estar vacío");
        }
        if (capacidadVehiculos < 0) {
            throw new IllegalArgumentException("La capacidad no puede ser negativa");
        }

<<<<<<< HEAD
        this.id = id;
=======
        this.id = id.toUpperCase(); // NUEVO: convertir a mayúsculas
>>>>>>> Aportes_Elian
        this.nombre = nombre;
        this.capacidadVehiculos = capacidadVehiculos;
        this.tieneCentroCarga = tieneCentroCarga;
        this.vehiculosEstacionados = 0;
<<<<<<< HEAD
=======
        this.esCentroPrincipal = false; // NUEVO: inicializar como falso
    }

    // NUEVO: Constructor sobrecargado para modo centralizado
    public Edificio(String id, String nombre, int capacidadVehiculos, boolean tieneCentroCarga, boolean esCentroPrincipal) {
        this(id, nombre, capacidadVehiculos, tieneCentroCarga);
        this.esCentroPrincipal = esCentroPrincipal;
>>>>>>> Aportes_Elian
    }

    // Getters
    public String getId() {
        return id;
    }

<<<<<<< HEAD
=======
    // NUEVO: Setter para nombre
    public void setNombre(String nombre) {
        if (nombre != null && !nombre.trim().isEmpty()) {
            this.nombre = nombre;
        }
    }

    public void setTieneCentroCarga(boolean tieneCentroCarga) {
        this.tieneCentroCarga = tieneCentroCarga;
    }

    public void setCapacidadVehiculos(int capacidadVehiculos) {
        if (capacidadVehiculos >= 0) {
            this.capacidadVehiculos = capacidadVehiculos;
        }
    }

    // NUEVO: Getters y Setters para centro principal
    public boolean isEsCentroPrincipal() {
        return esCentroPrincipal;
    }

    public void setEsCentroPrincipal(boolean esCentroPrincipal) {
        this.esCentroPrincipal = esCentroPrincipal;
        // Si es centro principal, automáticamente tiene centro de carga
        if (esCentroPrincipal) {
            this.tieneCentroCarga = true;
        }
    }

>>>>>>> Aportes_Elian
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

<<<<<<< HEAD
    // Metodos para gestión de vehículos
=======
    public String getTipoEdificio() {
        if (esCentroPrincipal) {
            return "Centro Principal";
        } else if (tieneCentroCarga) {
            return "Centro de Carga";
        } else {
            return "Punto de Entrega";
        }
    }

>>>>>>> Aportes_Elian
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

<<<<<<< HEAD
    // Metodos para centros de carga
=======
    public boolean estacionarVehiculos(int cantidad) {
        if (vehiculosEstacionados + cantidad <= capacidadVehiculos) {
            vehiculosEstacionados += cantidad;
            return true;
        }
        return false;
    }

    public boolean retirarVehiculos(int cantidad) {
        if (vehiculosEstacionados >= cantidad) {
            vehiculosEstacionados -= cantidad;
            return true;
        }
        return false;
    }

>>>>>>> Aportes_Elian
    public boolean puedeCargarVehiculos() {
        return tieneCentroCarga;
    }

<<<<<<< HEAD
    // Representación textual
    @Override
    public String toString() {
        return nombre + " (" + id + ")" +
                (tieneCentroCarga ? " ⚡" : "") +
                " [" + vehiculosEstacionados + "/" + capacidadVehiculos + " vehículos]";
    }

    // Comparación (dos edificios son iguales si tienen el mismo ID)
=======
    public boolean puedeSerCentroPrincipal() {
        return tieneCentroCarga && capacidadVehiculos >= 10; // Mínimo 10 vehículos para centro principal
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(nombre).append(" (").append(id).append(")");

        if (esCentroPrincipal) {
            sb.append(" 🏢⚡"); // Centro principal
        } else if (tieneCentroCarga) {
            sb.append(" ⚡"); // Centro de carga normal
        }

        sb.append(" [").append(vehiculosEstacionados)
                .append("/").append(capacidadVehiculos).append(" vehículos]");

        return sb.toString();
    }

    public String getInfoDetallada() {
        return String.format(
                "Edificio: %s (%s)\n" +
                        "Capacidad: %d vehículos\n" +
                        "Vehículos estacionados: %d\n" +
                        "Centro de carga: %s\n" +
                        "Centro principal: %s\n" +
                        "Tipo: %s",
                nombre, id, capacidadVehiculos, vehiculosEstacionados,
                tieneCentroCarga ? "SÍ" : "NO",
                esCentroPrincipal ? "SÍ" : "NO",
                getTipoEdificio()
        );
    }

>>>>>>> Aportes_Elian
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

<<<<<<< HEAD
    // Metodo estándar útil (opcionales)
    public static boolean esIdValido(String id) {
        return id != null && id.matches("[A-Z]"); // Solo letras mayúsculas
=======
    public static boolean esIdValido(String id) {
        return id != null && id.matches("[A-Za-z0-9]+"); // MODIFICADO: Permitir letras y números
>>>>>>> Aportes_Elian
    }

    public static String generarNombreDesdeId(String id) {
        return "Edificio " + id;
    }
}<<<<<<< HEAD
}
=======

    public static Edificio crearConNombreAutomatico(String id, int capacidadVehiculos, boolean tieneCentroCarga) {
        return new Edificio(id, generarNombreDesdeId(id), capacidadVehiculos, tieneCentroCarga);
    }

    public Edificio clone() {
        Edificio clone = new Edificio(this.id, this.nombre, this.capacidadVehiculos, this.tieneCentroCarga);
        clone.esCentroPrincipal = this.esCentroPrincipal;
        clone.vehiculosEstacionados = this.vehiculosEstacionados;
        return clone;
    }
}
>>>>>>> Stashed changes
>>>>>>> Aportes_Elian
