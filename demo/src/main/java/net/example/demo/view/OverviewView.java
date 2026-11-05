package net.example.demo.view;

import net.example.demo.component.Graph;
import net.example.demo.data.Database;
import net.gui.primitives.dimension.Fixed;
import net.rosu.component.BoxComponent;
import net.rosu.component.Centered;
import net.rosu.component.Component;
import net.rosu.component.Renderable;

public class OverviewView extends Component {
    private Database db;

    public OverviewView(Database db) {
        this.db = db;
    }

    @Override
    public Renderable render() {
        var graph = new Graph(this.db.getData());

        var box = new BoxComponent(graph)
                      .setWidth(new Fixed(70))
                      .setHeight(new Fixed(26));

        return new Centered(box);
    }
}
