package net.example.demo;

import net.gui.primitives.AlignSelf;
import net.gui.primitives.JustifySelf;
import net.gui.primitives.Overflow;
import net.gui.primitives.dimension.Fixed;
import net.rosu.Rosu;
import net.rosu.component.BoxComponent;
import net.rosu.component.Component;
import net.rosu.component.FlexComponent;
import net.rosu.component.FlexItem;
import net.rosu.component.InputComponent;
import net.rosu.component.Renderable;
import net.rosu.component.ScrollableComponent;
import net.rosu.component.TextComponent;

class Center extends Component {
    Renderable child;

    public Center(Renderable child) {
        this.child = child;
    }

    @Override
    public Renderable render() {
        var fc = new FlexItem(this.child, JustifySelf.Center, AlignSelf.Center);
        return new FlexComponent().addChild(fc);
    }
}

class Window extends Component {
    int counter = 0;

    @Override
    public Renderable render() {
        var text = new Center(new TextComponent("Hello, World"));
        var box = new BoxComponent()
                      .setOverflow(Overflow.Visible)
                      .setChild(text)
                      .setWidth(new Fixed(30))
                      .setHeight(new Fixed(50));

        var scroll = new ScrollableComponent(box);

        return new BoxComponent()
            .setOverflow(Overflow.Hidden)
            .setWidth(new Fixed(20))
            .setHeight(new Fixed(20))
            .setChild(scroll);
    }

    @Override
    public void onKey(char c) {
        this.redraw();
        this.counter += 1;
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
