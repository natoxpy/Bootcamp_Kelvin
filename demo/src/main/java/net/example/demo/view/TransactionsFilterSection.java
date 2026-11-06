package net.example.demo.view;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import net.example.demo.component.InputDatetimeComponent;
import net.example.demo.component.InputDatetimeComponent.PartialDateTime;
import net.example.demo.component.TransactionTable;
import net.example.demo.data.Database;
import net.example.demo.data.Transaction;
import net.gui.commands.BoxStyle;
import net.gui.primitives.AlignSelf;
import net.gui.primitives.Direction;
import net.gui.primitives.JustifySelf;
import net.rosu.component.BoxComponent;
import net.rosu.component.Component;
import net.rosu.component.FlexComponent;
import net.rosu.component.FlexItem;
import net.rosu.component.InputComponent;
import net.rosu.component.Renderable;
import net.rosu.component.TextComponent;

public class TransactionsFilterSection extends Component {
    private static final Pattern PARTIAL_NUMBER = Pattern.compile("^-?\\d*\\.?\\d*$");
    private Database db;
    private TransactionTable tt;
    private PartialDateTime ptFrom = new PartialDateTime(null, null, null, null, null);
    private PartialDateTime ptTo = new PartialDateTime(null, null, null, null, null);
    private String vendor = null;
    private String description = null;
    private BigDecimal minMoney = null;
    private BigDecimal maxMoney = null;

    public TransactionsFilterSection(Database db, TransactionTable tt) {
        this.db = db;
        this.tt = tt;
    }

    @Override
    public Renderable render() {
        var flex = new FlexComponent();
        var vendorAndAmountFilter = new FlexComponent().setDirection(Direction.Column);

        vendorAndAmountFilter.addChild(new FlexItem(this.filterByVendor(), 0));
        vendorAndAmountFilter.addChild(new FlexItem(this.filterByAmount(), 0));

        flex.addChild(new FlexItem(this.filterByDate(), 2));
        flex.addChild(new FlexItem(vendorAndAmountFilter, 3));

        this.performFiltrationOperation();

        return flex;
    }

