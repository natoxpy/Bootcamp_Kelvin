package net.gui.commands;

import java.util.List;
import net.gui.primitives.Rect;

public record DrawBoxCommand(int x, int y, int width, int height, BoxStyle boxStyle, Object data)
    implements DrawCommand {

    public DrawBoxCommand(int x, int y, int width, int height, BoxStyle boxStyle) {
        this(x, y, width, height, boxStyle, null);
    }

    public void clip(Rect bounds, List<DrawCommand> out) {
        int x1 = Math.max(x, bounds.x()), y1 = Math.max(y, bounds.y());
        int x2 = Math.min(x + width, bounds.x() + bounds.size().width());
        int y2 = Math.min(y + height, bounds.y() + bounds.size().height());
        if (x2 > x1 && y2 > y1) {
            out.add(new DrawBoxCommand(x1, y1, x2 - x1, y2 - y1, this.boxStyle, this.data));
        }
    }

    public void offset(int x, int y, List<DrawCommand> out) {
        out.add(new DrawBoxCommand(
            this.x() - x, this.y() - y, this.width(), this.height(), this.boxStyle, this.data));
    }
}
