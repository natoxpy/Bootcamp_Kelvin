package net.gui.primitives;

import java.util.ArrayList;
import java.util.List;
import net.gui.Context;
import net.gui.commands.DrawCommand;
import net.gui.commands.Size;

// Center
public final record Flex(Direction axis, int gap, List<FlexChild> children) implements Node {
    public Size measure(Context ctx, int avalWidth, int avalHeight) {
        var isRow = axis == Direction.Row;

        boolean unbounded =
            isRow ? avalWidth == Integer.MAX_VALUE : avalHeight == Integer.MAX_VALUE;

        if (!unbounded) {
            return new Size(avalWidth, avalHeight);
        }

        var mainTotal = 0;
        var crossMax = 0;

        for (int i = 0; i < children.size(); i++) {
            var child = ctx.get(children.get(i).nodeId());

            var childSize = isRow ? child.measure(ctx, Integer.MAX_VALUE, avalHeight)
                                  : child.measure(ctx, avalWidth, Integer.MAX_VALUE);

            var mainSize = isRow ? childSize.width() : childSize.height();
            var crossSize = isRow ? childSize.height() : childSize.width();

            mainTotal += mainSize;
            crossMax = Math.max(crossMax, crossSize);
        }

        var gapTotal = gap * Math.max(0, children.size() - 1);
        mainTotal += gapTotal;

        System.out.println(mainTotal + " " + crossMax);

        return isRow ? new Size(mainTotal, crossMax) : new Size(crossMax, mainTotal);
    }

    public void draw(
        Context ctx, int x, int y, int avalWidth, int avalHeight, List<DrawCommand> cmds) {}
}
