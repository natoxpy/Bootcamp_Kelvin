package net.ecom;

import java.io.IOException;
import java.util.HashMap;
import net.ecom.views.MainView;
import org.jline.utils.NonBlockingReader;

enum InteractionType { Tab, Next, Previous, UpArrow, DownArrow, Enter }

interface InteractionBehavior {
    public void behavior(InteractionType interacType, InteractiveTree tree);
}

class InteractiveTree {
    public InteractiveTree parent = null;
    public InteractiveTree leftTree = null;
    public InteractiveTree rightTree = null;
    public HashMap<String, String> behaviors = new HashMap<>();
    public InteractionBehavior interactiveBehavior = null;
    public String id = null;

    public InteractiveTree setBehavior(InteractionBehavior ib) {
        this.interactiveBehavior = ib;
        return this;
    }

    public InteractiveTree addBehaviors(String key, String behavior) {
        this.behaviors.put(key, behavior);
        return this;
    }

    public InteractiveTree createLeftTree() {
        this.leftTree = new InteractiveTree();
        this.leftTree.parent = this;
        return this.leftTree;
    }

    public InteractiveTree createRightTree() {
        this.rightTree = new InteractiveTree();
        this.rightTree.parent = this;
        return this.rightTree;
    }

    public String getId() {
        return this.id;
    }

    public InteractiveTree setId(String id) {
        this.id = id;
        return this;
    }

    public InteractiveTree getFocus() {
        if (this.leftTree == null)
            return this;
        return this.leftTree.getFocus();
    }

    public void swap() {
        var leftTree = this.leftTree;
        this.leftTree = this.rightTree;
        this.rightTree = leftTree;
    }

    public void behavior(InteractionType interacType) {
        if (interactiveBehavior == null)
            return;
        interactiveBehavior.behavior(interacType, this);
    }
}

public class Controller {
    private MainView view;
    private Model model;

    private InteractiveTree rootTree;
    private InteractiveTree activeTree;

    public Controller(MainView view, Model model) {
        this.view = view;
        this.model = model;
        this.rootTree = new InteractiveTree();

        InteractionBehavior ArrowPickSibling = (b, t) -> {
            if (this.activeTree == t.parent.leftTree && b == InteractionType.Next)
                this.activeTree = t.parent.rightTree;
            else if (this.activeTree == t.parent.rightTree && b == InteractionType.Previous)
                this.activeTree = t.parent.leftTree;
        };

        InteractionBehavior pickSiblingInt = (b, t) -> {
            ArrowPickSibling.behavior(b, t);

            if (this.activeTree == t.parent.leftTree && b == InteractionType.Tab)
                this.activeTree = t.parent.rightTree;
            else if (b == InteractionType.Tab)
                this.activeTree = t.parent.leftTree;
        };

        this.rootTree.createLeftTree()
            .setId("sidebar.categories")
            .setBehavior((b, t) -> {
                pickSiblingInt.behavior(b, t);

                if (b == InteractionType.DownArrow) {
                    String k = "category.selection.index";
                    int ioffset = 0;
                    Object obj = model.getFocusState(k);
                    Object windowHeight = model.getFocusState("category.area.height");
                    Object offset = model.getFocusState("category.offset");

                    if (obj == null) {
                        obj = Integer.valueOf(0);
                        model.setFocusState(k, obj);
                    }

                    if (offset instanceof Integer)
                        ioffset = (int) offset;

                    if (obj instanceof Integer && windowHeight instanceof Integer) {
                        int wh = (int) windowHeight;

                        if (wh - 3 > (int) obj)
                            model.setFocusState(k, Integer.valueOf(((int) obj) + 1));
                        else
                            model.setFocusState("category.offset", ((int) ioffset) + 1);
                    }
                }

                if (b == InteractionType.UpArrow) {
                    String k = "category.selection.index";
                    int ioffset = 0;
                    Object obj = model.getFocusState(k);
                    Object offset = model.getFocusState("category.offset");

                    if (obj == null) {
                        obj = Integer.valueOf(0);
                        model.setFocusState(k, obj);
                    }

                    if (offset instanceof Integer)
                        ioffset = (int) offset;

                    if (obj instanceof Integer) {
                        if ((int) obj >= 1)
                            model.setFocusState(k, Integer.valueOf(((int) obj) - 1));
                        else if (ioffset > 0)
                            model.setFocusState("category.offset", ((int) ioffset) - 1);
                    }
                }

                if (b == InteractionType.Enter) {
                    Object indexCategory = model.getFocusState("category.selection.index.category");

                    if (indexCategory instanceof String) {
                        String category = (String) indexCategory;
                        model.setCategorySelection(category);
                        model.setFocusState("category.selection.index.category.group", null);
                    }

                    Object indexCategoryGroup =
                        model.getFocusState("category.selection.index.category.group");

                    if (indexCategoryGroup instanceof String) {
                        String icg = (String) indexCategoryGroup;

                        model.setFocusState("category.selection.index.category.group", null);

                        if (model.isCollapsed(icg))
                            model.removeCollapse(icg);
                        else
                            model.addCollapse(icg);
                    }
                }
            })
            .addBehaviors("[←|→] or [Tab]", "Navigate")
            .addBehaviors("[↑|↓]", "Categories")
            .addBehaviors("[Enter]", "View Category");

        this.rootTree.createRightTree()
            .setBehavior((b, t) -> { pickSiblingInt.behavior(b, t); })
            .setId("body")
            .addBehaviors("[←|→] or [Tab]", "Navigate")
            .addBehaviors("[↑|↓]", "Browse Products")
            .addBehaviors("[Enter]", "Add To Cart");

        this.activeTree = this.rootTree.getFocus();

        this.update();
    }

    public MainView getView() {
        return view;
    }

    public Model getModel() {
        return model;
    }

    public void update() {
        this.model.setFocusArea(this.activeTree.getId());
        this.model.setFocusBehaviors(this.activeTree.behaviors);
    }

    public int handleInput(NonBlockingReader reader) throws IOException {
        int c = reader.read(1000);

        if (c == 'q')
            return -1;

        if (c == 9) // (tab)
            this.activeTree.behavior(InteractionType.Tab);

        if (c == '>')
            this.activeTree.behavior(InteractionType.Next);
        if (c == '<')
            this.activeTree.behavior(InteractionType.Previous);

        if (c == 13 || c == 10) // \r or \n
            this.activeTree.behavior(InteractionType.Enter);

        // Multi-keys

        if (c == 27) {
            c = reader.read(1000);
            if (c == '[') {
                c = reader.read(1000);
                switch (c) {
                    case 'A': // 'Up'
                        this.activeTree.behavior(InteractionType.UpArrow);
                        break;
                    case 'B': // 'Down'
                        this.activeTree.behavior(InteractionType.DownArrow);
                        break;
                    case 'C': // 'Right'
                        this.activeTree.behavior(InteractionType.Next);
                        break;
                    case 'D': // 'Left'
                        this.activeTree.behavior(InteractionType.Previous);
                        break;
                }
            }
        }

        this.update();

        return 0;
    }
}
