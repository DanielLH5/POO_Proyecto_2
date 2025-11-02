package exceptions;

public class InsufficientBatteryException extends Exception {
    private final String vehiculoId;
    private final double bateriaActual;
    private final double bateriaRequerida;

    // Constructor con mensaje (Donde llama al constructor de la clase padre (Exception))
    public InsufficientBatteryException(String message, String vehiculoId,
                                        double bateriaActual, double bateriaRequerida) {
        super(message);
        this.vehiculoId = vehiculoId;
        this.bateriaActual = bateriaActual;
        this.bateriaRequerida = bateriaRequerida;
    }

    // Constructor simplificado
    public InsufficientBatteryException(String vehiculoId, double bateriaActual,
                                        double bateriaRequerida) {
        this("Batería insuficiente para el vehículo " + vehiculoId +
                        ". Actual: " + bateriaActual + "%, Requerida: " + bateriaRequerida + "%",
                vehiculoId, bateriaActual, bateriaRequerida);
    }

    // Getters
    public String getVehiculoId() { return vehiculoId; }
    public double getBateriaActual() { return bateriaActual; }
    public double getBateriaRequerida() { return bateriaRequerida; }

    @Override
    public String toString() {
        return getMessage() + " [Vehículo: " + vehiculoId + "]";
    }
}