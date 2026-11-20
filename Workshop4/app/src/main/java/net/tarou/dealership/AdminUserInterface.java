package net.tarou.dealership;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

/**
 * The password-gated side menu for looking at recorded contracts.
 *
 * <p>{@link UserInterface} only hands control here after the password check, so this class is
 * just the contract browsing loop: list all contracts, or the last ten.
 */
public class AdminUserInterface {
    static final String PASSWORD = "admin";

    private static final int ALL_CONTRACTS = 1;
    private static final int LAST_TEN = 2;
    private static final int BACK = 99;
    private static final int RECENT_COUNT = 10;

    private final ContractDataManager contractManager;
    private final Scanner keyboard;

    public AdminUserInterface(ContractDataManager contractManager, Scanner keyboard) {
        this.contractManager = contractManager;
        this.keyboard = keyboard;
    }

    public static boolean acceptsPassword(String entry) {
        return PASSWORD.equals(entry);
    }

    public void display() {
        boolean staying = true;

        while (staying) {
            System.out.println();
            System.out.println("=== Admin ===");
            System.out.println("1 - List ALL contracts");
            System.out.println("2 - List the last 10 contracts");
            System.out.println("99 - Back to the dealership menu");
            System.out.print("Choose an option: ");

            String entry = keyboard.hasNextLine() ? keyboard.nextLine().trim() : null;

            if (entry == null) {
                return;
            }

            int command;
            try {
                command = Integer.parseInt(entry);
            } catch (NumberFormatException e) {
                System.out.println("That is not a menu option. Pick 1, 2, or 99.");
                continue;
            }

            switch (command) {
                case ALL_CONTRACTS:
                    displayContracts(readContracts(), "Contracts");
                    break;
                case LAST_TEN:
                    displayContracts(readRecentContracts(), "Last " + RECENT_COUNT + " contracts");
                    break;
                case BACK:
                    staying = false;
                    break;
                default:
                    System.out.println("That is not a menu option. Pick 1, 2, or 99.");
                    break;
            }
        }
    }

    private List<Contract> readContracts() {
        try {
            return contractManager.getAllContracts();
        } catch (FileNotFoundException e) {
            System.out.println(
                    "Could not open " + contractManager.getContractsFile().toAbsolutePath() + ": " + e.getMessage());
            return List.of();
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
            return List.of();
        }
    }

    private List<Contract> readRecentContracts() {
        List<Contract> contracts = readContracts();

        if (contracts.size() <= RECENT_COUNT) {
            return contracts;
        }

        return contracts.subList(contracts.size() - RECENT_COUNT, contracts.size());
    }

    private void displayContracts(List<Contract> contracts, String heading) {
        if (contracts.isEmpty()) {
            System.out.println("No contracts recorded yet.");
            return;
        }

        System.out.println();
        System.out.println(heading + ":");
        System.out.printf(
                "%-6s %-10s %-20s %-8s %-5s %-12s %-16s %12s %10s%n",
                "TYPE", "DATE", "CUSTOMER", "VIN", "YEAR", "MAKE", "MODEL", "TOTAL", "MONTHLY");

        for (Contract contract : contracts) {
            Vehicle vehicle = contract.getVehicle();

            System.out.printf(
                    "%-6s %-10s %-20s %-8s %-5d %-12s %-16s %12s %10s%n",
                    contract instanceof SalesContract ? "SALE" : "LEASE",
                    contract.getDate(),
                    contract.getCustomerName(),
                    vehicle.getVin(),
                    vehicle.getYear(),
                    vehicle.getMake(),
                    vehicle.getModel(),
                    contract.getTotalPrice().toPlainString(),
                    contract.getMonthlyPayment().toPlainString());
        }

        System.out.println();
        System.out.println(contracts.size() + " contract(s).");
    }
}
