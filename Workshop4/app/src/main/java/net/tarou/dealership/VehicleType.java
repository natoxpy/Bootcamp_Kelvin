package net.tarou.dealership;

/**
 * The vehicle categories the dealership stocks.
 *
 * The workshop menu searches by four categories (car, truck, SUV, van), so the enum stays
 * small and the inventory file always stores one of these canonical names. Anything the user
 * types is funnelled through {@link #fromString(String)} so "suv", "SUV" and "S u v" all work.
 */
public enum VehicleType {
    CAR,
    TRUCK,
    SUV,
    VAN;

    /**
     * Converts free-form user input (or a column read from the inventory file) into a
     * {@code VehicleType}.
     *
     * @param value text such as "car", "Pickup", "minivan" or "SUV"
     * @return the matching type, or {@code null} when the text matches no category
     */
    public static VehicleType fromString(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim().toLowerCase().replaceAll("[^a-z]", "");

        switch (normalized) {
            case "car":
            case "auto":
            case "automobile":
                return CAR;
            case "truck":
            case "pickup":
            case "pickups":
                return TRUCK;
            case "suv":
            case "sportutility":
                return SUV;
            case "van":
            case "minivan":
                return VAN;
            default:
                return null;
        }
    }
}
