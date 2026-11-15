package net.tarou.db;

import java.math.BigDecimal;

public class Vehicle {
    public enum VehicleType {
        MICRO,
        SPORT_CAR,
        SUR,
        CROSSOVER,
        PICKUP,
        VAN,
        MINIVAN,
        MUSCLE_CAR,
        ROADSTER,
        COUPE,
    }

    private String vin;
    private int year;
    private String make;
    private String model;
    private VehicleType vtype;
    private String color;
    private int odometer;
    private BigDecimal price;

    public String getVin() {
        return vin;
    }

    public int getYear() {
        return year;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public VehicleType getVtype() {
        return vtype;
    }

    public String getColor() {
        return color;
    }

    public int getOdometer() {
        return odometer;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
