package net.example.demo.component;

import net.gui.primitives.AlignSelf;
import net.gui.primitives.JustifySelf;
import net.rosu.component.Component;
import net.rosu.component.FlexComponent;
import net.rosu.component.FlexItem;
import net.rosu.component.Renderable;

public class Centered extends Component {
    private Renderable child;

    public Centered(Renderable child) {
        this.child = child;
    }

    @Override
    public Renderable render() {
        var flex = new FlexComponent();
        var item = new FlexItem(this.child, JustifySelf.Center, AlignSelf.Center, 0);

        flex.addChild(item);

        return flex;
    }
}
