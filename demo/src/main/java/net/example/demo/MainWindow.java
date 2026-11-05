package net.example.demo;

import net.example.demo.component.*;
import net.gui.commands.Color;
import net.gui.primitives.Direction;
import net.gui.primitives.dimension.Fixed;
import net.rosu.component.BoxComponent;
import net.rosu.component.Component;
import net.rosu.component.FlexComponent;
import net.rosu.component.FlexItem;
import net.rosu.component.OutlineDesign;
import net.rosu.component.Renderable;
import net.rosu.component.TextComponent;

class TopView extends Component {
    @Override
    public Renderable render() {
        return new TextComponent("top bar").setColor(Color.White);
    }
}

class MainView extends Component {
    @Override
    public Renderable render() {
        return new TextComponent("main content").setColor(Color.White);
    }
}

public class MainWindow extends Component {
    public Renderable render() {
        var flex = new FlexComponent().setDirection(Direction.Column).setGap(1);

        var topbar =
            new BoxComponent().setOutlineDesign(OutlineDesign.basic()).setChild(new TopView());

        var maincontent = new BoxComponent()
                              .setOutlineDesign(OutlineDesign.basicTopConnect())
                              .setChild(new MainView());

        flex.addChild(new FlexItem(topbar, 1));
        flex.addChild(new FlexItem(maincontent, 8));

        return new Centered(new BoxComponent(new Fixed(130), new Fixed(35)).setChild(flex));
    }
}
