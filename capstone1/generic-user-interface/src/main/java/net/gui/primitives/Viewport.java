package net.gui.primitives;

import java.util.ArrayList;
import java.util.List;
import net.gui.Context;
import net.gui.commands.DrawCommand;
import net.gui.commands.Size;

// Measurement: Boundless
// Compute Size: Fill
public record Viewport(int offsetX, int offsetY, Clamp clampX, Clamp clampY, Integer child)
    implements Node {
    public Size measure(Context ctx, int avalWidth, int avalHeight) {
        Node child = ctx.get(this.child() == null ? -1 : this.child());

        boolean widthUnbounded = avalWidth == Integer.MAX_VALUE;
        boolean heightUnbounded = avalHeight == Integer.MAX_VALUE;

        if (widthUnbounded || heightUnbounded) {
            return child.measure(ctx, avalWidth, avalHeight);
        }

        return new Size(avalWidth, avalHeight);
    }

    public boolean isFillAxis(Direction direction) {
        return true;
    }

    public void draw(Context ctx, int viewportId, int absX, int absY, int avalWidth, int avalHeight,
        Rect bounds, List<DrawCommand> cmds) {
        if (this.child() == null)
            return;

        Node nodeChild = ctx.get(this.child());

        List<DrawCommand> childCmds = new ArrayList<>();
        nodeChild.draw(ctx, this.child(), absX, absY, avalWidth, avalHeight, bounds, childCmds);

        int childOffsetX;
        int childOffsetY;

        if (clampX == Clamp.Content)
            childOffsetX = Math.max(0,
                Math.min(offsetX,
                    nodeChild.measure(ctx, Integer.MAX_VALUE, Integer.MAX_VALUE).width()
                        - bounds.size().width()));
        else
            childOffsetX = this.offsetX();

        if (clampY == Clamp.Content) {
            childOffsetY = Math.max(0,
                Math.min(offsetY,
                    nodeChild.measure(ctx, Integer.MAX_VALUE, Integer.MAX_VALUE).height()
                        - bounds.size().height()));
        } else {
            childOffsetY = this.offsetY();
        }

        childCmds.forEach((cmd) -> cmd.offset(childOffsetX, childOffsetY, cmds));
		ctx.drawtime.updateRect(viewportId, new Rect(absX, absY, new Size(avalWidth, avalHeight)));
    }
}
