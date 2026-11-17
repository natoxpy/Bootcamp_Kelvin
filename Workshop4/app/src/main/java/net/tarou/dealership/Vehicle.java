package net.tarou.dealership;

import java.math.BigDecimal;

/**
 * One vehicle on the lot. Each field is one column of the inventory file:
 *
 * <pre>vin|year|make|model|type|color|odometer|price</pre>
 *
 * A vehicle is identified by its VIN, so {@link #equals(Object)} compares VINs only.
 */
public class Vehicle {
    private String vin;
    private int year;
    private String make;
    private String model;
    private VehicleType type;
    private String color;
    private int odometer;
    private BigDecimal price;

    public Vehicle() {
        this("", 0, "", "", VehicleType.CAR, "", 0, BigDecimal.ZERO);
    }

    public Vehicle(
            String vin,
            int year,
            String make,
            String model,
            VehicleType type,
            String color,
            int odometer,
            BigDecimal price) {
        this.vin = vin;
        this.year = year;
        this.make = make;
        this.model = model;
        this.type = type;
        this.color = color;
        this.odometer = odometer;
        this.price = price;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getOdometer() {
        return odometer;
    }

    public void setOdometer(int odometer) {
        this.odometer = odometer;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Vehicle otherVehicle)) {
            return false;
        }
        return vin != null && vin.equalsIgnoreCase(otherVehicle.vin);
    }

    @Override
    public int hashCode() {
        return vin == null ? 0 : vin.toUpperCase().hashCode();
    }

    /** The record line as it is written to the inventory file. */
    @Override
    public String toString() {
        return String.join(
                "|",
                vin,
                String.valueOf(year),
                make,
                model,
                type == null ? "" : type.name(),
                color,
                String.valueOf(odometer),
                price == null ? "" : price.toPlainString());
    }
}
