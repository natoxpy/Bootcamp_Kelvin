package net.example.demo.view;

import net.gui.commands.Color;
import net.rosu.component.Component;
import net.rosu.component.Renderable;
import net.rosu.component.TextComponent;

public class OverviewView extends Component {
    @Override
    public Renderable render() {
        var overview = new TextComponent("overview view").setColor(Color.White);

        return overview;
    }
}
