package net.rosu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;

interface Dimension {
    public int resolve(int available, IntSupplier measureContent);
}

record Fixed(int value) implements Dimension {
    public int resolve(int available, IntSupplier measureContent) {
        return value;
    }
}

record Fill() implements Dimension {
    public int resolve(int available, IntSupplier measureContent) {
        return available;
    }
}

record Auto() implements Dimension {
    public int resolve(int available, IntSupplier measureContent) {
        return measureContent.getAsInt();
    }
}

record LayoutConstraint(Dimension width, Dimension height) {}

enum WrapMode { NoWrap, Wrap, Truncate }
enum Direction { Row, Column }

sealed interface Node permits Box, Text, Flex {}

record Box(LayoutConstraint constraints, Integer child) implements Node {}
record Text(String content, WrapMode wrap) implements Node {}
record FlexChild(int nodeId, int grow, int shrink, Dimension basis) {}
record Flex(Direction axis, int gap, List<FlexChild> children) implements Node {
    Flex {
        children = List.copyOf(children);
    }
}

interface WidgetState {}
record InputState(String buffer, int cursor) implements WidgetState {}

class Arena {
    private int nextId = 0;
    private final Map<Integer, Node> nodes = new HashMap<>();
    private final Map<Integer, WidgetState> widgetStates = new HashMap<>();

    int insert(Node node) {
        int id = nextId++;
        nodes.put(id, node);
        return id;
    }

    Node get(int id) {
        return nodes.get(id);
    }

    void update(int id, Node node) {
        nodes.put(id, node);
    }

    WidgetState getState(int id) {
        return this.widgetStates.get(id);
    }

    void putState(int id, WidgetState state) {
        this.widgetStates.put(id, state);
    }

    void print(int rootId) {
        printNode(rootId, "", true);
    }

    private void printNode(int id, String prefix, boolean isLast) {
        Node node = nodes.get(id);
        System.out.println(prefix + (isLast ? "└── " : "├── ") + "[" + id + "] " + describe(node));

        String childPrefix = prefix + (isLast ? "    " : "│   ");
        List<Integer> childIds = childrenOf(node);
        for (int i = 0; i < childIds.size(); i++) {
            printNode(childIds.get(i), childPrefix, i == childIds.size() - 1);
        }
    }
    private List<Integer> childrenOf(Node node) {
        if (node instanceof Box b) {
            return b.child() != null ? List.of(b.child()) : List.of();
        } else if (node instanceof Flex f) {
            return f.children().stream().map(FlexChild::nodeId).toList();
        } else if (node instanceof Text) {
            return List.of();
        }
        throw new IllegalStateException(
            "unreachable"); // no exhaustiveness check without switch patterns
    }

    private String describe(Node node) {
        if (node instanceof Box b) {
            return "Box { constraints=" + describe(b.constraints()) + " }";
        } else if (node instanceof Text t) {
            return "Text { content=\"" + t.content() + "\", wrap=" + t.wrap() + " }";
        } else if (node instanceof Flex f) {
            return "Flex { axis=" + f.axis() + ", gap=" + f.gap() + " }";
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
}

sealed interface DrawCommand {}
record DrawRectCmd(int x, int y, int width, int height) implements DrawCommand {}
record DrawTextCmd(int x, int y, String text) implements DrawCommand {}

record Size(int width, int height) {}

class Resolver {
    static Size measure(Arena arena, int id, int availableWidth, int AvailableHeight) {
        Node node = arena.get(id);
        if (node instanceof Text t) {
            int w = Math.min(t.content().length(), availableWidth);
            return new Size(w, 1);
        } else if (node instanceof Box b) {
            IntSupplier wis = ()
                -> b.child() != null
                ? measure(arena, b.child(), availableWidth, AvailableHeight).width()
                : 0;

            IntSupplier his = ()
                -> b.child() != null
                ? measure(arena, b.child(), availableWidth, AvailableHeight).height()
                : 0;

            int w = b.constraints().width().resolve(availableWidth, wis);

            int h = b.constraints().height().resolve(AvailableHeight, his);

            return new Size(w, h);
        } else if (node instanceof Flex) {
            return new Size(availableWidth, AvailableHeight);
        }

        return null;
    }

    static void layout(Arena arena, int id, int x, int y, int availableWidth, int AvailableHeight,
        List<DrawCommand> out) {
        Node node = arena.get(id);

        if (node instanceof Text t) {
            out.add(new DrawTextCmd(x, y, t.content()));
        } else if (node instanceof Box b) {
            var size = measure(arena, id, availableWidth, AvailableHeight);
            var cmd = new DrawRectCmd(x, y, size.width(), size.height());
            out.add(cmd);

            if (b.child() != null) {
                layout(arena, b.child(), x, y, size.width(), size.height(), out);
            }
        } else if (node instanceof Flex f) {
            boolean isRow = f.axis() == Direction.Row;
            int mainAvaialable = isRow ? availableWidth : AvailableHeight;

            int[] basis = new int[f.children().size()];
            int totalBasis = 0, totalGrow = 0;
            for (int i = 0; i < f.children().size(); i++) {
                FlexChild fc = f.children().get(i);
                Size s = measure(
                    arena, fc.nodeId(), isRow ? 0 : availableWidth, isRow ? AvailableHeight : 0);
                basis[i] = isRow ? s.width() : s.height();
                totalBasis += basis[i];
                totalGrow += fc.grow();
            }

            int remaining =
                Math.max(0, mainAvaialable - totalBasis - f.gap() * (f.children().size()));

            int cursor = isRow ? x : y;
            for (int i = 0; i < f.children().size(); i++) {
                FlexChild fc = f.children().get(i);
                int extra = totalGrow > 0 ? remaining * fc.grow() / totalGrow : 0;
                int size = basis[i] + extra;
                int cx = isRow ? cursor : x;
                int cy = isRow ? y : cursor;
                int cw = isRow ? size : availableWidth;
                int ch = isRow ? AvailableHeight : size;

                layout(arena, fc.nodeId(), cx, cy, cw, ch, out);
                cursor += size + f.gap();
            }
        }
    }
}

class Input {
    static int create(Arena arena, LayoutConstraint constraint) {
        int textId = arena.insert(new Text("", WrapMode.NoWrap));
        int boxId = arena.insert(new Box(constraint, textId));

        arena.putState(boxId, new InputState("", 0));
        return boxId;
    }

    static void handleKey(Arena arena, int boxId, char c) {
        var s = (InputState) arena.getState(boxId);
        var left = s.buffer().substring(0, s.cursor());
        var right = s.buffer().substring(s.cursor());
        var updated = new InputState(left + c + right, s.cursor() + 1);
        arena.putState(boxId, updated);
        rebuildSubtree(arena, boxId, updated);
    }

    static void handleBackspace(Arena arena, int boxId, char c) {
        var s = (InputState) arena.getState(boxId);
        var left = s.buffer().substring(0, s.cursor() - 1);
        var right = s.buffer().substring(s.cursor());
        var updated = new InputState(left + c + right, s.cursor() - 1);
        arena.putState(boxId, updated);
        rebuildSubtree(arena, boxId, updated);
    }

    private static void rebuildSubtree(Arena arena, int boxId, InputState state) {
        int textId = arena.insert(new Text(state.buffer(), WrapMode.NoWrap));
        Box current = (Box) arena.get(boxId);
        arena.update(boxId, new Box(current.constraints(), textId));
    }
}

public class Rosu {
    public static void test() {}
}
