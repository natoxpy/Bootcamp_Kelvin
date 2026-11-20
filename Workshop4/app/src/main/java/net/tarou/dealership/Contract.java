package net.tarou.dealership;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * A sale or lease agreement between the dealership and a customer.
 *
 * <p>Abstract because a contract is always one specific kind: {@link SalesContract} or
 * {@link LeaseContract}. The total price and the monthly payment are computed by the subclass
 * from the contract rules, so this class has no setter for them.
 */
public abstract class Contract {
    /** Common columns of a contract file line, before the contract-type-specific tail. */
    static final int COMMON_FIELD_COUNT = 12;

    private String date;
    private String customerName;
    private String customerEmail;
    private Vehicle vehicle;

    protected Contract() {
        this("", "", "", new Vehicle());
    }

    protected Contract(String date, String customerName, String customerEmail, Vehicle vehicle) {
        this.date = date;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.vehicle = vehicle;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public abstract BigDecimal getTotalPrice();

    public abstract BigDecimal getMonthlyPayment();

    protected String[] commonFields() {
        return new String[] {
            contractTypeToken(),
            date,
            customerName,
            customerEmail,
            vehicle.getVin(),
            String.valueOf(vehicle.getYear()),
            vehicle.getMake(),
            vehicle.getModel(),
            vehicle.getType() == null ? "" : vehicle.getType().name(),
            vehicle.getColor(),
            String.valueOf(vehicle.getOdometer()),
            vehicle.getPrice() == null ? "" : vehicle.getPrice().toPlainString(),
        };
    }

    protected abstract String contractTypeToken();

    /**
     * The payment for an amortized loan: a fixed monthly payment that pays the principal off
     * exactly after {@code months} payments at {@code annualRatePercent} per year, compounded
     * monthly. Rounded to cents.
     */
    protected static BigDecimal monthlyPayment(BigDecimal principal, BigDecimal annualRatePercent, int months) {
        if (principal == null || principal.signum() <= 0) {
            return new BigDecimal("0.00");
        }

        BigDecimal monthlyRate = annualRatePercent.divide(BigDecimal.valueOf(1200), MathContext.DECIMAL128);
        BigDecimal growth = BigDecimal.ONE.add(monthlyRate).pow(months);
        BigDecimal factor = monthlyRate.multiply(growth)
                .divide(growth.subtract(BigDecimal.ONE), MathContext.DECIMAL128);

        return principal.multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }

    /** Multiply by a percentage and round to cents, the way money amounts in this project are kept. */
    protected static BigDecimal percentOf(BigDecimal amount, BigDecimal percent) {
        return amount.multiply(percent).setScale(2, RoundingMode.HALF_UP);
    }
}
