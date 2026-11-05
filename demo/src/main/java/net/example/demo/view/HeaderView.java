package net.example.demo.view;

import java.math.BigDecimal;
import net.example.demo.data.Database;
import net.example.demo.data.Transaction;
import net.gui.commands.Color;
import net.gui.primitives.AlignSelf;
import net.gui.primitives.JustifySelf;
import net.rosu.component.Component;
import net.rosu.component.FlexComponent;
import net.rosu.component.FlexItem;
import net.rosu.component.Renderable;
import net.rosu.component.TextComponent;

public class HeaderView extends Component {
    private Database db;

    public HeaderView(Database db) {
        this.db = db;
    }

    @Override
    public Renderable render() {
        var flex = new FlexComponent();
        var title = new TextComponent("PGFAT32").setColor(Color.White);
        var netmoney = this.computeNetMoney();
        TextComponent netincome;

        if (netmoney.signum() >= 0)
            netincome = new TextComponent(String.format("Net Money $%s", netmoney.floatValue()))
                            .setColor(Color.White);
        else
            netincome =
                new TextComponent(String.format("Net Money -$%s", netmoney.abs().floatValue()))
                    .setColor(Color.White);

        flex.addChild(new FlexItem(title, JustifySelf.Start, AlignSelf.Center, 0));
        flex.addChild(new FlexItem(netincome, JustifySelf.End, AlignSelf.Center, 0));

        return flex;
    }

    private BigDecimal computeNetMoney() {
        return this.db.getData()
            .stream()
            .map(Transaction::amount)
			// .filter(amount -> amount.signum() < 0)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
