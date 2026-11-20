package net.tarou.dealership;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ContractDataManagerTest {
    private static final Vehicle EXPLORER =
            new Vehicle("10112", 1993, "Ford", "Explorer", VehicleType.SUV, "Red", 525123, new BigDecimal("995.00"));
    private static final Vehicle SILVERADO =
            new Vehicle("37846", 2021, "Chevrolet", "Silverado", VehicleType.TRUCK, "Black", 2750, new BigDecimal("31995.00"));

    @TempDir
    Path tempDir;

    @Test
    void savingAppendsOneLinePerContractAndCreatesTheFile() throws Exception {
        Path csv = tempDir.resolve("contracts.csv");
        ContractDataManager manager = new ContractDataManager(csv);

        manager.saveContract(new SalesContract("20210928", "Dana Wyatt", "dana@texas.com", EXPLORER, false));
        manager.saveContract(new LeaseContract("20210928", "Zachary Westly", "zach@texas.com", SILVERADO));

        List<String> lines = Files.readAllLines(csv);

        assertEquals(2, lines.size());
        assertEquals(List.of(
                "SALE|20210928|Dana Wyatt|dana@texas.com|10112|1993|Ford|Explorer|SUV|Red|525123|995.00"
                        + "|49.75|100.00|295.00|1439.75|NO|0.00|",
                "LEASE|20210928|Zachary Westly|zach@texas.com|37846|2021|Chevrolet|Silverado|TRUCK|Black|2750|31995.00"
                        + "|15997.50|2239.65|18237.15|538.43|"), lines);
    }

    @Test
    void savingKeepsWhatAlreadyWasInTheFile() throws Exception {
        Path csv = tempDir.resolve("contracts.csv");
        Files.writeString(csv, "SALE|20200101|Old Buyer|old@texas.com|9|2019|Ford|Ranger|TRUCK|Blue|10|100.00|5.00|100.00|295.00|500.00|NO|0.00|\n");

        ContractDataManager manager = new ContractDataManager(csv);
        manager.saveContract(new LeaseContract("20210928", "Zachary Westly", "zach@texas.com", SILVERADO));

        List<String> lines = Files.readAllLines(csv);

        assertEquals(2, lines.size());
        assertTrue(lines.get(0).startsWith("SALE|20200101"));
        assertTrue(lines.get(1).startsWith("LEASE|20210928"));
    }

    @Test
    void saleAddOnsAreWrittenLastAndReadBack() throws Exception {
        Path csv = tempDir.resolve("contracts.csv");
        ContractDataManager manager = new ContractDataManager(csv);

        SalesContract sale = new SalesContract("20210928", "Dana Wyatt", "dana@texas.com", EXPLORER, false);
        sale.addAddOn(new AddOn("Nitrogen tires", new BigDecimal("220.00")));
        sale.addAddOn(new AddOn("Wheel locks", new BigDecimal("85.00")));
        manager.saveContract(sale);

        String line = Files.readAllLines(csv).get(0);
        assertTrue(line.endsWith("|Nitrogen tires@220.00;Wheel locks@85.00"), line);

        SalesContract reloaded = (SalesContract) manager.getAllContracts().get(0);
        assertEquals(List.of("Nitrogen tires", "Wheel locks"),
                reloaded.getAddOns().stream().map(AddOn::getName).toList());
        assertEquals(0, reloaded.getSalesTax().compareTo(new BigDecimal("65.00")));
    }

    @Test
    void loadingReturnsContractsOfBothTypesWithTheirMathRebuilt() throws Exception {
        Path csv = tempDir.resolve("contracts.csv");
        ContractDataManager manager = new ContractDataManager(csv);

        manager.saveContract(new SalesContract("20210928", "Dana Wyatt", "dana@texas.com", EXPLORER, true));
        manager.saveContract(new LeaseContract("20210928", "Zachary Westly", "zach@texas.com", SILVERADO));

        List<Contract> contracts = manager.getAllContracts();

        assertEquals(2, contracts.size());

        SalesContract sale = (SalesContract) contracts.get(0);
        assertTrue(sale.isFinanced());
        assertEquals("Dana Wyatt", sale.getCustomerName());
        assertEquals("dana@texas.com", sale.getCustomerEmail());
        assertEquals("20210928", sale.getDate());
        assertEquals("10112", sale.getVehicle().getVin());
        assertEquals(VehicleType.SUV, sale.getVehicle().getType());
        assertEquals(0, sale.getMonthlyPayment().compareTo(new BigDecimal("63.33")));

        LeaseContract lease = (LeaseContract) contracts.get(1);
        assertEquals("37846", lease.getVehicle().getVin());
        assertEquals(0, lease.getTotalPrice().compareTo(new BigDecimal("18237.15")));
    }

    @Test
    void aMissingContractsFileLoadsAsNoContracts() throws Exception {
        List<Contract> contracts =
                new ContractDataManager(tempDir.resolve("nope.csv")).getAllContracts();

        assertTrue(contracts.isEmpty());
    }

    @Test
    void aLineThatIsNeitherSaleNorLeaseIsReportedWithItsLineNumber() throws Exception {
        Path csv = tempDir.resolve("contracts.csv");
        Files.writeString(csv, "WARRANTY|20210928|A|b@c|1|2020|Ford|Ranger|TRUCK|Blue|10|100.00|1|2|3|4|5|6|7\n");

        IllegalStateException problem = assertThrows(
                IllegalStateException.class, () -> new ContractDataManager(csv).getAllContracts());

        assertTrue(problem.getMessage().contains("Line 1"), problem.getMessage());
        assertTrue(problem.getMessage().contains("WARRANTY"), problem.getMessage());
    }

    @Test
    void aSaleLineThatIsTooShortIsReported() throws Exception {
        Path csv = tempDir.resolve("contracts.csv");
        Files.writeString(csv, "SALE|20210928|A|b@c|1|2020|Ford|Ranger|TRUCK|Blue|10|100.00|49.75|100.00\n");

        IllegalStateException problem = assertThrows(
                IllegalStateException.class, () -> new ContractDataManager(csv).getAllContracts());

        assertTrue(problem.getMessage().contains("too short"), problem.getMessage());
    }

    @Test
    void aMalformedAddOnTokenIsReported() throws Exception {
        Path csv = tempDir.resolve("contracts.csv");
        Files.writeString(csv, "SALE|20210928|A|b@c|1|2020|Ford|Ranger|TRUCK|Blue|10|100.00|5.00|100.00|295.00|500.00|NO|0.00|sunroof\n");

        IllegalStateException problem = assertThrows(
                IllegalStateException.class, () -> new ContractDataManager(csv).getAllContracts());

        assertTrue(problem.getMessage().contains("sunroof"), problem.getMessage());
    }

    @Test
    void anUnacceptableContractSubclassIsRefused() {
        Contract notARealContract = new Contract() {
            @Override
            public BigDecimal getTotalPrice() {
                return BigDecimal.ZERO;
            }

            @Override
            public BigDecimal getMonthlyPayment() {
                return BigDecimal.ZERO;
            }

            @Override
            protected String contractTypeToken() {
                return "ODD";
            }
        };

        assertThrows(
                IllegalArgumentException.class,
                () -> new ContractDataManager(tempDir.resolve("contracts.csv")).saveContract(notARealContract));
    }
}
