package net.rosu.component;

import net.gui.primitives.AlignSelf;
import net.gui.primitives.JustifySelf;

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
