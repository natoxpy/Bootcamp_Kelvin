package net.gui.primitives;

import java.util.List;
import net.gui.Context;
import net.gui.commands.DrawCommand;
import net.gui.commands.Size;

// Measurement: Boundless
// Compute Size: Fill
public record Viewport(int offsetX, int offsetY, Integer child) implements Node {
    public Size measure(Context ctx, int avalWidth, int avalHeight) {
        return new Size(avalWidth, avalHeight);
    }

    public boolean isFillAxis(Direction direction) {
        return true;
    }

    public void draw(Context ctx, int x, int y, int avalWidth, int avalHeight, Rect bounds,
        List<DrawCommand> cmds) {}
}
