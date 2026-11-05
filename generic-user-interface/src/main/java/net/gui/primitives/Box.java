package net.gui.primitives;

import java.util.ArrayList;
import java.util.List;
import net.gui.Context;
import net.gui.commands.BoxStyle;
import net.gui.commands.DrawBoxCommand;
import net.gui.commands.DrawCommand;
import net.gui.commands.Size;
import net.gui.primitives.dimension.Fill;

public final record Box(Integer child, Overflow overflow, LayoutConstraint constraints,
    BoxStyle boxstyle, Object data) implements Node {
    public Box(Integer child, Overflow overflow, LayoutConstraint constraints, BoxStyle boxstyle) {
        this(child, overflow, constraints, boxstyle, null);
    }

    public Size measure(Context ctx, int avalWidth, int avalHeight) {
        Node child = ctx.get(this.child() == null ? -1 : this.child());

        var width = constraints().width().resolve(
            avalWidth, () -> child != null ? child.measure(ctx, avalWidth, avalHeight).width() : 0);

        var height = constraints().height().resolve(avalHeight,
            () -> child != null ? child.measure(ctx, avalWidth, avalHeight).height() : 0);

        return new Size(width, height);
    }

    public boolean isFillAxis(Direction direction) {
        if (direction == Direction.Row) {
            return this.constraints().width() instanceof Fill;
        } else {
            return this.constraints().height() instanceof Fill;
        }
    }

    public void draw(Context ctx, int id, int absX, int absY, int avalWidth, int avalHeight,
        Rect bounds, List<DrawCommand> cmds) {
        Node child = ctx.get(this.child() == null ? -1 : this.child());

        Size size = this.measure(ctx, avalWidth, avalHeight);

        cmds.add(new DrawBoxCommand(absX, absY, size.width(), size.height(), boxstyle, this.data));

        ctx.drawtime.updateRect(id, new Rect(absX, absY, size));

        if (child == null)
            return;

        List<DrawCommand> childCmds = new ArrayList<>();
        child.draw(ctx, this.child(), absX, absY, size.width(), size.height(),
            new Rect(absX, absY, size), childCmds);

        if (this.overflow == Overflow.Hidden)
            childCmds.forEach((c) -> c.clip(new Rect(absX, absY, size), cmds));
        else
            cmds.addAll(childCmds);
    }
}
