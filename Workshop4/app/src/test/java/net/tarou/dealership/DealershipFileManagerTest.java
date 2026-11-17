package net.tarou.dealership;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DealershipFileManagerTest {
    @TempDir
    Path tempDir;

    @Test
    void loadsDealershipDetailsAndEveryVehicle() throws Exception {
        Path csv = writeInventory(
                """
                D & B Used Cars|111 Old Benbrook Rd|817-555-5555
                10112|1993|Ford|Explorer|SUV|Red|525123|995.00
                37846|2001|Ford|Ranger|truck|Yellow|172544|1995.00
                """);

        Dealership dealership = new DealershipFileManager(csv).getDealership();

        assertEquals("D & B Used Cars", dealership.getName());
        assertEquals("111 Old Benbrook Rd", dealership.getAddress());
        assertEquals("817-555-5555", dealership.getPhone());

        List<Vehicle> inventory = dealership.getAllVehicles();
        assertEquals(2, inventory.size());

        Vehicle explorer = inventory.get(0);
        assertEquals("10112", explorer.getVin());
        assertEquals(1993, explorer.getYear());
        assertEquals("Ford", explorer.getMake());
        assertEquals("Explorer", explorer.getModel());
        assertEquals(VehicleType.SUV, explorer.getType());
        assertEquals("Red", explorer.getColor());
        assertEquals(525123, explorer.getOdometer());
        assertEquals(0, explorer.getPrice().compareTo(new BigDecimal("995.00")));
    }

    @Test
    void acceptsAnyLetterCasingInTheTypeColumn() throws Exception {
        Path csv = writeInventory(
                """
                Lot|1 Main St|555-0100
                1|2001|Ford|Ranger|truck|Yellow|10|100.00
                2|2002|Honda|Civic|CAR|Gray|20|200.00
                3|2003|Ford|Explorer|suv|Red|30|300.00
                4|2004|Honda|Odyssey|Minivan|White|40|400.00
                """);

        List<Vehicle> inventory = new DealershipFileManager(csv).getDealership().getAllVehicles();

        assertEquals(VehicleType.TRUCK, inventory.get(0).getType());
        assertEquals(VehicleType.CAR, inventory.get(1).getType());
        assertEquals(VehicleType.SUV, inventory.get(2).getType());
        assertEquals(VehicleType.VAN, inventory.get(3).getType());
    }

    @Test
    void ignoresBlankLines() throws Exception {
        Path csv = writeInventory(
                """
                Lot|1 Main St|555-0100

                1|2001|Ford|Ranger|truck|Yellow|10|100.00

                """);

        assertEquals(1, new DealershipFileManager(csv).getDealership().getAllVehicles().size());
    }

    @Test
    void reportsWhichLineHasAnUnknownVehicleType() throws Exception {
        Path csv = writeInventory(
                """
                Lot|1 Main St|555-0100
                1|2001|Ford|Ranger|spaceship|Yellow|10|100.00
                """);

        IllegalStateException problem = assertThrows(
                IllegalStateException.class, () -> new DealershipFileManager(csv).getDealership());

        assertTrue(problem.getMessage().contains("Line 2"), problem.getMessage());
        assertTrue(problem.getMessage().contains("spaceship"), problem.getMessage());
    }

    @Test
    void reportsWhichLineHasANonNumericYear() throws Exception {
        Path csv = writeInventory(
                """
                Lot|1 Main St|555-0100
                1|twelve|Ford|Ranger|truck|Yellow|10|100.00
                """);

        IllegalStateException problem = assertThrows(
                IllegalStateException.class, () -> new DealershipFileManager(csv).getDealership());

        assertTrue(problem.getMessage().contains("year"), problem.getMessage());
    }

    @Test
    void reportsWhichLineIsTooShort() throws Exception {
        Path csv = writeInventory(
                """
                Lot|1 Main St|555-0100
                1|2001|Ford|Ranger|truck
                """);

        IllegalStateException problem = assertThrows(
                IllegalStateException.class, () -> new DealershipFileManager(csv).getDealership());

        assertTrue(problem.getMessage().contains("Line 2"), problem.getMessage());
    }

    @Test
    void reportsAMissingInventoryFile() {
        Path missing = tempDir.resolve("nope.csv");

        FileNotFoundException problem = assertThrows(
                FileNotFoundException.class, () -> new DealershipFileManager(missing).getDealership());

        assertTrue(problem.getMessage() == null || !problem.getMessage().isEmpty());
    }

    @Test
    void vehiclesWithTheSameVinAreTheSameVehicle() {
        Vehicle a = new Vehicle("10112", 1993, "Ford", "Explorer", VehicleType.SUV, "Red", 1, null);
        Vehicle b = new Vehicle("10112", 2020, "Other", "Other", VehicleType.CAR, "Blue", 2, null);
        Vehicle c = new Vehicle("99999", 1993, "Ford", "Explorer", VehicleType.SUV, "Red", 1, null);

        assertEquals(a, b);
        assertTrue(a.hashCode() == b.hashCode());
        assertTrue(!a.equals(c));
        assertNull(a.getPrice());
    }

    @Test
    void savingThenLoadingKeepsTheDealershipIdentical() throws Exception {
        Path csv = writeInventory(
                """
                D & B Used Cars|111 Old Benbrook Rd|817-555-5555
                10112|1993|Ford|Explorer|SUV|Red|525123|995.00
                37846|2001|Ford|Ranger|truck|Yellow|172544|1995.00
                44901|2012|Honda|Civic|car|Gray|103221|6995.00
                """);

        DealershipFileManager fileManager = new DealershipFileManager(csv);
        Dealership dealership = fileManager.getDealership();

        assertTrue(dealership.removeVehicle(new Vehicle("37846", 2001, "Ford", "Ranger", VehicleType.TRUCK, "Yellow", 172544, new BigDecimal("1995.00"))));
        dealership.addVehicle(new Vehicle(
                "71345", 2004, "Honda", "Odyssey", VehicleType.VAN, "White", 188300, new BigDecimal("8750.00")));
        fileManager.saveDealership(dealership);

        Dealership reloaded = new DealershipFileManager(csv).getDealership();

        assertEquals("D & B Used Cars", reloaded.getName());
        assertEquals("111 Old Benbrook Rd", reloaded.getAddress());
        assertEquals("817-555-5555", reloaded.getPhone());
        assertEquals(List.of("10112", "44901", "71345"), reloaded.getAllVehicles().stream().map(Vehicle::getVin).toList());
        assertEquals(0, reloaded.findVehicleByVin("71345").getPrice().compareTo(new BigDecimal("8750.00")));
        assertEquals(VehicleType.VAN, reloaded.findVehicleByVin("71345").getType());
    }

    @Test
    void saveWritesOneHeaderLineAndOneRecordLinePerVehicle() throws Exception {
        Path csv = writeInventory("Lot|1 Main St|555-0100\n1|2001|Ford|Ranger|truck|Yellow|10|100.00\n");

        Dealership dealership = new DealershipFileManager(csv).getDealership();
        dealership.addVehicle(new Vehicle(
                "2|2002", 2002, "Honda", "Civic", VehicleType.CAR, "Gray", 20, new BigDecimal("200.00")));
        new DealershipFileManager(csv).saveDealership(dealership);

        List<String> lines = Files.readAllLines(csv);

        assertEquals(List.of(
                "Lot|1 Main St|555-0100",
                "1|2001|Ford|Ranger|TRUCK|Yellow|10|100.00",
                "2|2002|2002|Honda|Civic|CAR|Gray|20|200.00"), lines);
    }

    @Test
    void savingKeepsThePricesTwoDecimalPlaces() throws Exception {
        Path csv = writeInventory("Lot|1 Main St|555-0100\n1|2001|Ford|Ranger|truck|Yellow|10|995.00\n");

        Dealership dealership = new DealershipFileManager(csv).getDealership();
        new DealershipFileManager(csv).saveDealership(dealership);

        assertEquals("995.00", Files.readAllLines(csv).get(1).split("\\|")[7]);
        assertEquals(2, new DealershipFileManager(csv).getDealership().getAllVehicles().get(0).getPrice().scale());
    }

    @Test
    void savingCreatesTheInventoryFileWhenItIsMissing() throws Exception {
        Path csv = tempDir.resolve("new-inventory.csv");

        new DealershipFileManager(csv).saveDealership(new Dealership(
                "New Lot",
                "9 Second St",
                "555-0199",
                List.of(new Vehicle("1", 2020, "Ford", "Ranger", VehicleType.TRUCK, "Blue", 5, new BigDecimal("500.00")))));

        Dealership reloaded = new DealershipFileManager(csv).getDealership();

        assertEquals("New Lot", reloaded.getName());
        assertEquals(1, reloaded.getAllVehicles().size());
    }

    @Test
    void savingAnEmptyLotLeavesOnlyTheHeaderLine() throws Exception {
        Path csv = writeInventory("Lot|1 Main St|555-0100\n1|2001|Ford|Ranger|truck|Yellow|10|100.00\n");

        Dealership dealership = new DealershipFileManager(csv).getDealership();
        dealership.getAllVehicles().clear();
        new DealershipFileManager(csv).saveDealership(dealership);

        assertEquals(List.of("Lot|1 Main St|555-0100"), Files.readAllLines(csv));
        assertTrue(new DealershipFileManager(csv).getDealership().getAllVehicles().isEmpty());
    }

    private Path writeInventory(String contents) throws Exception {
        Path csv = Files.createTempFile(tempDir, "inventory", ".csv");
        Files.writeString(csv, contents);
        return csv;
    }
}
