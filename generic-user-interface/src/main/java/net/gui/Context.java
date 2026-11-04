package net.gui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.gui.primitives.Box;
import net.gui.primitives.DrawtimeContext;
import net.gui.primitives.Flex;
import net.gui.primitives.FlexChild;
import net.gui.primitives.LayoutConstraint;
import net.gui.primitives.Node;
import net.gui.primitives.Text;
import net.gui.primitives.Viewport;
import net.gui.primitives.dimension.Auto;
import net.gui.primitives.dimension.Dimension;
import net.gui.primitives.dimension.Fill;
import net.gui.primitives.dimension.Fixed;
import net.gui.witgets.ContextState;

public class Context {
    private int nextId = 0;
    private final Map<Integer, Node> nodes = new HashMap<>();
    private final Map<Integer, ContextState> states = new HashMap<>();
    public final DrawtimeContext drawtime = new DrawtimeContext();

    public int insert(Node node) {
        int id = this.nextId++;
        this.nodes.put(id, node);
        return id;
    }

    public Node get(int id) {
        return this.nodes.get(id);
    }

    public void update(int id, Node node) {
        this.nodes.put(id, node);
    }

    public void remove(int id) {
        this.nodes.remove(id);
    }

    public ContextState getState(int id) {
        return this.states.get(id);
    }

    public void updateState(int id, ContextState state) {
        this.states.put(id, state);
    }

    private List<Integer> childrenOf(Node node) {
        if (node instanceof Box b) {
            return b.child() != null ? List.of(b.child()) : List.of();
        } else if (node instanceof Flex f) {
            return f.children().stream().map(FlexChild::nodeId).toList();
        } else if (node instanceof Text) {
            return List.of();
        } else if (node instanceof Viewport v) {
            return v.child() != null ? List.of(v.child()) : List.of();
        }
        throw new IllegalStateException("unreachable");
    }

    private String describe(Node node) {
        if (node instanceof Box b) {
            return "Box { constraints=" + describe(b.constraints()) + " }";
        } else if (node instanceof Text t) {
            return "Text { content=\"" + t.content() + "\", wrap=" + t.wrap() + " }";
        } else if (node instanceof Flex f) {
            return "Flex { axis=" + f.axis() + ", gap=" + f.gap() + " }";
        } else if (node instanceof Viewport v) {
            return String.format("Viewport { offsetX=%s, offsetY=%s }", v.offsetX(), v.offsetY());
        }
        throw new IllegalStateException("unreachable");
    }

    private String describe(LayoutConstraint c) {
        return "{ w=" + describe(c.width()) + ", h=" + describe(c.height()) + " }";
    }

    private String describe(Dimension d) {
        if (d instanceof Fixed f) {
            return "Fixed(" + f.value() + ")";
        } else if (d instanceof Fill) {
            return "Fill";
        } else if (d instanceof Auto) {
            return "Auto";
        }
        throw new IllegalStateException("unreachable");
    }

    private void printNode(int id, String prefix, boolean isLast, StringBuilder sb) {
        Node node = nodes.get(id);
        sb.append(prefix + (isLast ? "└── " : "├── ") + "[" + id + "] " + describe(node) + "\n");

        String childPrefix = prefix + (isLast ? "    " : "│   ");
        List<Integer> childIds = childrenOf(node);
        for (int i = 0; i < childIds.size(); i++) {
            printNode(childIds.get(i), childPrefix, i == childIds.size() - 1, sb);
        }
    }

    public String getPrettyTree(int rootId) {
        StringBuilder sb = new StringBuilder();
        this.printNode(rootId, "", true, sb);
        return sb.toString();
    }
}
