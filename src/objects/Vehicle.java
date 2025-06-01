package objects;

import java.util.Objects;

public class Vehicle {
    private final String licensePlate;
    private final int originalValue;

    public Vehicle(String licensePlate, int originalValue) {
        validateLicensePlate(licensePlate);
        validateOriginalValue(originalValue);
        this.licensePlate = licensePlate;
        this.originalValue = originalValue;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public int getOriginalValue() {
        return originalValue;
    }

    ///  Validation methods

    private void validateLicensePlate(String licensePlate) {
        if (licensePlate == null || licensePlate.length() != 7) {
            throw new IllegalArgumentException("License plate cannot be null or have less than 7 characters");
        }
        if (!licensePlate.matches("[A-Z0-9]{7}")) {
            throw new IllegalArgumentException("License plate must consist of exactly 7 uppercase letters or digits");
        }
    }

    private void validateOriginalValue(int originalValue) {
        if (originalValue <= 0) {
            throw new IllegalArgumentException("Original value cannot be negative");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return originalValue == vehicle.originalValue && Objects.equals(licensePlate, vehicle.licensePlate);
    }

}
