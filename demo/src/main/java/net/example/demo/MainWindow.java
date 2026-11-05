package net.example.demo;

import net.example.demo.component.*;
import net.example.demo.data.Database;
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
    private Database db;

    HeaderView headerView;
    BodyView bodyView;

    public MainWindow(Database db) {
        this.db = db;
        this.headerView = new HeaderView(this.db);
		this.bodyView = new BodyView(this.db);
    }

    public Renderable render() {
        var flex = new FlexComponent().setDirection(Direction.Column).setGap(1);

        var topbarWrapper =
            new BoxComponent().setOutlineDesign(OutlineDesign.basic()).setChild(this.headerView);

        var maincontent = new BoxComponent()
                              .setOutlineDesign(OutlineDesign.basicTopConnect())
                              .setChild(this.bodyView);

        flex.addChild(new FlexItem(topbarWrapper, 1));
        flex.addChild(new FlexItem(maincontent, 18));

        return new Centered(new BoxComponent(new Fixed(100), new Fixed(30)).setChild(flex));
    }
}