    private int levenshtein(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                dp[i][j] =
                    Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }
        return dp[a.length()][b.length()];
    }

    private boolean fuzzyMatches(String vendor, String query, int maxDistance) {
        String[] vendorWords = vendor.toLowerCase().split("\\s+");
        String[] queryWords = query.toLowerCase().split("\\s+");

        for (String qWord : queryWords) {
            boolean matchedThisWord = false;

            for (String vWord : vendorWords) {
                if (vWord.contains(qWord) || levenshtein(vWord, qWord) <= maxDistance) {
                    matchedThisWord = true;
                    break;
                }
            }

            if (!matchedThisWord)
                return false; // every query word must match something in the vendor name
        }

        return true;
    }

    private Renderable inputDatatimeField(String txt, Consumer<PartialDateTime> event) {
        var flex = new FlexComponent();
        var input = new InputDatetimeComponent().onPartialDt(event);

        flex.addChild(
            new FlexItem(new TextComponent(txt + " "), JustifySelf.End, AlignSelf.Start, 1));
        flex.addChild(new FlexItem(input, 5));

        return flex;
    }

    private Renderable inputField(String txt, String placeholder, Consumer<String> event) {
        var flex = new FlexComponent();
        var input = new InputComponent(event)
                        .setPlaceholder(placeholder)
                        .setValidator((s) -> s.length() < placeholder.length());

        flex.addChild(
            new FlexItem(new TextComponent(txt + " "), JustifySelf.End, AlignSelf.Start, 1));
        flex.addChild(new FlexItem(input, 5));

        return flex;
    }

    private Renderable inputNumberField(String txt, String placeholder, Consumer<String> event) {
        var flex = new FlexComponent();
        var input =
            new InputComponent(event)
                .setRegex("[0-9\\.\\-]+")
                .setPlaceholder(placeholder)
                .setValidator((s) -> s.length() < placeholder.length() && isPartialNumber(s));

        flex.addChild(
            new FlexItem(new TextComponent(txt + " "), JustifySelf.End, AlignSelf.Start, 1));
        flex.addChild(new FlexItem(input, 5));

        return flex;
    }

    private boolean isPartialNumber(String s) {
        if (s.isEmpty())
            return true; // empty is always a valid in-progress state
        return PARTIAL_NUMBER.matcher(s).matches();
    }

    private Renderable filterByDate() {
        var flex = new FlexComponent().setDirection(Direction.Column);
        var fromInput = this.inputDatatimeField("From", (pt) -> {
            this.ptFrom = pt;
            this.performFiltrationOperation();
        });
        var toInput = this.inputDatatimeField("To", (pt) -> {
            this.ptTo = pt;
            this.performFiltrationOperation();
        });

        flex.addChild(new FlexItem(fromInput, 1));
        flex.addChild(new FlexItem(toInput, 1));

        return new BoxComponent(flex).setStyle(new BoxStyle(null));
    }

    private Renderable filterByVendor() {
        var flex = new FlexComponent().setDirection(Direction.Row);
        var vendorInput = this.inputField("Vendor", "_________________", (i) -> {
            this.vendor = i.trim().equals("") ? null : i;
            this.performFiltrationOperation();
        });
        var minMoneyInput = this.inputNumberField("Min $", "_________", (i) -> {
            if (i == null || i.isBlank()) {
                this.minMoney = null;
            } else {
                try {
                    this.minMoney = new BigDecimal(i);
                } catch (NumberFormatException e) {
                    this.minMoney = null;
					return;
                }
            }

            performFiltrationOperation();
        });

        flex.addChild(new FlexItem(vendorInput, 3));
        flex.addChild(new FlexItem(minMoneyInput, 1));

        return new BoxComponent(flex).setStyle(new BoxStyle(null));
    }

    private Renderable filterByAmount() {
        var flex = new FlexComponent().setDirection(Direction.Row);
        var descriptionInput = this.inputField("Description", "_________________", (i) -> {
            this.description = i.trim().equals("") ? null : i;
            this.performFiltrationOperation();
        });
        var maxMoneyInput = this.inputNumberField("Max $", "_________", (i) -> {
            if (i == null || i.isBlank()) {
                this.maxMoney = null;
            } else {
                try {
                    this.maxMoney = new BigDecimal(i);
                } catch (NumberFormatException e) {
                    this.maxMoney = null;
                    return;
                }
            }

            performFiltrationOperation();
        });

        flex.addChild(new FlexItem(descriptionInput, 3));
        flex.addChild(new FlexItem(maxMoneyInput, 1));

        return new BoxComponent(flex).setStyle(new BoxStyle(null));
    }

    private boolean inRange(
        LocalDate date, LocalTime time, PartialDateTime from, PartialDateTime to) {
        if (ptFrom.hasDatePart() && date.isBefore(from.toEarliestDate()))
            return false;
        if (ptTo.hasDatePart() && date.isAfter(to.toLatestDate()))
            return false;
        if (ptFrom.hasTimePart() && time.isBefore(from.toEarliestTime()))
            return false;
        if (ptTo.hasTimePart() && time.isAfter(to.toLatestTime()))
            return false;
        return true;
    }

    private void performFiltrationOperation() {
        var filtered =
            this.db.getData()
                .stream()
                .filter(t -> inRange(t.date(), t.time(), ptFrom, ptTo))
                .filter(t
                    -> this.vendor == null || this.vendor.isBlank()
                        || fuzzyMatches(t.vendor(), this.vendor, 2))
                .filter(t
                    -> this.description == null || this.description.isBlank()
                        || fuzzyMatches(t.description(), this.description, 2))
                .filter(t -> this.minMoney == null || t.amount().compareTo(this.minMoney) >= 0)
                .filter(t -> this.maxMoney == null || t.amount().compareTo(this.maxMoney) <= 0)
                .sorted(Comparator.comparing((Transaction t) -> exactMatchRank(t))
                        .thenComparing((Transaction t)
                                           -> LocalDateTime.of(t.date(), t.time()),
                            Comparator.reverseOrder()))
                .toList();

        this.tt.triggerRerender(filtered);
    }

    private int exactMatchRank(Transaction t) {
        boolean vendorExact = isExactMatch(t.vendor(), this.vendor);
        boolean descExact = isExactMatch(t.description(), this.description);

        if (vendorExact && descExact)
            return 0; // both fields exact
        if (vendorExact || descExact)
            return 1; // one field exact
        return 2; // neither exact (fuzzy-only match)
    }

    private boolean isExactMatch(String field, String query) {
        if (query == null || query.isBlank())
            return false;
        return field.equalsIgnoreCase(query.trim());
    }
}
