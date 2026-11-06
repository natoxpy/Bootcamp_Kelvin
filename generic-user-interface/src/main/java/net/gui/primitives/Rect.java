package net.gui.primitives;

import net.gui.commands.Size;

public record Rect(int x, int y, Size size) {
    public boolean contains(int px, int py) {
        return px >= x && px < x + size().width() && py >= y && py < y + size().height();
    }

    public boolean intersects(Rect other) {
        return x < other.x() + other.size().width() && x + size().width() >= other.x()
            && y < other.y() + other.size().height() && y + size().height() > other.y();
    }

    public Rect inflate(int amount) {
        return new Rect(x - amount, y - amount,
            new Size(size().width() + amount * 2, size().height() + amount * 2));
    }

    public Rect intersect(Rect other) {
        int x1 = Math.max(x, other.x()), y1 = Math.max(y, other.y());
        int x2 = Math.min(x + size().width(), other.x() + other.size().width());
        int y2 = Math.min(y + size().height(), other.y() + other.size().height());
        return new Rect(x1, y1, new Size(Math.max(0, x2 - x1), Math.max(0, y2 - y1)));
    }
}
