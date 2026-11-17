package net.tarou.dealership;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * The dealership: its contact details plus the list of vehicles on the lot.
 *
 * All searches and mutations are held in memory here; saving to disk is
 * {@link DealershipFileManager}'s job, and {@link UserInterface} calls it after every change.
 *
 * Range searches are exclusive on both ends: a vehicle must be strictly greater than the minimum
 * and strictly less than the maximum.
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

    /**
     * Every vehicle currently on the lot, in the order it was loaded or added.
     */
    public List<Vehicle> getAllVehicles() {
        return inventory;
    }

    /**
     * Puts a vehicle on the lot. Persisting it to disk is the caller's job, because only
     * {@link DealershipFileManager} knows where the inventory lives.
     */
    public void addVehicle(Vehicle vehicle) {
        if (vehicle != null) {
            inventory.add(vehicle);
        }
    }

    /**
     * Takes a vehicle off the lot. {@link Vehicle#equals(Object)} compares VINs, so a
     * {@link Vehicle} that only carries the VIN the sales manager typed is enough to find the one
     * to remove.
     *
     * @return {@code true} when a vehicle was found and removed
     */
    public boolean removeVehicle(Vehicle vehicle) {
        return inventory.remove(vehicle);
    }

    /**
     * Finds the vehicle with this VIN, or {@code null} if the lot has no such vehicle. Lets the
     * user confirm which vehicle a removal refers to before it is taken off the lot.
     */
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

    /**
     * Vehicles priced strictly between {@code min} and {@code max}. A vehicle priced exactly
     * {@code min} or exactly {@code max} is not included. {@code compareTo} is used rather than
     * {@code equals} of the scaled values, so {@code 995.0} and {@code 995.00} compare the same.
     */
    public List<Vehicle> getVehiclesInPriceRange(BigDecimal min, BigDecimal max) {
        List<Vehicle> matches = new ArrayList<>();

        for (Vehicle vehicle : inventory) {
            BigDecimal price = vehicle.getPrice();

            if (price != null && price.compareTo(min) > 0 && price.compareTo(max) < 0) {
                matches.add(vehicle);
            }
        }

        return matches;
    }

    /**
     * Vehicles whose make and model both match, ignoring case and surrounding spaces.
     */
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

    /**
     * Vehicles with a model year strictly between {@code min} and {@code max}.
     */
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

    /**
     * Vehicles matching {@code color}, ignoring case and surrounding spaces.
     */
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

    /**
     * Vehicles with an odometer reading strictly between {@code min} and {@code max} miles.
     */
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

    /**
     * Vehicles of one {@link VehicleType}.
     */
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
