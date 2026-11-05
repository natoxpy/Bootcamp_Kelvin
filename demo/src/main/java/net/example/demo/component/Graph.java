package net.example.demo.component;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import net.example.demo.data.Transaction;
import net.gui.commands.BoxStyle;
import net.gui.commands.Color;
import net.gui.primitives.AlignSelf;
import net.gui.primitives.Direction;
import net.gui.primitives.JustifySelf;
import net.gui.primitives.dimension.Fixed;
import net.rosu.component.BoxComponent;
import net.rosu.component.Centered;
import net.rosu.component.Component;
import net.rosu.component.FlexComponent;
import net.rosu.component.FlexItem;
import net.rosu.component.Renderable;
import net.rosu.component.TextComponent;

public class Graph extends Component {
    private List<Transaction> transactions;
    private static final DateTimeFormatter LEGEND_FMT = DateTimeFormatter.ofPattern("MM/dd");

    public Graph(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public Renderable render() {
        var body = new FlexComponent().setDirection(Direction.Column);
        var legend = this.legend();

        var topSection = new FlexComponent();
        var bottomSection = new FlexComponent();

        var multiWeekReport = this.getMultiWeekReport();
        BigDecimal topHighest =
            Arrays.stream(multiWeekReport).max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);

        BigDecimal bottomLowest =
            Arrays.stream(multiWeekReport).min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);

		BigDecimal totalHeighest = topHighest.max(bottomLowest);

        topSection.addChild(new FlexItem(this.topSection(multiWeekReport, totalHeighest)));
        bottomSection.addChild(new FlexItem(this.bottomSection(multiWeekReport, totalHeighest)));

        body.addChild(new FlexItem(topSection, 12));
        body.addChild(new FlexItem(bottomSection, 12));
        body.addChild(new FlexItem(legend, 1));

        return body;
    }

    public Renderable topSection(BigDecimal[] weeklyNet, BigDecimal heightest) {
        var flex = new FlexComponent();

        for (var net : weeklyNet) {
            float ratio = net.floatValue() / heightest.floatValue();

            if (ratio < 0.1)
                ratio = 0.1f;

            var text = new TextComponent(String.format("$%s", net.floatValue()))
                           .setBackground(Color.Green)
                           .setColor(Color.Black);

            var box = new BoxComponent(new Centered(text))
                          .setHeight(new Fixed((int) (ratio * 11f)))
                          .setWidth(new Fixed(14))
                          .setStyle(new BoxStyle(Color.Green));

            if (net.signum() < 0)
                box = new BoxComponent();

            flex.addChild(new FlexItem(box, JustifySelf.Center, AlignSelf.End, 0));
        }

        return flex;
    }

    public Renderable bottomSection(BigDecimal[] weeklyNet, BigDecimal heightest) {
        var flex = new FlexComponent();

        for (var net : weeklyNet) {
            float ratio = net.abs().floatValue() / heightest.floatValue();

            if (ratio < 0.1)
                ratio = 0.1f;

            var text = new TextComponent(String.format("-$%s", net.abs().floatValue()))
                           .setBackground(Color.Red)
                           .setColor(Color.Black);

            var box = new BoxComponent(new Centered(text))
                          .setHeight(new Fixed((int) (ratio * 11f)))
                          .setWidth(new Fixed(14))
                          .setStyle(new BoxStyle(Color.Red));

            if (net.signum() > 0)
                box = new BoxComponent();

            flex.addChild(new FlexItem(box, JustifySelf.Center, AlignSelf.Start, 0));
        }

        return flex;
    }

    public BigDecimal[] getMultiWeekReport() {
        LocalDate currentWeekStart =
            LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        BigDecimal[] weeklyNet =
            IntStream.rangeClosed(0, 4)
                .mapToObj(i -> {
                    LocalDate from = currentWeekStart.minusWeeks(4 - i);
                    LocalDate to = from.plusWeeks(1);
                    return transactions.stream()
                        .filter(tx -> !tx.date().isBefore(from) && tx.date().isBefore(to))
                        .map(Transaction::amount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                })
                .toArray(BigDecimal[] ::new);

        return weeklyNet;
    }

    public Renderable legend() {
        var flex = new FlexComponent();

        LocalDate currentWeekStart =
            LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        for (int i = 0; i < 5; i++) {
            LocalDate weekStart = currentWeekStart.minusWeeks(4 - i);
            LocalDate weekEnd = weekStart.plusDays(6);

            var f = new FlexComponent().setDirection(Direction.Column);

            f.addChild(new FlexItem(new TextComponent(weekStart.format(LEGEND_FMT)),
                JustifySelf.Center, AlignSelf.Center, 0));
            f.addChild(new FlexItem(new TextComponent(weekEnd.format(LEGEND_FMT)),
                JustifySelf.Center, AlignSelf.Center, 0));

            flex.addChild(new FlexItem(f, JustifySelf.Center, AlignSelf.Center, 0));
        }

        return flex;
    }
}
