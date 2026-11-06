package net.example.demo.component;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import net.example.demo.data.Transaction;
import net.gui.commands.Color;
import net.gui.primitives.AlignSelf;
import net.gui.primitives.Direction;
import net.gui.primitives.JustifySelf;
import net.gui.primitives.Overflow;
import net.rosu.component.BoxComponent;
import net.rosu.component.ButtonComponent;
import net.rosu.component.Centered;
import net.rosu.component.Component;
import net.rosu.component.FlexComponent;
import net.rosu.component.FlexItem;
import net.rosu.component.Renderable;
import net.rosu.component.TextComponent;

public class TransactionTable extends Component {
    private List<Transaction> transactions = List.of();
    private int showingPage = 0;
    private final int showingPerPage = 20;

    private final ButtonComponent buttonLeftPagination =
        new ButtonComponent("<", () -> this.setShowingIndex(this.showingPage - 1))
            .setClickedBg(Color.Blue);

    private final ButtonComponent buttonRightPagination =
        new ButtonComponent(">", () -> this.setShowingIndex(this.showingPage + 1))
            .setClickedBg(Color.Blue);

    public TransactionTable() {}

    @Override
    public Renderable render() {
        var flex = new FlexComponent().setDirection(Direction.Column);

        flex.addChild(new FlexItem(this.table(), 8));
        flex.addChild(new FlexItem(this.pagination(), 1));

        return flex;
    }

    public void triggerRerender(List<Transaction> transactions) {
		this.showingPage = 0;
        this.transactions = transactions.stream()
                                .sorted(Comparator.comparing(Transaction::date)
                                        .thenComparing(Transaction::time)
                                        .reversed())
                                .toList();

        this.redraw();
    }

    private Renderable transactionItem(Transaction t) {
        var flex = new FlexComponent().setGap(1);

        String datetime = t.date().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + " - "
            + t.time().format(DateTimeFormatter.ofPattern("HH:mm"));

        String amount = t.amount().signum() >= 0 ? " $" + t.amount() : "-$" + t.amount().abs();

        flex.addChild(new FlexItem(
            new BoxComponent(new TextComponent(t.vendor())).setOverflow(Overflow.Hidden), 2));
        flex.addChild(new FlexItem(
            new BoxComponent(new TextComponent(t.description())).setOverflow(Overflow.Hidden), 3));
        flex.addChild(new FlexItem(new TextComponent(datetime), 2));
        flex.addChild(new FlexItem(new TextComponent(amount), 1));

        return flex;
    }

    private Renderable tableTitle() {
        var flex = new FlexComponent().setGap(1);

        flex.addChild(new FlexItem(
            new Centered(new TextComponent("vendor".toUpperCase()).setColor(Color.Cyan)), 2));
        flex.addChild(new FlexItem(
            new Centered(new TextComponent("description".toUpperCase()).setColor(Color.Cyan)),
            3));
        flex.addChild(new FlexItem(
            new Centered(new TextComponent("Date/Time".toUpperCase()).setColor(Color.Cyan)), 2));
        flex.addChild(new FlexItem(
            new Centered(new TextComponent("Amount".toUpperCase()).setColor(Color.Cyan)), 1));

        return flex;
    }

    public Renderable table() {
        var flex = new FlexComponent().setDirection(Direction.Column);

        flex.addChild(new FlexItem(this.tableTitle(), 2));

        for (int i = 0; i < this.showingPerPage; i++) {
            var ri = i + showingPerPage * showingPage; // relative i

            if (ri >= transactions.size()) {
                // Due to system constraints empty boxes must be added to preserve proper growth
                // from actual content boxes
                flex.addChild(new FlexItem(new BoxComponent(), 1));
                continue;
            }

            var t = transactions.get(ri);

            flex.addChild(new FlexItem(
                new BoxComponent(this.transactionItem(t)).setOverflow(Overflow.Hidden), 1));
        }

        return flex;
    }

    public Renderable paginationOptions() {
        var flex = new FlexComponent();

        int totalPages = (int) Math.ceil(transactions.size() / (double) showingPerPage);
        if (totalPages <= 0)
            return flex;

        final int WINDOW = 9;
        final int LOOKAHEAD = 2; // keep at least this many pages visible ahead of showingPage

        int windowSize = Math.min(WINDOW, totalPages);

        // Position the window so showingPage sits LOOKAHEAD slots from the window's end
        int start = showingPage - (windowSize - 1 - LOOKAHEAD);

        // Clamp so the window never runs off either edge
        start = Math.max(0, start);
        start = Math.min(start, totalPages - windowSize);

        Set<Integer> pagesToShow = new TreeSet<>();
        for (int i = start; i < start + windowSize; i++) {
            pagesToShow.add(i);
        }

        for (int page : pagesToShow) {
            var button =
                new ButtonComponent("" + (page + 1)).onClick(() -> this.setShowingIndex(page));

            if (page == showingPage) {
                button = new ButtonComponent("" + (page + 1))
                             .setClickedBg(Color.Cyan)
                             .setBackground(Color.Cyan)
                             .setColor(Color.Black);
            }

            flex.addChild(new FlexItem(button, JustifySelf.Start, AlignSelf.Start, 1));
        }

        return flex;
    }

    public Renderable pagination() {
        var flex = new FlexComponent().setGap(4);
        var flexOptions = this.paginationOptions();

        flex.addChild(new FlexItem(buttonLeftPagination, JustifySelf.Start, AlignSelf.Start, 1));
        flex.addChild(new FlexItem(flexOptions, 4));
        flex.addChild(new FlexItem(buttonRightPagination, JustifySelf.Start, AlignSelf.Start, 1));

        return flex;
    }

    public void setShowingIndex(int i) {
        int totalPages = (int) Math.ceil(transactions.size() / (double) showingPerPage);
        if (i < 0 || i >= totalPages)
            return;
        this.showingPage = i;
        this.redraw();
    }
}
