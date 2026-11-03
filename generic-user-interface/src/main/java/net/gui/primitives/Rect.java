package net.gui.primitives;

import net.gui.commands.Size;

public record Rect(int x, int y, Size size) {
    Rect intersect(Rect other) {
        int x1 = Math.max(x, other.x()), y1 = Math.max(y, other.y());
        int x2 = Math.min(x + size().width(), other.x() + other.size().width());
        int y2 = Math.min(y + size().height(), other.y() + other.size().height());
        return new Rect(x1, y1, new Size(Math.max(0, x2 - x1), Math.max(0, y2 - y1)));
    }
}
