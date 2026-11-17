package net.tarou.dealership;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * A dealership: its contact details plus the vehicles on its lot.
 *
 * Range searches are exclusive at both ends.
 */
public class Dealership {
    private String name;
    private String address;
    private String phone;
    private final List<Vehicle> inventory = new ArrayList<>();

    public Dealership() {
        this("", "", "", new ArrayList<>());
    }

    public Dealership(String name, String address, String phone, List<Vehicle> inventory) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.inventory.clear();
        if (inventory != null) {
            this.inventory.addAll(inventory);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Vehicle> getAllVehicles() {
        return inventory;
    }

    public void addVehicle(Vehicle vehicle) {
        if (vehicle != null) {
            inventory.add(vehicle);
        }
    }

    public boolean removeVehicle(Vehicle vehicle) {
        return inventory.remove(vehicle);
    }

    public Vehicle findVehicleByVin(String vin) {
        if (vin == null) {
            return null;
        }

        Vehicle probe = new Vehicle();
        probe.setVin(vin);

        for (Vehicle vehicle : inventory) {
            if (vehicle.equals(probe)) {
                return vehicle;
            }
        }

        return null;
    }

    public List<Vehicle> getVehiclesInPriceRange(BigDecimal min, BigDecimal max) {
        List<Vehicle> matches = new ArrayList<>();

        for (Vehicle vehicle : inventory) {
            BigDecimal price = vehicle.getPrice();

            // compareTo, not equals: 995.0 and 995.00 are the same price
            if (price != null && price.compareTo(min) > 0 && price.compareTo(max) < 0) {
                matches.add(vehicle);
            }
        }

        return matches;
    }

    public List<Vehicle> getVehiclesByMakeModel(String make, String model) {
        List<Vehicle> matches = new ArrayList<>();
        String wantedMake = make.trim();
        String wantedModel = model.trim();

        for (Vehicle vehicle : inventory) {
            if (wantedMake.equalsIgnoreCase(vehicle.getMake())
                    && wantedModel.equalsIgnoreCase(vehicle.getModel())) {
                matches.add(vehicle);
            }
        }

        return matches;
    }

    public List<Vehicle> getVehiclesByYear(int min, int max) {
        List<Vehicle> matches = new ArrayList<>();

        for (Vehicle vehicle : inventory) {
            int year = vehicle.getYear();

            if (year > min && year < max) {
                matches.add(vehicle);
            }
        }

        return matches;
    }

    public List<Vehicle> getVehiclesByColor(String color) {
        List<Vehicle> matches = new ArrayList<>();
        String wantedColor = color.trim();

        for (Vehicle vehicle : inventory) {
            if (wantedColor.equalsIgnoreCase(vehicle.getColor())) {
                matches.add(vehicle);
            }
        }

        return matches;
    }

    public List<Vehicle> getVehiclesInMileageRange(int min, int max) {
        List<Vehicle> matches = new ArrayList<>();

        for (Vehicle vehicle : inventory) {
            int odometer = vehicle.getOdometer();

            if (odometer > min && odometer < max) {
                matches.add(vehicle);
            }
        }

        return matches;
    }

    public List<Vehicle> getVehiclesByType(VehicleType type) {
        List<Vehicle> matches = new ArrayList<>();

        for (Vehicle vehicle : inventory) {
            if (vehicle.getType() == type) {
                matches.add(vehicle);
            }
        }

        return matches;
    }
}
