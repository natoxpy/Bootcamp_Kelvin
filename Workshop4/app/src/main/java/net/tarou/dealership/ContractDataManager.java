package net.tarou.dealership;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Appends contracts to, and reads contracts back from, a pipe-delimited contracts file.
 *
 * <p>Every line is one contract. The first 12 columns are common to both kinds and the tail
 * depends on the contract type, which is why the first column says {@code SALE} or {@code LEASE}:
 *
 * <pre>
 * SALE|20210928|Dana Wyatt|dana@texas.com|10112|1993|Ford|Explorer|SUV|Red|525123|995.00|49.75|100.00|295.00|1439.75|NO|0.00|Nitrogen tires@220.00
 * LEASE|20210928|Zachary Westly|zach@texas.com|37846|2021|Chevrolet|Silverado|TRUCK|Black|2750|31995.00|15997.50|2239.65|18237.15|538.43|
 * </pre>
 *
 * <pre>
 * common:   TYPE|DATE|NAME|EMAIL|VIN|YEAR|MAKE|MODEL|VEHICLE_TYPE|COLOR|ODOMETER|VEHICLE_PRICE
 * SALE:     SALES_TAX|RECORDING_FEE|PROCESSING_FEE|TOTAL_PRICE|FINANCE_OPTION|MONTHLY_PAYMENT|ADDONS
 * LEASE:    EXPECTED_ENDING_VALUE|LEASE_FEE|TOTAL_PRICE|MONTHLY_PAYMENT|ADDONS
 * </pre>
 *
 * Blank lines are ignored. The add-ons column is always written last and is empty when there
 * are none.
 */
public class ContractDataManager {
    private static final Path DEFAULT_CONTRACTS_FILE = Path.of("data", "contracts.csv");

    private final Path contractsFile;

    public ContractDataManager() {
        this(DEFAULT_CONTRACTS_FILE);
    }

    public ContractDataManager(Path contractsFile) {
        this.contractsFile = contractsFile;
    }

    public Path getContractsFile() {
        return contractsFile;
    }

    public void saveContract(Contract contract) throws FileNotFoundException {
        String[] fields;

        if (contract instanceof SalesContract sale) {
            fields = concat(sale.commonFields(), sale.tailFields(), addOnsField(sale.getAddOns()));
        } else if (contract instanceof LeaseContract lease) {
            fields = concat(lease.commonFields(), lease.tailFields(), "");
        } else {
            throw new IllegalArgumentException(
                    "Cannot save a contract of type " + contract.getClass().getSimpleName());
        }

        try (PrintStream writer = new PrintStream(new FileOutputStream(contractsFile.toFile(), true))) {
            writer.println(String.join("|", fields));
        }
    }

    /** Read every contract back; an empty list when the file does not exist yet. */
    public List<Contract> getAllContracts() throws FileNotFoundException {
        List<Contract> contracts = new ArrayList<>();

        if (!Files.exists(contractsFile)) {
            return contracts;
        }

        try (Scanner reader = new Scanner(contractsFile.toFile())) {
            int lineNumber = 0;

            while (reader.hasNextLine()) {
                lineNumber++;
                String line = reader.nextLine();

                if (line.isBlank()) {
                    continue;
                }

                contracts.add(parseContractLine(line, lineNumber));
            }
        }

        return contracts;
    }

    private Contract parseContractLine(String line, int lineNumber) {
        String[] fields = line.split("\\|", -1);
        String type = fields[0].trim();

        int neededTail = SalesContract.TYPE_TOKEN.equalsIgnoreCase(type)
                ? SalesContract.TAIL_FIELD_COUNT
                : LeaseContract.TAIL_FIELD_COUNT;

        if (fields.length < Contract.COMMON_FIELD_COUNT + neededTail) {
            throw new IllegalStateException(
                    "Line "
                            + lineNumber
                            + " of "
                            + contractsFile.toAbsolutePath()
                            + " is too short to be a "
                            + type
                            + " contract, but was: "
                            + line);
        }

        Vehicle vehicle = parseVehicle(fields, lineNumber, line);
        String date = fields[1].trim();
        String name = fields[2].trim();
        String email = fields[3].trim();

        if (SalesContract.TYPE_TOKEN.equalsIgnoreCase(type)) {
            SalesContract contract = new SalesContract(
                    date, name, email, vehicle, "YES".equalsIgnoreCase(fields[16].trim()));

            // The add-ons column is last and may be missing or empty on a sale without extras.
            String addOnsColumn = fields.length > 18 ? fields[18].trim() : "";

            for (String token : addOnsColumn.split(";")) {
                if (!token.isBlank()) {
                    AddOn addOn = AddOn.fromToken(token);

                    if (addOn == null) {
                        throw new IllegalStateException(
                                "Line " + lineNumber + " has add-on \"" + token + "\", which is not name@price: " + line);
                    }

                    contract.addAddOn(addOn);
                }
            }

            return contract;
        }

        if (LeaseContract.TYPE_TOKEN.equalsIgnoreCase(type)) {
            return new LeaseContract(date, name, email, vehicle);
        }

        throw new IllegalStateException(
                "Line "
                        + lineNumber
                        + " starts with \""
                        + type
                        + "\", which is neither SALE nor LEASE: "
                        + line);
    }

    private Vehicle parseVehicle(String[] fields, int lineNumber, String line) {
        VehicleType type = VehicleType.fromString(fields[8].trim());

        if (type == null) {
            throw new IllegalStateException(
                    "Line "
                            + lineNumber
                            + " has vehicle type \""
                            + fields[8].trim()
                            + "\", which is not car, truck, SUV or van: "
                            + line);
        }

        try {
            return new Vehicle(
                    fields[4].trim(),
                    Integer.parseInt(fields[5].trim()),
                    fields[6].trim(),
                    fields[7].trim(),
                    type,
                    fields[9].trim(),
                    Integer.parseInt(fields[10].trim()),
                    new BigDecimal(fields[11].trim()));
        } catch (NumberFormatException | ArithmeticException e) {
            throw new IllegalStateException(
                    "Line " + lineNumber + " of " + contractsFile.toAbsolutePath()
                            + " has a vehicle column that is not a number: " + line,
                    e);
        }
    }

    private String addOnsField(List<AddOn> addOns) {
        List<String> tokens = new ArrayList<>();

        for (AddOn addOn : addOns) {
            tokens.add(addOn.toString());
        }

        return String.join(";", tokens);
    }

    private String[] concat(String[] first, String[] second, String last) {
        List<String> all = new ArrayList<>();

        for (String field : first) {
            all.add(field);
        }

        for (String field : second) {
            all.add(field);
        }

        all.add(last);

        return all.toArray(new String[0]);
    }
}
