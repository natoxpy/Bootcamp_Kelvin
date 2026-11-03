package net.gui.primitives;

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

        return isRow ? new Size(mainTotal, crossMax) : new Size(crossMax, mainTotal);
    }

    public void draw(
        Context ctx, int x, int y, int avalWidth, int avalHeight, List<DrawCommand> cmds) {
        boolean isRow = this.axis() == Direction.Row;
        int mainAvaialable = (isRow ? avalWidth : avalHeight) - this.gap() * this.children().size();

        int totalGrow = this.children().stream().reduce(
            0, (sum, e) -> sum + (e.grow() > 0 ? e.grow() : 1), Integer::sum);

        totalGrow = totalGrow > 0 ? totalGrow : this.children().size();

        int cursor = isRow ? x : y;
        for (int i = 0; i < this.children().size(); i++) {
            FlexChild flexChild = this.children().get(i);
            Node childNode = ctx.get(flexChild.nodeId());
            int grow = flexChild.grow() > 0 ? flexChild.grow() : 1;
            int size = (int) ((double) mainAvaialable * ((double) grow / (double) totalGrow));

            if (this.children().size() > 0)
                size += (double) this.gap() / (double) this.children().size();

            int cx = isRow ? cursor : x;
            int cy = isRow ? y : cursor;
            int cw = isRow ? size : avalWidth;
            int ch = isRow ? avalHeight : size;

            cursor += size;

            if (i != this.children().size() - 1) {
                cursor += this.gap();
            } else {
                double remaining = (isRow ? (x + avalWidth) : (y + avalHeight)) - cursor;
                if (isRow)
                    cw += remaining;
                else
                    ch += remaining;
            }

            // Alignment
            Size internalSize = childNode.measure(ctx, cw, ch);

            int jcx = cx;
            int jcy = cy;
            int jcw = cw;
            int jch = ch;

            double leftOver = (isRow ? cw - internalSize.width() : ch - internalSize.height());

            if (flexChild.justify() == JustifySelf.Center) {
                leftOver /= 2;

                if (isRow)
                    jcx += leftOver;
                else
                    jcy += leftOver;
            } else if (flexChild.justify() == JustifySelf.End) {
                if (isRow)
                    jcx += leftOver;
                else
                    jcy += leftOver;
            }

            childNode.draw(ctx, jcx, jcy, jcw, jch, cmds);
        }
    }
}
