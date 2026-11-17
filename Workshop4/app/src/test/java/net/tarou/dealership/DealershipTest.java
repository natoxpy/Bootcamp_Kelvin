package net.tarou.dealership;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Searching, adding and removing vehicles on an in-memory dealership. Range searches are
 * exclusive at both ends, which is what these tests pin down.
 */
class DealershipTest {
    private static final BigDecimal PRICE_995 = new BigDecimal("995.00");
    private static final BigDecimal PRICE_2000 = new BigDecimal("2000.00");
    private static final BigDecimal PRICE_6995 = new BigDecimal("6995.00");
    private static final BigDecimal PRICE_19900 = new BigDecimal("19900.00");

    private Dealership dealership = new Dealership(
            "D & B Used Cars",
            "111 Old Benbrook Rd",
            "817-555-5555",
            List.of(
                    new Vehicle("10112", 1993, "Ford", "Explorer", VehicleType.SUV, "Red", 525123, PRICE_995),
                    new Vehicle("37846", 2001, "Ford", "Ranger", VehicleType.TRUCK, "Yellow", 172544, PRICE_2000),
                    new Vehicle("44901", 2012, "Honda", "Civic", VehicleType.CAR, "Gray", 103221, PRICE_6995),
                    new Vehicle("60877", 2016, "Toyota", "RAV4", VehicleType.SUV, "Blue", 61240, PRICE_19900)));

    @Test
    void priceRangeExcludesTheVehiclesPricedExactlyAtTheBounds() {
        assertEquals(List.of("37846"), vins(dealership.getVehiclesInPriceRange(PRICE_995, PRICE_6995)));
    }

    @Test
    void priceRangeIgnoresTrailingZerosInBigDecimal() {
        List<Vehicle> matches =
                dealership.getVehiclesInPriceRange(new BigDecimal("1999.99"), new BigDecimal("2000.01"));

        assertEquals(1, matches.size());
        assertEquals("37846", matches.get(0).getVin());
    }

    @Test
    void makeModelSearchIgnoresCaseButNeedsBothParts() {
        assertEquals(List.of("37846"), vins(dealership.getVehiclesByMakeModel("ford", "RANGER")));
        assertEquals(List.of("37846"), vins(dealership.getVehiclesByMakeModel(" Ford ", " Ranger ")));
        assertEquals(List.of("10112"), vins(dealership.getVehiclesByMakeModel("Ford", "Explorer")));
        assertTrue(dealership.getVehiclesByMakeModel("Honda", "Ranger").isEmpty());
    }

    @Test
    void makeModelSearchMatchesEveryVehicleWithThatMakeAndModel() {
        dealership.addVehicle(new Vehicle("99999", 2001, "Ford", "Ranger", VehicleType.TRUCK, "Black", 10, PRICE_995));

        assertEquals(List.of("37846", "99999"), vins(dealership.getVehiclesByMakeModel("Ford", "Ranger")));
    }

    @Test
    void yearRangeExcludesTheBounds() {
        assertEquals(List.of("37846"), vins(dealership.getVehiclesByYear(1993, 2012)));
        assertTrue(dealership.getVehiclesByYear(2016, 1993).isEmpty(), "reversed bounds match nothing");
    }

    @Test
    void colorSearchIgnoresCaseAndSurroundingSpaces() {
        assertEquals(List.of("10112"), vins(dealership.getVehiclesByColor("red")));
        assertEquals(List.of("10112"), vins(dealership.getVehiclesByColor(" RED ")));
        assertTrue(dealership.getVehiclesByColor("Purple").isEmpty());
    }

    @Test
    void mileageRangeExcludesTheBounds() {
        assertEquals(List.of("44901"), vins(dealership.getVehiclesInMileageRange(61240, 172544)));
    }

    @Test
    void typeSearchMatchesEveryVehicleOfThatType() {
        assertEquals(List.of("10112", "60877"), vins(dealership.getVehiclesByType(VehicleType.SUV)));
        assertEquals(List.of("37846"), vins(dealership.getVehiclesByType(VehicleType.TRUCK)));
        assertTrue(dealership.getVehiclesByType(VehicleType.VAN).isEmpty());
    }

