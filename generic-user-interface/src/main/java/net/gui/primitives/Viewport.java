package net.gui.primitives;

import java.util.List;
import net.gui.Context;
import net.gui.commands.DrawCommand;
import net.gui.commands.Size;

public record Viewport(int offsetX, int offsetY, Integer child) implements Node {
    public Size measure(Context ctx, int avalWidth, int avalHeight) {
        return null;
    }

    public void draw(
        Context ctx, int x, int y, int avalWidth, int avalHeight, List<DrawCommand> cmds) {}
}
