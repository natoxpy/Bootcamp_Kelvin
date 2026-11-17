package net.tarou.dealership;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * The persistence layer: turns the pipe-delimited inventory file into a loaded {@link Dealership}
 * object, and writes a {@link Dealership} back over that file after every add or remove.
 *
 * File layout, exactly as the workshop specifies:
 *
 * <pre>
 * D &amp; B Used Cars|111 Old Benbrook Rd|817-555-5555
 * 10112|1993|Ford|Explorer|SUV|Red|525123|995.00
 * </pre>
 *
 * The first line is the dealership's name, address and phone. Every later line is one vehicle:
 * {@code vin|year|make|model|type|color|odometer|price}. Blank lines are ignored.
 *
 * The default path is {@code data/inventory.csv}, which resolves to {@code app/data/inventory.csv}
 * when the app is launched with {@code ./gradlew :app:run} because Gradle runs with the project
 * directory as its working directory. Tests pass their own path to the second constructor.
 */
public class DealershipFileManager {
    private static final Path DEFAULT_INVENTORY_FILE = Path.of("data", "inventory.csv");

    private static final int DEALERSHIP_FIELD_COUNT = 3;
    private static final int VEHICLE_FIELD_COUNT = 8;

    private final Path inventoryFile;

    /** Loads and saves {@code data/inventory.csv} relative to the working directory. */
    public DealershipFileManager() {
        this(DEFAULT_INVENTORY_FILE);
    }

    /** Loads and saves an explicit file, which keeps tests independent of the real inventory. */
    public DealershipFileManager(Path inventoryFile) {
        this.inventoryFile = inventoryFile;
    }

    public Path getInventoryFile() {
        return inventoryFile;
    }

    /**
     * Reads the inventory file and builds a fully populated {@link Dealership}: its contact
     * details plus one {@link Vehicle} per data line.
     *
     * @return the dealership the file describes
     * @throws FileNotFoundException when the inventory file is not there
     */
    public Dealership getDealership() throws FileNotFoundException {
        Dealership dealership = new Dealership();

        try (Scanner reader = new Scanner(inventoryFile.toFile())) {
            boolean headerRead = false;
            int lineNumber = 0;

            while (reader.hasNextLine()) {
                lineNumber++;
                String line = reader.nextLine();

                if (line.isBlank()) {
                    continue;
                }

                if (!headerRead) {
                    applyDealershipLine(dealership, line, lineNumber);
                    headerRead = true;
                    continue;
                }

                dealership.addVehicle(parseVehicleLine(line, lineNumber));
            }
        }

        return dealership;
    }

    /**
     * Overwrites the inventory file with the dealership's current name, address, phone and
     * inventory list, in the same layout {@link #getDealership()} reads back.
     *
     * The header line is written first, then one record line per vehicle, in inventory order.
     * {@link Vehicle#toString()} is the record format, so what is written here is what is read
     * there. {@code inventory.csv.bak} keeps the starter file this session began from.
     *
     * @throws FileNotFoundException when the inventory file cannot be opened for writing
     */
    public void saveDealership(Dealership dealership) throws FileNotFoundException {
        try (PrintStream writer = new PrintStream(inventoryFile.toFile())) {
            writer.println(
                    dealership.getName()
                            + "|"
                            + dealership.getAddress()
                            + "|"
                            + dealership.getPhone());

            for (Vehicle vehicle : dealership.getAllVehicles()) {
                writer.println(vehicle);
            }
        }
    }

    /** Fills in the dealership fields from the first line of the file. */
    private void applyDealershipLine(Dealership dealership, String line, int lineNumber) {
        String[] fields = line.split("\\|");

        if (fields.length < DEALERSHIP_FIELD_COUNT) {
            throw new IllegalStateException(
                    "Line "
                            + lineNumber
                            + " of "
                            + inventoryFile.toAbsolutePath()
                            + " should hold name|address|phone, but was: "
                            + line);
        }

        dealership.setName(fields[0].trim());
        dealership.setAddress(fields[1].trim());
        dealership.setPhone(fields[2].trim());
    }

    /**
     * Converts one data line into a {@link Vehicle}, reporting which line was unusable so a
     * hand-edited inventory file is easy to fix.
     */
    private Vehicle parseVehicleLine(String line, int lineNumber) {
        String[] fields = line.split("\\|");

        if (fields.length < VEHICLE_FIELD_COUNT) {
            throw new IllegalStateException(
                    "Line "
                            + lineNumber
                            + " of "
                            + inventoryFile.toAbsolutePath()
                            + " should hold vin|year|make|model|type|color|odometer|price, but was: "
                            + line);
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setVin(fields[0].trim());
        vehicle.setYear(readInt(fields[1], "year", lineNumber, line));
        vehicle.setMake(fields[2].trim());
        vehicle.setModel(fields[3].trim());

        String rawType = fields[4].trim();
        VehicleType type = VehicleType.fromString(rawType);
        if (type == null) {
            throw new IllegalStateException(
                    "Line "
                            + lineNumber
                            + " has vehicle type \""
                            + rawType
                            + "\", which is not car, truck, SUV or van: "
                            + line);
        }
        vehicle.setType(type);

        vehicle.setColor(fields[5].trim());
        vehicle.setOdometer(readInt(fields[6], "odometer", lineNumber, line));

        String rawPrice = fields[7].trim();
        try {
            vehicle.setPrice(new BigDecimal(rawPrice));
        } catch (NumberFormatException e) {
            throw new IllegalStateException(
                    "Line "
                            + lineNumber
                            + " has price \""
                            + rawPrice
                            + "\", which is not a number: "
                            + line,
                    e);
        }

        return vehicle;
    }

    /** Parses an odometer or model-year column, reporting the offending line on failure. */
    private int readInt(String rawValue, String fieldName, int lineNumber, String line) {
        try {
            return Integer.parseInt(rawValue.trim());
        } catch (NumberFormatException e) {
            throw new IllegalStateException(
                    "Line "
                            + lineNumber
                            + " has "
                            + fieldName
                            + " \""
                            + rawValue
                            + "\", which is not a whole number: "
                            + line,
                    e);
        }
    }
}
