package net.ecom.views.sidebar;

import java.util.ArrayList;
import java.util.HashSet;
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

    public int getSelfW() {
        return ((Window) this.self).getW();
    }

    public int getSelfH() {
        return ((Window) this.self).getH();
    }

    public void update(Model model) {
        this.self.clearChildren();

        int top = 0;
    root:
        for (String key : model.getCategories().keySet()) {
            HashSet<String> categorySet = model.getCategories().get(key);
            String[] categoryArr = new String[categorySet.size()];
            categoryArr = categorySet.toArray(categoryArr);

            Text catTxt = new Text(key);

            catTxt.updateUi(UiComponent.SetPosition(1, top++));

            this.self.addChildren(catTxt);

            if (model.isCollapsed(key)) {
                catTxt.updateUi(c -> c.setContent(String.format("> %s", c.getContent())));
                continue;
            }

            catTxt.updateUi(c -> c.setContent(String.format("v %s", c.getContent())));

            for (int i = 0; i < categorySet.size(); i++) {
                if (top > this.getSelfH() - 5)
                    break root;

                String categoryName = categoryArr[i];

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
