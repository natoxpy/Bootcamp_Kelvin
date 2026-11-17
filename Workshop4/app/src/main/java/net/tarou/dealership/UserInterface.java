package net.tarou.dealership;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

/**
 * The menu, prompts and listings the sales manager works through, and the dealership they operate
 * on. Saves the inventory file after every add or remove.
 */
public class UserInterface {
    private static final int PRICE_RANGE = 1;
    private static final int MAKE_MODEL = 2;
    private static final int YEAR_RANGE = 3;
    private static final int COLOR = 4;
    private static final int MILEAGE_RANGE = 5;
    private static final int TYPE = 6;
    private static final int ALL_VEHICLES = 7;
    private static final int ADD_VEHICLE = 8;
    private static final int REMOVE_VEHICLE = 9;
    private static final int QUIT = 99;

    private Dealership dealership;
    private final DealershipFileManager fileManager = new DealershipFileManager();
    private final Scanner keyboard = new Scanner(System.in);

    public void display() {
        init();

        boolean running = true;
        while (running) {
            displayMenu();

            int command = readCommand();
            if (command == QUIT) {
                running = false;
                continue;
            }

            processUserRequest(command);
        }

        System.out.println();
        System.out.println("Goodbye.");
    }

    private void init() {
        try {
            dealership = fileManager.getDealership();
            System.out.println(
                    "Loaded "
                            + dealership.getName()
                            + " ("
                            + dealership.getAllVehicles().size()
                            + " vehicle(s)) from "
                            + fileManager.getInventoryFile().toAbsolutePath());
        } catch (FileNotFoundException e) {
            dealership = new Dealership();
            System.out.println(
                    "Could not open "
                            + fileManager.getInventoryFile().toAbsolutePath()
                            + ": "
                            + e.getMessage());
            System.out.println("Continuing with an empty dealership.");
        }
    }

    private void displayMenu() {
        System.out.println();
        System.out.println("=== " + (dealership.getName().isBlank() ? "Dealership" : dealership.getName()) + " ===");
        System.out.println("1 - Find vehicles within a price range");
        System.out.println("2 - Find vehicles by make / model");
        System.out.println("3 - Find vehicles by year range");
        System.out.println("4 - Find vehicles by color");
        System.out.println("5 - Find vehicles by mileage range");
        System.out.println("6 - Find vehicles by type (car, truck, SUV, van)");
        System.out.println("7 - List ALL vehicles");
        System.out.println("8 - Add a vehicle");
        System.out.println("9 - Remove a vehicle");
        System.out.println("99 - Quit");
        System.out.print("Choose an option: ");
    }

