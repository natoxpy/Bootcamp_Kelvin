package net.tarou.dealership;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Reads the pipe-delimited inventory file into a {@link Dealership}, and writes one back over it.
 *
 * <pre>
 * D &amp; B Used Cars|111 Old Benbrook Rd|817-555-5555
 * 10112|1993|Ford|Explorer|SUV|Red|525123|995.00
 * </pre>
 *
 * First line is the dealership's name, address and phone; every later line is one vehicle.
 * Blank lines are ignored.
 */
public class DealershipFileManager {
    private static final Path DEFAULT_INVENTORY_FILE = Path.of("data", "inventory.csv");

    private static final int DEALERSHIP_FIELD_COUNT = 3;
    private static final int VEHICLE_FIELD_COUNT = 8;

    private final Path inventoryFile;

    public DealershipFileManager() {
        this(DEFAULT_INVENTORY_FILE);
    }

    public DealershipFileManager(Path inventoryFile) {
        this.inventoryFile = inventoryFile;
    }

    public Path getInventoryFile() {
        return inventoryFile;
    }

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
