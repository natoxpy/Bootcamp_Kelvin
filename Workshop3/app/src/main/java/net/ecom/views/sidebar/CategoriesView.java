package net.ecom.views.sidebar;

import net.ecom.Model;
import net.ecom.components.Text;
import net.ecom.components.UiComponent;
import net.ecom.components.Window;

public class CategoriesView {
    private UiComponent<Window> parent;
    private UiComponent<Window> self;

    public CategoriesView(UiComponent<Window> parent) {
        this.parent = parent;

        this.self = new Window()
                        .updateUi((c) -> c.setW(((Window) parent).getW()))
                        .updateUi((c) -> c.setH(((Window) parent).getH() - 2))
                        .updateUi(UiComponent.SetPosition(-1, 1));

        this.parent.addChildren(this.self);
    }

    public UiComponent<Window> getSelf() {
        return this.self;
    }

    public void update(Model model) {
        this.self.clearChildren();

        int top = 0;
        for (String key : model.getCategories().keySet()) {
            String[] categorySet = model.getCategories().get(key);

            Text catTxt = new Text(key);

            catTxt.updateUi(UiComponent.SetPosition(1, top++));

            this.self.addChildren(catTxt);

            if (model.isCollapsed(key)) {
                catTxt.updateUi(c -> c.setContent(String.format("> %s", c.getContent())));
                continue;
            }

            catTxt.updateUi(c -> c.setContent(String.format("v %s", c.getContent())));

            for (int i = 0; i < categorySet.length; i++) {
                String categoryName = categorySet[i];

                Text txt = new Text(categoryName);

                if (categoryName.equals(model.getCategorySelection())) {
                    txt.updateUi(c -> c.setContent(String.format("[ %s ]", c.getContent())));
                } else {
                    txt.updateUi(c -> c.setContent(String.format("- %s", c.getContent())));
                }

                txt.updateUi(UiComponent.SetPosition(3, top++));

                this.self.addChildren(txt);
            }
        }
    }
}
