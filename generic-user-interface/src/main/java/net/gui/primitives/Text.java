package net.gui.primitives;

import java.util.List;
import net.gui.Context;
import net.gui.commands.DrawCommand;
import net.gui.commands.DrawTextCommand;
import net.gui.commands.Size;
import net.gui.commands.TextStyle;

public final record Text(String content, WrapMode wrap, TextStyle style) implements Node {
    public Size measure(Context ctx, int avalWidth, int avalHeight) {
        return new Size(this.content().length(), 1);
    }

    public boolean isFillAxis(Direction direction) {
        return false;
    }

    public void draw(Context ctx, int id, int x, int y, int avalWidth, int avalHeight, Rect bounds,
        List<DrawCommand> cmds) {
        cmds.add(new DrawTextCommand(x, y, this.content(), this.style()));
        ctx.drawtime.updateRect(id, new Rect(x, y, this.measure(ctx, avalWidth, avalHeight)));
    }
}
