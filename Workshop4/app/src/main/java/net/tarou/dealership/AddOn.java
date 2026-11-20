package net.tarou.dealership;

import java.math.BigDecimal;
import java.util.List;

/**
 * An optional extra the buyer can bolt onto a sale: a name and a fixed price.
 *
 * <p>In a contract file line the add-ons of a sale are one trailing column of
 * {@code name@price} pairs separated by semicolons, for example
 * {@code Nitrogen tires@220.00;Wheel locks@85.00}.
 */
public final class AddOn {
    private static final List<AddOn> CATALOG = List.of(
            new AddOn("Nitrogen tires", new BigDecimal("220.00")),
            new AddOn("All-season floor mats", new BigDecimal("120.00")),
            new AddOn("Cargo tray", new BigDecimal("150.00")),
            new AddOn("Window tinting", new BigDecimal("175.00")),
            new AddOn("Splash guards", new BigDecimal("95.00")),
            new AddOn("Wheel locks", new BigDecimal("85.00")));

    private final String name;
    private final BigDecimal price;

    public AddOn(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
    }

    public static List<AddOn> catalog() {
        return CATALOG;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    /** Parse one {@code name@price} pair; null when it is malformed. */
    public static AddOn fromToken(String token) {
        String[] parts = token.trim().split("@");

        if (parts.length != 2) {
            return null;
        }

        try {
            return new AddOn(parts[0].trim(), new BigDecimal(parts[1].trim()));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AddOn otherAddOn)) {
            return false;
        }
        return name != null && name.equalsIgnoreCase(otherAddOn.name);
    }

    @Override
    public int hashCode() {
        return name == null ? 0 : name.toUpperCase().hashCode();
    }

    @Override
    public String toString() {
        return name + "@" + price.toPlainString();
    }
}
