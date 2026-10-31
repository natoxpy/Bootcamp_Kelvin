package net.ecom.views.sidebar;

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

        int selectedIndex = 0, categoryOffset = 0;
        Object selectedIndexState = model.getFocusState("category.selection.index");
        Object categoryOffsetState = model.getFocusState("category.offset");

        model.setFocusState("category.area.height", this.getSelfH());

        if (selectedIndexState instanceof Integer)
            selectedIndex = (int) selectedIndexState;
        if (categoryOffsetState instanceof Integer)
            categoryOffset = (int) categoryOffsetState;

        if (!model.getFocusArea().startsWith("sidebar.categories"))
            selectedIndex = -1;

        int top = 0 - categoryOffset;
        for (String key : model.getCategories().keySet()) {
            HashSet<String> categorySet = model.getCategories().get(key);
            String[] categoryArr = new String[categorySet.size()];
            categoryArr = categorySet.toArray(categoryArr);

            Text catTxt = new Text(key);

            catTxt.updateUi(UiComponent.SetPosition(1, top++));

            if (top >= 0 && top <= this.getSelfH())
                this.self.addChildren(catTxt);

            char cursor = '>';
            if (selectedIndex == top - 1)
                cursor = '*';

            if (selectedIndex == top - 1) {
                model.setFocusState("category.selection.index.category", null);
                model.setFocusState("category.selection.index.category.group", key);
            }

            if (model.isCollapsed(key)) {
                catTxt.setContent(String.format("%s %s", cursor, catTxt.getContent()));
                continue;
            }

            cursor = 'v';
            if (selectedIndex == top - 1)
                cursor = '*';

            catTxt.setContent(String.format("%s %s", cursor, catTxt.getContent()));

            for (int i = 0; i < categorySet.size(); i++) {
                String categoryName = categoryArr[i];

                Text txt = new Text(categoryName);

                if (categoryName.equals(model.getCategorySelection())) {
                    cursor = '[';
                    char cursor2 = ']';

                    if (selectedIndex == top) {
                        cursor = '|';
                        cursor2 = '|';
                    }

                    txt.setContent(String.format("%s %s %s", cursor, txt.getContent(), cursor2));
                } else {
                    cursor = '-';
                    if (selectedIndex == top) {
                        model.setFocusState("category.selection.index.category", categoryName);
                        cursor = '*';
                    }

                    txt.setContent(String.format("%s %s", cursor, txt.getContent()));
                }

                txt.updateUi(UiComponent.SetPosition(3, top++));

                if (top >= 0 && top <= this.getSelfH())
                    this.self.addChildren(txt);
            }
        }

        if (selectedIndex >= top) {
            model.setFocusState("category.selection.index", top - 1);
            this.update(model);
        }
    }
}
