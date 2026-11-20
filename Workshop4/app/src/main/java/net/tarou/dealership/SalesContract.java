package net.tarou.dealership;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * A plain purchase of a vehicle.
 *
 * <p>The customer pays the vehicle price plus 5% sales tax (on the vehicle and any add-ons),
 * a $100 recording fee, and a processing fee of $295 for vehicles under $10,000 or $495 for
 * everything else. Both fee tiers key off the vehicle price, not the contract total.
 *
 * <p>If they finance, the whole total is an amortized loan: 4.25% for 48 months when the
 * vehicle price is $10,000 or more, 5.25% for 24 months otherwise. Not financing means a
 * monthly payment of 0.00.
 */
public class SalesContract extends Contract {
    static final String TYPE_TOKEN = "SALE";
    static final int TAIL_FIELD_COUNT = 6;

    static final BigDecimal SALES_TAX_RATE = new BigDecimal("0.05");
    static final BigDecimal RECORDING_FEE = new BigDecimal("100.00");
    static final BigDecimal LOW_PROCESSING_FEE = new BigDecimal("295.00");
    static final BigDecimal HIGH_PROCESSING_FEE = new BigDecimal("495.00");
    static final BigDecimal PROCESSING_TIER = new BigDecimal("10000");

    static final BigDecimal LOW_PRICE_RATE = new BigDecimal("5.25");
    static final int LOW_PRICE_MONTHS = 24;
    static final BigDecimal HIGH_PRICE_RATE = new BigDecimal("4.25");
    static final int HIGH_PRICE_MONTHS = 48;

    private boolean financed;
    private final List<AddOn> addOns = new ArrayList<>();

    public SalesContract() {
        super();
    }

    public SalesContract(String date, String customerName, String customerEmail, Vehicle vehicle, boolean financed) {
        super(date, customerName, customerEmail, vehicle);
        this.financed = financed;
    }

    public boolean isFinanced() {
        return financed;
    }

    public void setFinanced(boolean financed) {
        this.financed = financed;
    }

    public List<AddOn> getAddOns() {
        return addOns;
    }

    public void addAddOn(AddOn addOn) {
        if (addOn != null && !addOns.contains(addOn)) {
            addOns.add(addOn);
        }
    }

    public BigDecimal getSalesTax() {
        return percentOf(taxableAmount(), SALES_TAX_RATE);
    }

    public BigDecimal getRecordingFee() {
        return RECORDING_FEE;
    }

    public BigDecimal getProcessingFee() {
        return vehiclePrice().compareTo(PROCESSING_TIER) < 0 ? LOW_PROCESSING_FEE : HIGH_PROCESSING_FEE;
    }

    public BigDecimal getAddOnsTotal() {
        BigDecimal total = BigDecimal.ZERO;

        for (AddOn addOn : addOns) {
            total = total.add(addOn.getPrice());
        }

        return total.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getTotalPrice() {
        return taxableAmount().add(getSalesTax()).add(RECORDING_FEE).add(getProcessingFee());
    }

    @Override
    public BigDecimal getMonthlyPayment() {
        if (!financed) {
            return new BigDecimal("0.00");
        }

        if (vehiclePrice().compareTo(PROCESSING_TIER) >= 0) {
            return monthlyPayment(getTotalPrice(), HIGH_PRICE_RATE, HIGH_PRICE_MONTHS);
        }

        return monthlyPayment(getTotalPrice(), LOW_PRICE_RATE, LOW_PRICE_MONTHS);
    }

    String[] tailFields() {
        return new String[] {
            getSalesTax().toPlainString(),
            getRecordingFee().toPlainString(),
            getProcessingFee().toPlainString(),
            getTotalPrice().toPlainString(),
            financed ? "YES" : "NO",
            getMonthlyPayment().toPlainString(),
        };
    }

    @Override
    protected String contractTypeToken() {
        return TYPE_TOKEN;
    }

    private BigDecimal taxableAmount() {
        return vehiclePrice().add(getAddOnsTotal());
    }

    private BigDecimal vehiclePrice() {
        BigDecimal price = getVehicle().getPrice();
        return price == null ? BigDecimal.ZERO : price;
    }
}
