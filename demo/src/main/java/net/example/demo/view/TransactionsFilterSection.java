package net.example.demo.view;

import net.example.demo.component.InputDatetimeComponent;
import net.example.demo.component.TransactionTable;
import net.example.demo.data.Database;
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
    private Database db;
    private TransactionTable tt;

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

        flex.addChild(new FlexItem(this.filterByDate(), 1));
        flex.addChild(new FlexItem(vendorAndAmountFilter, 1));

		this.performFiltrationOperation();

        return flex;
    }

    private Renderable inputDatatimeField(String txt) {
        var flex = new FlexComponent();
        var input = new InputDatetimeComponent().onPartialDt((p) -> {

        });

        flex.addChild(
            new FlexItem(new TextComponent(txt + " "), JustifySelf.End, AlignSelf.Start, 1));
        flex.addChild(new FlexItem(input, 5));

        return flex;
    }

    private Renderable inputField(String txt, String placeholder) {
        var flex = new FlexComponent();
        var input = new InputComponent().setPlaceholder(placeholder);

        flex.addChild(
            new FlexItem(new TextComponent(txt + " "), JustifySelf.End, AlignSelf.Start, 1));
        flex.addChild(new FlexItem(input, 5));

        return flex;
    }

    private Renderable filterByDate() {
        var flex = new FlexComponent().setDirection(Direction.Column);

        flex.addChild(new FlexItem(this.inputDatatimeField("From"), 1));
        flex.addChild(new FlexItem(this.inputDatatimeField("To"), 1));

        return new BoxComponent(flex).setStyle(new BoxStyle(null));
    }

    private Renderable filterByVendor() {
        var flex = new FlexComponent().setDirection(Direction.Row);

        flex.addChild(new FlexItem(this.inputField("Vendor", "_________________"), 3));
        flex.addChild(new FlexItem(this.inputField("Min $", "_______"), 1));

        return new BoxComponent(flex).setStyle(new BoxStyle(null));
    }

    private Renderable filterByAmount() {
        var flex = new FlexComponent().setDirection(Direction.Row);

        flex.addChild(new FlexItem(this.inputField("Description", "_________________"), 3));
        flex.addChild(new FlexItem(this.inputField("Max $", "_______"), 1));

        return new BoxComponent(flex).setStyle(new BoxStyle(null));
    }

    private void performFiltrationOperation() {
		this.tt.triggerRerender(this.db.getData());
    }
}
