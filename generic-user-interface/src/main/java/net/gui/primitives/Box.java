package net.gui.primitives;

import java.util.List;
import net.gui.Context;
import net.gui.commands.DrawBoxCommand;
import net.gui.commands.DrawCommand;
import net.gui.commands.Size;

public final record Box(Integer child, LayoutConstraint constraints) implements Node {
    public Size measure(Context ctx, int avalWidth, int avalHeight) {
        Node child = ctx.get(this.child() == null ? -1 : this.child());

        var width = constraints().width().resolve(
            avalWidth, () -> child != null ? child.measure(ctx, avalWidth, avalHeight).width() : 0);

        var height = constraints().height().resolve(avalHeight,
            () -> child != null ? child.measure(ctx, avalWidth, avalHeight).height() : 0);

        return new Size(width, height);
    }

    public void draw(
        Context ctx, int x, int y, int avalWidth, int avalHeight, List<DrawCommand> cmds) {
        Node child = ctx.get(this.child() == null ? -1 : this.child());

        Size size = this.measure(ctx, avalWidth, avalHeight);

        cmds.add(new DrawBoxCommand(x, y, size.width(), size.height()));

        if (child != null)
            child.draw(ctx, x, y, size.width(), size.height(), cmds);
    }
}
