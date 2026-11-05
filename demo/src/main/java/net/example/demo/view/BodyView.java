package net.example.demo.view;

import net.rosu.component.BoxComponent;
import net.rosu.component.Component;
import net.rosu.component.FlexComponent;
import net.rosu.component.FlexItem;
import net.rosu.component.OutlineDesign;
import net.rosu.component.Renderable;

public class BodyView extends Component {
    Renderable content = new OverviewView();
	int active = 0;
    SidebarView sidebar = new SidebarView(()
                                             -> this.setOverviewView(),
        () -> this.setTransactionsView(), () -> this.setRecordModificationView());

    @Override
    public Renderable render() {
        var flex = new FlexComponent();

        var c = new BoxComponent(this.content)
                    .setOutlineDesign(new OutlineDesign('─', '│', '┬', '┤', '└', '┘'));

        flex.addChild(new FlexItem(sidebar, 1));
        flex.addChild(new FlexItem(c, 4));

        return flex;
    }

    private void setOverviewView() {
        this.content = new OverviewView();
		this.sidebar.setActive(0);
        this.redraw();
    }

    private void setTransactionsView() {
        this.content = new TransactionsView();
		this.sidebar.setActive(1);
        this.redraw();
    }

    private void setRecordModificationView() {
        this.content = new RecordModificationView();
		this.sidebar.setActive(2);
        this.redraw();
    }
}
