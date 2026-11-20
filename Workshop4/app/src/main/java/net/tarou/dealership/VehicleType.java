package net.tarou.dealership;

public enum VehicleType {
    CAR,
    TRUCK,
    SUV,
    VAN;

    /** Maps loose spellings and synonyms ("suv", "pickup", "minivan") to a category, or null when nothing matches. */
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
