package net.example.demo.view;

import net.example.demo.component.TransactionTable;
import net.example.demo.data.Database;
import net.gui.primitives.Direction;
import net.rosu.component.Component;
import net.rosu.component.FlexComponent;
import net.rosu.component.FlexItem;
import net.rosu.component.Renderable;

public class TransactionsView extends Component {
    private Database db;
	private TransactionTable tt;

    public TransactionsView(Database db) {
        this.db = db;
		this.tt = new TransactionTable();
    }

    @Override
    public Renderable render() {
        var flex = new FlexComponent().setDirection(Direction.Column);
		var tf = new TransactionsFilterSection(this.db, this.tt);


        flex.addChild(new FlexItem(tf, 1));
        flex.addChild(new FlexItem(tt, 8));

        return flex;
    }
}
