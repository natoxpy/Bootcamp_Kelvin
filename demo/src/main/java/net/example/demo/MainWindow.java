package net.example.demo;

import net.example.demo.component.*;
import net.example.demo.view.*;
import net.gui.primitives.Direction;
import net.gui.primitives.dimension.Fixed;
import net.rosu.component.BoxComponent;
import net.rosu.component.Component;
import net.rosu.component.FlexComponent;
import net.rosu.component.FlexItem;
import net.rosu.component.OutlineDesign;
import net.rosu.component.Renderable;

public class MainWindow extends Component {
    public Renderable render() {
        var flex = new FlexComponent().setDirection(Direction.Column).setGap(1);

        var topbar =
            new BoxComponent().setOutlineDesign(OutlineDesign.basic()).setChild(new TopView());

        var maincontent = new BoxComponent()
                              .setOutlineDesign(OutlineDesign.basicTopConnect())
                              .setChild(new BodyView());

        flex.addChild(new FlexItem(topbar, 1));
        flex.addChild(new FlexItem(maincontent, 18));

        return new Centered(new BoxComponent(new Fixed(100), new Fixed(30)).setChild(flex));
    }
}
