package net.example.demo.view;

import net.gui.commands.Color;
import net.gui.primitives.AlignSelf;
import net.gui.primitives.JustifySelf;
import net.rosu.component.Component;
import net.rosu.component.FlexComponent;
import net.rosu.component.FlexItem;
import net.rosu.component.Renderable;
import net.rosu.component.TextComponent;

public class TopView extends Component {
    @Override
    public Renderable render() {
        var flex = new FlexComponent();
		var title = new TextComponent("PGFAT32").setColor(Color.White);
		var netincome = new TextComponent("net money $00,000").setColor(Color.White);

        flex.addChild(new FlexItem(title, JustifySelf.Start, AlignSelf.Center, 0));
        flex.addChild(new FlexItem(netincome, JustifySelf.End, AlignSelf.Center, 0));

        return flex;
    }
}
