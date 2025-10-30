package exceptions;

public class InsufficientBatteryException extends RuntimeException {
    public InsufficientBatteryException(String message) {
        super(message);
    }
}
