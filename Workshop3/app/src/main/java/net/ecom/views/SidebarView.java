package net.ecom.views;

import net.ecom.Model;
import net.ecom.components.ComponentUpdate;
import net.ecom.components.Text;
import net.ecom.components.UiComponent;
import net.ecom.components.Window;
import net.ecom.views.sidebar.CategoriesView;

public class SidebarView {
    private UiComponent<Window> parent;
    private UiComponent<Window> self;
    private UiComponent<Text> title;
    private CategoriesView categories;
    // private UiComponent<Window> filtersUi;

    public SidebarView(UiComponent<Window> parent) {
        this.parent = parent;

        this.self =
            new Window(30, ((Window) parent).getH()).updateUi(UiComponent.SetPosition(-1, -1));

        Window sidebarTitle = new Window(((Window) this.self).getW(), 3);
        sidebarTitle.updateUi(UiComponent.SetPosition(-1, -1));

        this.title = new Text("No Title");

        this.categories = new CategoriesView(this.self);

        // this.filtersUi =
        //     new Window()
        //         .updateUi((c) -> c.setW(((Window) self).getW()))
        //         .updateUi((c) -> c.setH(((Window) self).getH() / 2 - 1))
        //         .updateUi(UiComponent.setPosition(
        //             -1, ((Window) categories.getSelf()).getH() + categories.getSelf().getY() -
        //             1));

        sidebarTitle.addChildren(this.title);
        this.self.addChildren(sidebarTitle);

        // this.self.addChildren(this.filtersUi);

        this.parent.addChildren(this.self);
    }

    public void update(Model model) {
        this.categories.update(model);

        this.title.updateUi((c) -> c.setContent(model.getTitle()));

        ComponentUpdate<Text> setCenterX = (c)
            -> c.setX(UiComponent.getCenter(((Window) this.self).getW() - 2, c.getContent().length()));

        this.title.updateUi(setCenterX);
    }
}