    private int readCommand() {
        String entry = readLine();

        if (entry == null) {
            System.out.println();
            System.out.println("No more input, ending the session.");
            return QUIT;
        }

        try {
            return Integer.parseInt(entry);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void processUserRequest(int command) {
        switch (command) {
            case PRICE_RANGE:
                processGetVehiclesInPriceRangeRequest();
                break;
            case MAKE_MODEL:
                processGetVehiclesByMakeModelRequest();
                break;
            case YEAR_RANGE:
                processGetVehiclesByYearRequest();
                break;
            case COLOR:
                processGetVehiclesByColorRequest();
                break;
            case MILEAGE_RANGE:
                processGetVehiclesInMileageRangeRequest();
                break;
            case TYPE:
                processGetVehiclesByTypeRequest();
                break;
            case ALL_VEHICLES:
                processGetAllVehiclesRequest();
                break;
            case ADD_VEHICLE:
                processAddVehicleRequest();
                break;
            case REMOVE_VEHICLE:
                processRemoveVehicleRequest();
                break;
            case QUIT:
                System.out.println("Goodbye.");
                break;
            default:
                System.out.println("That is not a menu option. Pick a number from 1 to 9, or 99 to quit.");
                break;
        }
    }

    private void displayVehicles(List<Vehicle> vehicles) {
        if (vehicles == null) {
            System.out.println("No vehicles to show.");
            return;
        }

        if (vehicles.isEmpty()) {
            System.out.println("No vehicles match.");
            return;
        }

        System.out.println();
        System.out.printf(
                "%-8s %-5s %-12s %-16s %-6s %-8s %10s %12s%n",
                "VIN", "YEAR", "MAKE", "MODEL", "TYPE", "COLOR", "ODOMETER", "PRICE");

        for (Vehicle vehicle : vehicles) {
            System.out.printf(
                    "%-8s %-5d %-12s %-16s %-6s %-8s %10d %12s%n",
                    vehicle.getVin(),
                    vehicle.getYear(),
                    vehicle.getMake(),
                    vehicle.getModel(),
                    vehicle.getType(),
                    vehicle.getColor(),
                    vehicle.getOdometer(),
                    vehicle.getPrice());
        }

        System.out.println();
        System.out.println(vehicles.size() + " vehicle(s).");
    }

    private void processGetAllVehiclesRequest() {
        displayVehicles(dealership.getAllVehicles());
    }

    private void processGetVehiclesInPriceRangeRequest() {
        BigDecimal lowest = readPrice("Lowest price to include (for example 2000): ");
        if (lowest == null) {
            return;
        }

        BigDecimal highest = readPrice("Highest price to include (for example 15000): ");
        if (highest == null) {
            return;
        }

        if (lowest.compareTo(highest) >= 0) {
            System.out.println(
                    "Swapping them: looking for prices between " + lowest + " and " + highest + " is empty.");
            BigDecimal swap = lowest;
            lowest = highest;
            highest = swap;
        }

        System.out.println();
        System.out.println("Vehicles priced between " + lowest + " and " + highest + ":");
        displayVehicles(dealership.getVehiclesInPriceRange(lowest, highest));
    }

    private void processGetVehiclesByMakeModelRequest() {
        String make = readLine("Make (for example Ford): ");
        if (make == null) {
            return;
        }

        String model = readLine("Model (for example Ranger): ");
        if (model == null) {
            return;
        }

        if (make.isEmpty() || model.isEmpty()) {
            System.out.println("Make and model are both needed. Search cancelled.");
            return;
        }

        System.out.println();
        System.out.println("Vehicles matching " + make + " " + model + ":");
        displayVehicles(dealership.getVehiclesByMakeModel(make, model));
    }

    private void processGetVehiclesByYearRequest() {
        Integer oldest = readInteger("First model year to search from (for example 2000): ");
        if (oldest == null) {
            return;
        }

        Integer newest = readInteger("Last model year to search to (for example 2015): ");
        if (newest == null) {
            return;
        }

        int lowestYear = oldest;
        int highestYear = newest;

        if (lowestYear >= highestYear) {
            int swap = lowestYear;
            lowestYear = highestYear;
            highestYear = swap;
            System.out.println("Using " + lowestYear + " to " + highestYear + ".");
        }

        System.out.println();
        System.out.println("Vehicles modelled between " + lowestYear + " and " + highestYear + ":");
        displayVehicles(dealership.getVehiclesByYear(lowestYear, highestYear));
    }

    private void processGetVehiclesByColorRequest() {
        String color = readLine("Color (for example Red): ");
        if (color == null) {
            return;
        }

        if (color.isEmpty()) {
            System.out.println("A color is needed. Search cancelled.");
            return;
        }

        System.out.println();
        System.out.println("Vehicles colored " + color + ":");
        displayVehicles(dealership.getVehiclesByColor(color));
    }

    private void processGetVehiclesInMileageRangeRequest() {
        Integer fewestMiles = readInteger("Lowest mileage to search from (for example 10000): ");
        if (fewestMiles == null) {
            return;
        }

        Integer mostMiles = readInteger("Highest mileage to search to (for example 150000): ");
        if (mostMiles == null) {
            return;
        }

        int lowestMileage = fewestMiles;
        int highestMileage = mostMiles;

        if (lowestMileage >= highestMileage) {
            int swap = lowestMileage;
            lowestMileage = highestMileage;
            highestMileage = swap;
            System.out.println("Using " + lowestMileage + " to " + highestMileage + " miles.");
        }

        System.out.println();
        System.out.println("Vehicles between " + lowestMileage + " and " + highestMileage + " miles:");
        displayVehicles(dealership.getVehiclesInMileageRange(lowestMileage, highestMileage));
    }

    private void processGetVehiclesByTypeRequest() {
        VehicleType type = readVehicleType("Vehicle type (car, truck, SUV or van): ");
        if (type == null) {
            return;
        }

        System.out.println();
        System.out.println("Vehicles of type " + type.name() + ":");
        displayVehicles(dealership.getVehiclesByType(type));
    }

    private void processAddVehicleRequest() {
        System.out.println();
        System.out.println("Add a vehicle. Leave the VIN empty to cancel.");

        String vin = readLine("VIN: ");
        if (vin == null || vin.isEmpty()) {
            System.out.println("A VIN is needed. Vehicle not added.");
            return;
        }

        if (dealership.findVehicleByVin(vin) != null) {
            System.out.println("A vehicle with VIN " + vin + " is already on the lot. Vehicle not added.");
            return;
        }

        Integer year = readInteger("Model year (for example 2015): ");
        if (year == null) {
            System.out.println("Vehicle not added.");
            return;
        }

        String make = readLine("Make (for example Ford): ");
        String model = readLine("Model (for example Ranger): ");
        if (make == null || model == null || make.isEmpty() || model.isEmpty()) {
            System.out.println("Make and model are both needed. Vehicle not added.");
            return;
        }

        VehicleType type = readVehicleType("Type (car, truck, SUV or van): ");
        if (type == null) {
            System.out.println("Vehicle not added.");
            return;
        }

        String color = readLine("Color (for example Red): ");
        if (color == null || color.isEmpty()) {
            System.out.println("A color is needed. Vehicle not added.");
            return;
        }

        Integer odometer = readInteger("Odometer reading in miles (for example 87500): ");
        if (odometer == null) {
            System.out.println("Vehicle not added.");
            return;
        }

        BigDecimal price = readPrice("Price (for example 12345.99): ");
        if (price == null) {
            System.out.println("Vehicle not added.");
            return;
        }

        Vehicle vehicle = new Vehicle(vin, year, make, model, type, color, odometer, price);
        dealership.addVehicle(vehicle);

        System.out.println();
        System.out.println("Added: " + vehicle);
        saveInventory();
    }

    private void processRemoveVehicleRequest() {
        System.out.println();
        displayVehicles(dealership.getAllVehicles());

        String vin = readLine("VIN of the vehicle to remove (empty to cancel): ");
        if (vin == null || vin.isEmpty()) {
            System.out.println("Nothing removed.");
            return;
        }

        Vehicle found = dealership.findVehicleByVin(vin);
        if (found == null) {
            System.out.println("No vehicle on the lot has VIN " + vin + ". Nothing removed.");
            return;
        }

        System.out.println("About to remove: " + found);

        String confirmation = readLine("Remove it? (y to confirm, anything else keeps it): ");
        if (confirmation == null || !confirmation.equalsIgnoreCase("y")) {
            System.out.println("Kept " + found.getVin() + ". Nothing removed.");
            return;
        }

        dealership.removeVehicle(found);

        System.out.println("Removed " + found.getVin() + ".");
        saveInventory();
    }

    private void saveInventory() {
        try {
            fileManager.saveDealership(dealership);
            System.out.println("Saved " + dealership.getAllVehicles().size()
                    + " vehicle(s) to " + fileManager.getInventoryFile().toAbsolutePath());
        } catch (FileNotFoundException e) {
            System.out.println("Could not save to "
                    + fileManager.getInventoryFile().toAbsolutePath() + ": " + e.getMessage());
            System.out.println("The change is in memory only, so it is lost when you quit.");
        }
    }

    private String readLine() {
        return keyboard.hasNextLine() ? keyboard.nextLine().trim() : null;
    }

    private String readLine(String prompt) {
        System.out.print(prompt);

        String entry = readLine();

        if (entry == null) {
            System.out.println();
            System.out.println("No more input.");
        }

        return entry;
    }

    private Integer readInteger(String prompt) {
        String entry = readLine(prompt);

        if (entry == null) {
            return null;
        }

        try {
            return Integer.valueOf(entry);
        } catch (NumberFormatException e) {
            System.out.println("\"" + entry + "\" is not a whole number.");
            return null;
        }
    }

    private BigDecimal readPrice(String prompt) {
        String entry = readLine(prompt);

        if (entry == null) {
            return null;
        }

        try {
            return new BigDecimal(entry);
        } catch (NumberFormatException e) {
            System.out.println("\"" + entry + "\" is not a price.");
            return null;
        }
    }

    private VehicleType readVehicleType(String prompt) {
        while (true) {
            String entry = readLine(prompt);

            if (entry == null || entry.isEmpty()) {
                return null;
            }

            VehicleType type = VehicleType.fromString(entry);

            if (type != null) {
                return type;
            }

            System.out.println("\"" + entry + "\" is not a type this dealership uses.");
        }
    }
}
