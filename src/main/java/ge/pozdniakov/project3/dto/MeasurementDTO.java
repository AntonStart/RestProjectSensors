package ge.pozdniakov.project3.dto;

import ge.pozdniakov.project3.models.Sensor;
import jakarta.validation.constraints.*;

public class MeasurementDTO {

    @DecimalMin(value = "-100.0", message = "Temperature should be greater than -100")
    @DecimalMax(value = "100.0", message = "Temperature should be less than 100")
    private double value;

    @NotNull(message = "Raining status should not be empty")
    private boolean raining;

    @NotEmpty(message = "Sensor name should not be empty")
    @NotNull(message = "Sensor name should not be null")
    private String sensor;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public boolean isRaining() {
        return raining;
    }

    public void setRaining(boolean raining) {
        this.raining = raining;
    }

    public String getSensor() {
        return sensor;
    }

    public void setSensor(String sensor) {
        this.sensor = sensor;
    }
}
