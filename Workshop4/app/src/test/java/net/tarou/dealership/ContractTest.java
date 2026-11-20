package net.tarou.dealership;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ContractTest {
    private static final Vehicle EXPLORER =
            new Vehicle("10112", 1993, "Ford", "Explorer", VehicleType.SUV, "Red", 525123, new BigDecimal("995.00"));
    private static final Vehicle SILVERADO =
            new Vehicle("37846", 2026, "Chevrolet", "Silverado", VehicleType.TRUCK, "Black", 2750, new BigDecimal("31995.00"));
    private static final Vehicle TRUCK_12300 =
            new Vehicle("55211", 2026, "Ford", "F-150", VehicleType.TRUCK, "Blue", 50000, new BigDecimal("12300.00"));

    @Test
    void unpaidSaleMatchesTheWorkbookExample() {
        SalesContract sale = new SalesContract("20210928", "Dana Wyatt", "dana@texas.com", EXPLORER, false);

        assertEquals(0, sale.getSalesTax().compareTo(new BigDecimal("49.75")));
        assertEquals(0, sale.getRecordingFee().compareTo(new BigDecimal("100.00")));
        assertEquals(0, sale.getProcessingFee().compareTo(new BigDecimal("295.00")));
        assertEquals(0, sale.getTotalPrice().compareTo(new BigDecimal("1439.75")));
        assertEquals(0, sale.getMonthlyPayment().compareTo(new BigDecimal("0.00")));
    }

    @Test
    void financedSaleUnderTenThousandUses525PercentFor24Months() {
        SalesContract sale = new SalesContract("20210928", "Dana Wyatt", "dana@texas.com", EXPLORER, true);

        assertEquals(0, sale.getMonthlyPayment().compareTo(new BigDecimal("63.33")));
    }

    @Test
    void financedSaleOverTenThousandUses425PercentFor48MonthsAndThe495Fee() {
        SalesContract sale = new SalesContract("20210928", "Ann Peak", "ann@texas.com", TRUCK_12300, true);

        assertEquals(0, sale.getProcessingFee().compareTo(new BigDecimal("495.00")));
        assertEquals(0, sale.getTotalPrice().compareTo(new BigDecimal("13510.00")));
        assertEquals(0, sale.getMonthlyPayment().compareTo(new BigDecimal("306.56")));
    }

    @Test
    void exactlyTenThousandCountsAsTheUpperTier() {
        Vehicle exactly10000 = new Vehicle(
                "1", 2026, "Ford", "Ranger", VehicleType.TRUCK, "Blue", 10, new BigDecimal("10000.00"));
        SalesContract sale = new SalesContract("20210928", "Ann Peak", "ann@texas.com", exactly10000, true);

        assertEquals(0, sale.getProcessingFee().compareTo(new BigDecimal("495.00")));
        assertEquals(0, sale.getTotalPrice().compareTo(new BigDecimal("11095.00")));
        assertEquals(0, sale.getMonthlyPayment().compareTo(new BigDecimal("251.76")));
    }

    @Test
    void addOnsAreTaxedAndLiftTheTotal() {
        SalesContract sale = new SalesContract("20210928", "Dana Wyatt", "dana@texas.com", EXPLORER, false);
        sale.addAddOn(new AddOn("Nitrogen tires", new BigDecimal("220.00")));
        sale.addAddOn(new AddOn("Wheel locks", new BigDecimal("85.00")));

        assertEquals(2, sale.getAddOns().size());
        assertEquals(0, sale.getAddOnsTotal().compareTo(new BigDecimal("305.00")));
        assertEquals(0, sale.getSalesTax().compareTo(new BigDecimal("65.00"))); // 5% of 995 + 305
        assertEquals(0, sale.getTotalPrice().compareTo(new BigDecimal("1760.00")));
    }

    @Test
    void financedSaleWithAddOnsPricesTheLoanOnTheWholeTotal() {
        SalesContract sale = new SalesContract("20210928", "Dana Wyatt", "dana@texas.com", EXPLORER, true);
        sale.addAddOn(new AddOn("Nitrogen tires", new BigDecimal("220.00")));
        sale.addAddOn(new AddOn("Wheel locks", new BigDecimal("85.00")));

        assertEquals(0, sale.getMonthlyPayment().compareTo(new BigDecimal("77.41")));
    }

    @Test
    void leaseMatchesTheWorkbookExample() {
        LeaseContract lease = new LeaseContract("20210928", "Zachary Westly", "zach@texas.com", SILVERADO);

        assertEquals(0, lease.getExpectedEndingValue().compareTo(new BigDecimal("15997.50")));
        assertEquals(0, lease.getLeaseFee().compareTo(new BigDecimal("2239.65")));
        assertEquals(0, lease.getTotalPrice().compareTo(new BigDecimal("18237.15")));
        assertEquals(0, lease.getMonthlyPayment().compareTo(new BigDecimal("538.43")));
    }

    @Test
    void leaseGettersFollowTheVehicleAfterSetVehicle() {
        LeaseContract lease = new LeaseContract();
        lease.setVehicle(SILVERADO);

        assertEquals(0, lease.getExpectedEndingValue().compareTo(new BigDecimal("15997.50")));
    }

    @Test
    void onlyVehiclesAtMostThreeYearsOldCanBeLeased() {
        Vehicle newCar = new Vehicle("1", 2024, "Ford", "Ranger", VehicleType.TRUCK, "Blue", 1, new BigDecimal("30000.00"));
        Vehicle threeYearsOld = new Vehicle("2", 2023, "Ford", "Ranger", VehicleType.TRUCK, "Blue", 1, new BigDecimal("30000.00"));
        Vehicle fourYearsOld = new Vehicle("3", 2022, "Ford", "Ranger", VehicleType.TRUCK, "Blue", 1, new BigDecimal("30000.00"));

        assertTrue(LeaseContract.canLease(newCar, 2026));
        assertTrue(LeaseContract.canLease(threeYearsOld, 2026));
        assertFalse(LeaseContract.canLease(fourYearsOld, 2026));
        assertFalse(LeaseContract.canLease(EXPLORER, 2026));
    }
}