    @Test
    void searchesOnAnEmptyLotReturnEmptyListsRatherThanNull() {
        Dealership empty = new Dealership();

        assertNotNull(empty.getVehiclesInPriceRange(PRICE_995, PRICE_19900));
        assertTrue(empty.getVehiclesInPriceRange(PRICE_995, PRICE_19900).isEmpty());
        assertTrue(empty.getVehiclesByMakeModel("Ford", "Ranger").isEmpty());
        assertTrue(empty.getVehiclesByYear(1990, 2020).isEmpty());
        assertTrue(empty.getVehiclesByColor("Red").isEmpty());
        assertTrue(empty.getVehiclesInMileageRange(0, 300000).isEmpty());
        assertTrue(empty.getVehiclesByType(VehicleType.CAR).isEmpty());
    }

    @Test
    void addingAVehiclePutsItAtTheEndOfTheInventory() {
        Vehicle newcomer =
                new Vehicle("71345", 2004, "Honda", "Odyssey", VehicleType.VAN, "White", 188300, new BigDecimal("8750.00"));

        dealership.addVehicle(newcomer);

        List<String> vins = vins(dealership.getAllVehicles());
        assertEquals(5, vins.size());
        assertEquals("71345", vins.get(4));
    }

    @Test
    void addingNullChangesNothing() {
        dealership.addVehicle(null);

        assertEquals(4, dealership.getAllVehicles().size());
    }

    @Test
    void removingByAVehicleThatOnlyCarriesTheVinTakesTheRightVehicleOffTheLot() {
        Vehicle justAVin = new Vehicle();
        justAVin.setVin("37846");

        assertTrue(dealership.removeVehicle(justAVin));

        assertEquals(List.of("10112", "44901", "60877"), vins(dealership.getAllVehicles()));
        assertEquals("D & B Used Cars", dealership.getName());
    }

    @Test
    void removingAVehicleTheLotDoesNotHaveReportsFailure() {
        assertFalse(dealership.removeVehicle(new Vehicle("nope", 2020, "Ford", "Focus", VehicleType.CAR, "Blue", 1, PRICE_995)));

        assertEquals(4, dealership.getAllVehicles().size());
    }

    @Test
    void findVehicleByVinIgnoresCaseAndReportsUnknownVins() {
        Vehicle found = dealership.findVehicleByVin("10112");

        assertNotNull(found);
        assertEquals("Explorer", found.getModel());
        assertNull(dealership.findVehicleByVin("does-not-exist"));
        assertNull(dealership.findVehicleByVin(null));
    }

    @Test
    void vehiclesKeepTheirFieldsAfterBeingStoredInADealership() {
        Vehicle explorer = dealership.getAllVehicles().get(0);

        assertEquals("10112", explorer.getVin());
        assertEquals(1993, explorer.getYear());
        assertEquals("Ford", explorer.getMake());
        assertEquals("Explorer", explorer.getModel());
        assertEquals(VehicleType.SUV, explorer.getType());
        assertEquals("Red", explorer.getColor());
        assertEquals(525123, explorer.getOdometer());
        assertEquals(0, explorer.getPrice().compareTo(PRICE_995));
    }

    @Test
    void vehicleToStringIsThePipeDelimitedRecordLine() {
        assertEquals(
                "10112|1993|Ford|Explorer|SUV|Red|525123|995.00",
                dealership.getAllVehicles().get(0).toString());
    }

    @Test
    void vehicleTypeAcceptsCommonSpellingsInAnyCase() {
        assertEquals(VehicleType.CAR, VehicleType.fromString("Car"));
        assertEquals(VehicleType.CAR, VehicleType.fromString("auto"));
        assertEquals(VehicleType.TRUCK, VehicleType.fromString(" PICKUP "));
        assertEquals(VehicleType.SUV, VehicleType.fromString("sport utility"));
        assertEquals(VehicleType.VAN, VehicleType.fromString("minivan"));
        assertNull(VehicleType.fromString("spaceship"));
        assertNull(VehicleType.fromString(null));
    }

    private List<String> vins(List<Vehicle> vehicles) {
        return vehicles.stream().map(Vehicle::getVin).toList();
    }
}
