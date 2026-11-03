package net.gui.primitives;

import java.util.List;
import net.gui.Context;
import net.gui.commands.DrawCommand;
import net.gui.commands.DrawTextCommand;
import net.gui.commands.Size;

public final record Text(String content, WrapMode wrap) implements Node {
    public Size measure(Context ctx, int avalWidth, int avalHeight) {
        return new Size(this.content().length(), 1);
    }

    public boolean isFillAxis(Direction direction) {
        return false;
    }

    public void draw(
        Context ctx, int x, int y, int avalWidth, int avalHeight, Rect bounds, List<DrawCommand> cmds) {
        cmds.add(new DrawTextCommand(x, y, this.content()));
    }
}
