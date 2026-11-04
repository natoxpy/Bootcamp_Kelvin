package net.example.demo;

import net.gui.primitives.AlignSelf;
import net.gui.primitives.JustifySelf;
import net.gui.primitives.dimension.Fixed;
import net.rosu.Rosu;
import net.rosu.component.BoxComponent;
import net.rosu.component.Component;
import net.rosu.component.FlexComponent;
import net.rosu.component.FlexItem;
import net.rosu.component.Renderable;
import net.rosu.component.TextComponent;

class Center extends Component {
    private Renderable child;

    public Center(Renderable child) {
        this.child = child;
    }

    @Override
    public Renderable render() {
        var child = new FlexItem(this.child, JustifySelf.Center, AlignSelf.Center);
        return new FlexComponent().addChild(child);
    }
}

class Window extends Component {
    @Override
    public Renderable render() {
        var centeredText = new Center(new TextComponent("Centered Div"));
        var box = new BoxComponent(new Fixed(20), new Fixed(5)).setChild(centeredText);

        return new Center(box);
    }
}

public class Demo {
    public static void main(String[] args) {
        Rosu app = new Rosu();

        Component window = new Window();

        app.setRoot(window);

        app.start();
        app.close();
    }
}
