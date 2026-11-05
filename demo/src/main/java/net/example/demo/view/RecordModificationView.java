package net.example.demo.view;

import net.rosu.component.Component;
import net.rosu.component.Renderable;
import net.rosu.component.TextComponent;

public class RecordModificationView extends Component {
    @Override
    public Renderable render() {
        return new TextComponent("Record Modification View");
    }
}
