package net.rosu.component;

import java.util.ArrayList;
import java.util.List;

public class FocusManager {
    private final List<Component> focusable = new ArrayList<>();
    private Component focus;

    public Component getFocus() {
        return focus;
    }

    public void setFocus(Component c) {
        if (this.focus == c)
            return;

        Component prev = this.focus;
        this.focus = c;

        if (prev != null)
            prev.redraw();
        if (c != null)
            c.redraw();
    }

    public void register(Component c) {
        if (!focusable.contains(c))
            focusable.add(c);
    }

    public void unregister(Component c) {
        focusable.remove(c);
        if (focus == c)
            focus = null;
    }

    public boolean has(Component c) {
        return this.focusable.contains(c);
    }

    public void focusNext() {
        if (focusable.isEmpty())
            return;

        int idx = focusable.indexOf(focus);
        Component next = focusable.get((idx + 1) % focusable.size());
        setFocus(next);
    }
}
