package exceptions;

public class NoVehicleAvailableException extends RuntimeException {
    public NoVehicleAvailableException(String message) {
        super(message);
    }
}
