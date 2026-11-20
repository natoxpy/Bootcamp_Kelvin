package net.tarou.dealership;

import java.math.BigDecimal;

/**
 * A lease of a vehicle. A vehicle can only be leased while it is at most 3 years old
 * (current year minus model year), which the user interface enforces before a lease is built.
 *
 * <p>The lease is priced as an amortized loan: the customer effectively buys the vehicle and
 * sells it back at the expected ending value, and finances the difference plus a 7% lease fee
 * at 4.0% per year for 36 months.
 *
 * <p>Total price = expected ending value (50% of the vehicle price) + lease fee (7% of the
 * vehicle price).
 */
public class LeaseContract extends Contract {
    static final String TYPE_TOKEN = "LEASE";
    static final int TAIL_FIELD_COUNT = 4;

    static final BigDecimal EXPECTED_ENDING_VALUE_RATE = new BigDecimal("0.50");
    static final BigDecimal LEASE_FEE_RATE = new BigDecimal("0.07");
    static final BigDecimal LEASE_RATE = new BigDecimal("4.0");
    static final int LEASE_MONTHS = 36;

    /** A vehicle can be leased the year it is made and the three years after. */
    public static boolean canLease(Vehicle vehicle, int currentYear) {
        return currentYear - vehicle.getYear() <= 3;
    }

    public LeaseContract() {
        super();
    }

    public LeaseContract(String date, String customerName, String customerEmail, Vehicle vehicle) {
        super(date, customerName, customerEmail, vehicle);
    }

    public BigDecimal getExpectedEndingValue() {
        return percentOf(vehiclePrice(), EXPECTED_ENDING_VALUE_RATE);
    }

    public BigDecimal getLeaseFee() {
        return percentOf(vehiclePrice(), LEASE_FEE_RATE);
    }

    @Override
    public BigDecimal getTotalPrice() {
        return getExpectedEndingValue().add(getLeaseFee());
    }

    @Override
    public BigDecimal getMonthlyPayment() {
        // The amount actually financed is the vehicle price minus what the customer hands back
        // at the end of the lease; the lease fee rides on top of it.
        return monthlyPayment(vehiclePrice().subtract(getExpectedEndingValue()).add(getLeaseFee()),
                LEASE_RATE, LEASE_MONTHS);
    }

    String[] tailFields() {
        return new String[] {
            getExpectedEndingValue().toPlainString(),
            getLeaseFee().toPlainString(),
            getTotalPrice().toPlainString(),
            getMonthlyPayment().toPlainString(),
        };
    }

    @Override
    protected String contractTypeToken() {
        return TYPE_TOKEN;
    }

    private BigDecimal vehiclePrice() {
        BigDecimal price = getVehicle().getPrice();
        return price == null ? BigDecimal.ZERO : price;
    }
}
