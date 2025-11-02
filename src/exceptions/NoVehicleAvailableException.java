package exceptions;

public class NoVehicleAvailableException extends Exception {
    private final String criterio;
    private final String pedidoId;
    private final double pesoRequerido;

    // Constructor con mensaje (Donde llama al constructor de la clase padre (Exception))
    public NoVehicleAvailableException(String message, String criterio,
                                       String pedidoId, double pesoRequerido) {
        super(message);
        this.criterio = criterio;
        this.pedidoId = pedidoId;
        this.pesoRequerido = pesoRequerido;
    }

    // Constructor simplificado
    public NoVehicleAvailableException(String criterio, String pedidoId, double pesoRequerido) {
        this("No hay vehículos disponibles para el pedido " + pedidoId +
                        ". Criterio: " + criterio + ", Peso: " + pesoRequerido + "kg",
                criterio, pedidoId, pesoRequerido);
    }

    // Constructor más simple
    public NoVehicleAvailableException(String pedidoId) {
        this("No hay vehículos disponibles", "disponibilidad general", pedidoId, 0);
    }

    // Getters
    public String getCriterio() { return criterio; }
    public String getPedidoId() { return pedidoId; }
    public double getPesoRequerido() { return pesoRequerido; }

    @Override
    public String toString() {
        return getMessage();
    }
}