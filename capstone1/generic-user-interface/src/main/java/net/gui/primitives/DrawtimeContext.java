package net.gui.primitives;

import java.util.HashMap;
import java.util.Map;

public class DrawtimeContext {
    public final Map<Integer, Rect> resolvedRects = new HashMap<>();

    protected void updateRect(int id, Rect rect) {
        resolvedRects.put(id, rect);
    }

    public Rect getRect(int id) {
        return this.resolvedRects.get(id);
    }
}
