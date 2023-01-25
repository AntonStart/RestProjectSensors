package ge.pozdniakov.project3.util;

public class SensorNotFoundException extends RuntimeException{
    public SensorNotFoundException(String message) {
        super(message);
    }

    public SensorNotFoundException() {
    }
}
