package net.example.demo.view;

import net.example.demo.data.Database;
import net.rosu.component.BoxComponent;
import net.rosu.component.Component;
import net.rosu.component.FlexComponent;
import net.rosu.component.FlexItem;
import net.rosu.component.OutlineDesign;
import net.rosu.component.Renderable;

public class BodyView extends Component {
	private Database db;
    SidebarView sidebar;
    Renderable content;

    public BodyView(Database db) {
		this.db = db;
        sidebar = new SidebarView()
                      .onClickOverview(() -> this.setOverviewView())
                      .onClickTransaction(() -> this.setTransactionsView())
                      .onClickModifyRecord(() -> this.setRecordModificationView());

        content = new OverviewView(db);
    }

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
        this.content = new OverviewView(this.db);
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
