package net.tarou.dealership;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * The dealership: its contact details plus the list of vehicles on the lot.
 *
 * The workshop builds this class in stages. Phase 1 gives it an inventory it can grow and list;
 * the searches stay stubbed until Phase 5, and {@link #removeVehicle(Vehicle)} is filled in at
 * the same time.
 *
 * Range searches are exclusive on both ends, matching the {@code min < value < max} semantics of
 * the filters sketched in the older {@code net.tarou.db.Dealership}.
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
     * Takes a vehicle off the lot.
     *
     * TODO Phase 5: remove {@code vehicle} from {@link #inventory}.
     */
    public void removeVehicle(Vehicle vehicle) {
        // Intentionally empty until Phase 5.
    }

    /**
     * Vehicles priced strictly between {@code min} and {@code max}.
     *
     * TODO Phase 5: implement the search.
     */
    public List<Vehicle> getVehiclesInPriceRange(BigDecimal min, BigDecimal max) {
        return null;
    }

    /**
     * Vehicles whose make and model both match, ignoring case.
     *
     * TODO Phase 5: implement the search.
     */
    public List<Vehicle> getVehiclesByMakeModel(String make, String model) {
        return null;
    }

    /**
     * Vehicles with a model year strictly between {@code min} and {@code max}.
     *
     * TODO Phase 5: implement the search.
     */
    public List<Vehicle> getVehiclesByYear(int min, int max) {
        return null;
    }

    /**
     * Vehicles matching {@code color}, ignoring case.
     *
     * TODO Phase 5: implement the search.
     */
    public List<Vehicle> getVehiclesByColor(String color) {
        return null;
    }

    /**
     * Vehicles with an odometer reading strictly between {@code min} and {@code max} miles.
     *
     * TODO Phase 5: implement the search.
     */
    public List<Vehicle> getVehiclesInMileageRange(int min, int max) {
        return null;
    }

    /**
     * Vehicles of one {@link VehicleType}.
     *
     * TODO Phase 5: implement the search.
     */
    public List<Vehicle> getVehiclesByType(VehicleType type) {
        return null;
    }
}
