package net.tarou.dealership;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

/**
 * Everything the sales manager sees and types: the menu, the prompts, and the vehicle listings.
 *
 * This class owns the dealership object (the workshop deliberately keeps {@code Program} from
 * building one, so a later version can host several dealerships) and dispatches the menu number
 * to the matching {@code process…Request()} method, which in turn asks {@link Dealership} to
 * search, add or remove.
 *
 * Phase 3 state: {@link #display()}, {@link #init()}, {@link #displayMenu()},
 * {@link #displayVehicles(List)} and {@link #processGetAllVehiclesRequest()} are real. The other
 * handlers exist but only say they are unfinished — Phase 5 gives them their logic.
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

    /**
     * Loads the dealership, then shows the menu over and over until the user picks 99.
     */
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

    /**
     * Creates the file manager and assigns the dealership it loads. A missing inventory file is
     * reported and the session carries on with an empty lot, rather than crashing on start-up.
     */
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

    /** Prints the workshop menu. */
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

    /**
     * Reads one menu number. Anything that is not a number, and a closed input stream, both get
     * handled instead of throwing.
     */
    private int readCommand() {
        if (!keyboard.hasNextLine()) {
            System.out.println();
            System.out.println("No more input, ending the session.");
            return QUIT;
        }

        String entry = keyboard.nextLine().trim();

        try {
            return Integer.parseInt(entry);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /** Sends a menu number to the handler that belongs to it. */
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

    /**
     * Prints a list of vehicles in columns. Every search and list option reuses this one helper.
     */
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

    /**
     * Lists the whole lot. Implemented now, in Phase 3, because it is the quickest proof that the
     * inventory file really loaded.
     */
    private void processGetAllVehiclesRequest() {
        displayVehicles(dealership.getAllVehicles());
    }

    /**
     * TODO Phase 5: ask for a minimum and maximum price, then list
     * {@code dealership.getVehiclesInPriceRange(min, max)}.
     */
    private void processGetVehiclesInPriceRangeRequest() {
        notImplementedYet("search by price range");
    }

    /**
     * TODO Phase 5: ask for a make and a model, then list
     * {@code dealership.getVehiclesByMakeModel(make, model)}.
     */
    private void processGetVehiclesByMakeModelRequest() {
        notImplementedYet("search by make and model");
    }

    /**
     * TODO Phase 5: ask for a first and last model year, then list
     * {@code dealership.getVehiclesByYear(min, max)}.
     */
    private void processGetVehiclesByYearRequest() {
        notImplementedYet("search by year range");
    }

    /**
     * TODO Phase 5: ask for a color, then list {@code dealership.getVehiclesByColor(color)}.
     */
    private void processGetVehiclesByColorRequest() {
        notImplementedYet("search by color");
    }

    /**
     * TODO Phase 5: ask for a minimum and maximum odometer reading, then list
     * {@code dealership.getVehiclesInMileageRange(min, max)}.
     */
    private void processGetVehiclesInMileageRangeRequest() {
        notImplementedYet("search by mileage range");
    }

    /**
     * TODO Phase 5: ask for a type, convert it with {@link VehicleType#fromString(String)}, then
     * list {@code dealership.getVehiclesByType(type)}.
     */
    private void processGetVehiclesByTypeRequest() {
        notImplementedYet("search by type");
    }

    /**
     * TODO Phase 5: ask for each field, build a {@link Vehicle}, add it, then call
     * {@code fileManager.saveDealership(dealership)}.
     */
    private void processAddVehicleRequest() {
        notImplementedYet("adding a vehicle");
    }

    /**
     * TODO Phase 5: ask for a VIN, find that vehicle in the inventory, remove it, then call
     * {@code fileManager.saveDealership(dealership)}.
     */
    private void processRemoveVehicleRequest() {
        notImplementedYet("removing a vehicle");
    }

    /** Keeps the menu honest while a handler is still a stub. */
    private void notImplementedYet(String what) {
        System.out.println(what + " is not wired up yet - it lands in Phase 5.");
    }
}
