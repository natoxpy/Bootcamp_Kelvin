package net.example.demo.view;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import net.example.demo.component.InputDatetimeComponent;
import net.example.demo.component.InputDatetimeComponent.PartialDateTime;
import net.example.demo.component.TransactionTable;
import net.example.demo.data.Database;
import net.example.demo.data.Transaction;
import net.gui.commands.Color;
import net.gui.primitives.AlignSelf;
import net.gui.primitives.Direction;
import net.gui.primitives.JustifySelf;
import net.gui.primitives.dimension.Fixed;
import net.rosu.component.BoxComponent;
import net.rosu.component.ButtonComponent;
import net.rosu.component.Component;
import net.rosu.component.FlexComponent;
import net.rosu.component.FlexItem;
import net.rosu.component.InputComponent;
import net.rosu.component.Renderable;
import net.rosu.component.TextComponent;

public class RecordModificationView extends Component {
    private static final Pattern PARTIAL_NUMBER = Pattern.compile("^-?\\d*\\.?\\d*$");
    private ButtonComponent addButton = new ButtonComponent("Add")
                                            .onClick(() -> this.addClicked())
                                            .setHeight(new Fixed(1))
                                            .setClickedBg(Color.Blue);

    private ButtonComponent saveButton =
        new ButtonComponent("Write").onClick(() -> this.clickWrite()).setClickedBg(Color.Blue);

    private ButtonComponent discardButton =
        new ButtonComponent("Discard").onClick(() -> this.discardClicked()).setClickedBg(Color.Red);

    private Database db;
    private PartialDateTime pdt = new PartialDateTime(null, null, null, null, null);
    private String vendor = null;
    private String description = null;
    private BigDecimal amount = null;
    private HeaderView hv;

    public RecordModificationView(Database db, HeaderView hv) {
        this.db = db;
        this.hv = hv;
    }

    @Override
    public Renderable render() {
        var flex = new FlexComponent().setDirection(Direction.Column);

        flex.addChild(new FlexItem(this.editArea(), 3));
        flex.addChild(new FlexItem(this.previewArea(), 13));
        flex.addChild(new FlexItem(this.actionButtons(), 1));

        return flex;
    }

    private Renderable inputDatatimeField(String txt, Consumer<PartialDateTime> event) {
        var flex = new FlexComponent();
        var input = new InputDatetimeComponent().onPartialDt(event);

        flex.addChild(
            new FlexItem(new TextComponent(txt + " "), JustifySelf.Start, AlignSelf.Start, 1));
        flex.addChild(new FlexItem(input, 2));

        return flex;
    }

    private Renderable inputField(String txt, String placeholder, Consumer<String> event) {
        return this.inputField(txt, placeholder, event, JustifySelf.End, AlignSelf.Start);
    }

    private Renderable inputField(
        String txt, String placeholder, Consumer<String> event, JustifySelf js, AlignSelf as) {
        var flex = new FlexComponent();
        var input = new InputComponent(event)
                        .setPlaceholder(placeholder)
                        .setValidator((s) -> s.length() < placeholder.length());

        flex.addChild(new FlexItem(new TextComponent(txt + " "), js, as, 1));
        flex.addChild(new FlexItem(input, 3));

        return flex;
    }

    /*
     * 1 Hour wasted because the input could not be centered, I have no clue why,
     * I hate this, fix that dumb input bs that cannot be centered for no reason,
     * and can not be aligned because god knows why. @FIXME @FIXME @FIXME @FIXME
     * FIY, is 1AM, Friday on capstone day.
     */
    private Renderable inputFieldDescription(Consumer<String> event) {
        var placeholder = "_______________________________________________________";
        var flex = new FlexComponent();

        var input = new InputComponent(event)
                        .setPlaceholder(placeholder)
                        .setValidator((s) -> s.length() < placeholder.length());

        flex.addChild(
            new FlexItem(new TextComponent("Description "), JustifySelf.Start, AlignSelf.Start, 2));

        flex.addChild(new FlexItem(input, 9));

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
        flex.addChild(new FlexItem(input, 1));

        return flex;
    }

    private boolean isPartialNumber(String s) {
        if (s.isEmpty())
            return true; // empty is always a valid in-progress state
        return PARTIAL_NUMBER.matcher(s).matches();
    }

    public Renderable editArea() {
        var flex = new FlexComponent().setDirection(Direction.Column);

        flex.addChild(new FlexItem(this.topEditArea(), 1));
        flex.addChild(new FlexItem(this.bottomEditArea(), 1));

        return flex;
    }

    public Renderable topEditArea() {
        var flex = new FlexComponent();

        var inputTime = this.inputDatatimeField("Date/Time", (pt) -> { this.pdt = pt; });
        var inputVendor = this.inputField("Vendor", "_____________________",
            (i) -> { this.vendor = i.trim().equals("") ? null : i; });
        var inputAmount = this.inputNumberField("Amount $", "__________", (i) -> {
            if (i == null || i.isBlank()) {
                this.amount = null;
            } else {
                try {
                    this.amount = new BigDecimal(i);
                } catch (NumberFormatException e) {
                    this.amount = null;
                    return;
                }
            }
        });

        flex.addChild(new FlexItem(new BoxComponent(inputTime), 3));
        flex.addChild(new FlexItem(new BoxComponent(inputVendor), 3));
        flex.addChild(new FlexItem(new BoxComponent(inputAmount), 2));

        return flex;
    }

    public Renderable bottomEditArea() {
        var flex = new FlexComponent();

        var inputDescription = this.inputFieldDescription(
            (i) -> { this.description = i.trim().equals("") ? null : i; });

        flex.addChild(new FlexItem(inputDescription, 6));
        flex.addChild(new FlexItem(addButton));

        return flex;
    }

    /*
     * Dropped Feature of individual item deletion for time sake
     */
    public Renderable previewArea() {
        var flex = new FlexComponent();
        var tt = new TransactionTable(16);
        tt.setDatetimeMiddle(" - ");

        tt.triggerRerender(this.db.getUnwritten());

        flex.addChild(new FlexItem(tt, 6));

        return flex;
    }

    public Renderable actionButtons() {
        var flex = new FlexComponent();

        flex.addChild(new FlexItem(new BoxComponent(), 1));
        flex.addChild(new FlexItem(discardButton, 1));
        flex.addChild(new FlexItem(new BoxComponent(), 3));
        flex.addChild(new FlexItem(saveButton, 1));
        flex.addChild(new FlexItem(new BoxComponent(), 1));

        return flex;
    }

    private void resetVars() {
        this.description = null;
        this.amount = null;
        this.vendor = null;
    }

    public void addClicked() {
        if (this.amount == null)
            return;

        var now = LocalDateTime.now();

        LocalDate date = LocalDate.of(this.pdt.year() != null ? this.pdt.year() : now.getYear(),
            this.pdt.month() != null ? this.pdt.month() : now.getMonthValue(),
            this.pdt.day() != null ? this.pdt.day() : now.getDayOfMonth());
        LocalTime time = LocalTime.of(this.pdt.hour() != null ? this.pdt.hour() : now.getHour(),
            this.pdt.minute() != null ? this.pdt.minute() : now.getMinute());

        this.db.addTransaction(
            new Transaction(date, time, this.description != null ? this.description : "N/A",
                this.vendor != null ? this.vendor : "N/A", this.amount));

        this.resetVars();
        this.redraw();
    }

    public void discardClicked() {
        this.db.discardUnwritten();
        this.resetVars();
        this.redraw();
    }

    public void clickWrite() {
        this.db.save();
        this.redraw();
        this.hv.triggerRedraw();
    }
}
