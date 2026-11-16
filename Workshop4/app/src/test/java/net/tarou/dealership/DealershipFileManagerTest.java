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

/**
 * Reading the pipe-delimited inventory file. The round-trip (save then load) is added in Phase 5,
 * when {@code saveDealership()} gets its body.
 */
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

    private Path writeInventory(String contents) throws Exception {
        Path csv = Files.createTempFile(tempDir, "inventory", ".csv");
        Files.writeString(csv, contents);
        return csv;
    }
}
