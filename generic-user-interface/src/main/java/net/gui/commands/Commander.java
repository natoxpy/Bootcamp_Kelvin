package net.gui.commands;

import java.util.ArrayList;
import java.util.List;
import net.gui.Context;
import net.gui.primitives.Box;
import net.gui.primitives.Direction;
import net.gui.primitives.Flex;
import net.gui.primitives.FlexChild;
import net.gui.primitives.JustifySelf;
import net.gui.primitives.Node;
import net.gui.primitives.Text;
import net.gui.primitives.Viewport;
import net.gui.primitives.WrapMode;

public class Commander {
    // private static Size measureTextTrueSize(
    //     Context ctx, Text text, int availableWidth, int availableHeight) {
    //     return new Size(text.content().length(), 1);
    // }

    // private static Size measureBoxTrueSize(
    //     Context ctx, Box box, int boxId, int availableWidth, int availableHeight) {
    //     Size resolved = Commander.measureBox(ctx, box, availableWidth, availableHeight);
    //     Size internal;

    //     if (box.child() != null) {
    //         internal = Commander.measureTrueSize(ctx, box.child(), availableWidth,
    //         availableHeight);
    //     } else
    //         internal = new Size(0, 0);

    //     var ret = new Size(Math.max(resolved.width(), internal.width()),
    //         Math.max(resolved.height(), internal.height()));

    //     return ret;
    // }

    // private static Size measureTrueSize(
    //     Context ctx, int id, int availableWidth, int availableHeight) {
    //     Node node = ctx.get(id);

    //     if (node instanceof Text text) {
    //         return Commander.measureTextTrueSize(ctx, text, availableWidth, availableHeight);
    //     } else if (node instanceof Box box) {
    //         return Commander.measureBoxTrueSize(ctx, box, id, availableWidth, availableHeight);
    //     } else if (node instanceof Flex flex) {
    //         return Commander.measureFlex(ctx, flex, availableWidth, availableHeight);
    //     } else if (node instanceof Viewport viewport) {
    //         return Commander.measureViewport(ctx, viewport, availableWidth, availableHeight);
    //     } else {
    //         throw new IllegalStateException("unreachable");
    //     }
    // }

    // private static Size measureText(Context ctx, Text text, int availableWidth) {
    //     int width = Math.min(text.content().length(), availableWidth);
    //     int height = 1;
    //     return new Size(width, height);
    // }

    // private static Size measureBox(Context ctx, Box box, int availableWidth, int availableHeight)
    // {
    //     Size internal;

    //     if (box.child() != null)
    //         internal = Commander.measure(ctx, box.child(), availableWidth, availableHeight);
    //     else
    //         internal = new Size(0, 0);

    //     int width = box.constraints().width().resolve(availableWidth, () -> internal.width());
    //     int height = box.constraints().height().resolve(availableHeight, () ->
    //     internal.height());

    //     return new Size(width, height);
    // }

    // private static Size measureFlex(
    //     Context ctx, Flex flex, int availableWidth, int availableHeight) {
    //     System.out.printf("measureFlex %s - %s\n", availableWidth, availableHeight);

    //     return new Size(availableWidth, availableHeight);
    // }

    // private static Size measureViewport(
    //     Context ctx, Viewport viewport, int availableWidth, int availableHeight) {
    //     return new Size(availableWidth, availableHeight);
    // }

    // private static Size measure(Context ctx, int id, int availableWidth, int availableHeight) {
    //     Node node = ctx.get(id);

    //     if (node instanceof Text text) {
    //         return Commander.measureText(ctx, text, availableWidth);
    //     } else if (node instanceof Box box) {
    //         return Commander.measureBox(ctx, box, availableWidth, availableHeight);
    //     } else if (node instanceof Flex flex) {
    //         return Commander.measureFlex(ctx, flex, availableWidth, availableHeight);
    //     } else if (node instanceof Viewport viewport) {
    //         return Commander.measureViewport(ctx, viewport, availableWidth, availableHeight);
    //     }

    //     throw new IllegalStateException("unreachable");
    // }

    // private static void drawText(Context ctx, Text text, int id, int x, int y, int
    // availableWidth,
    //     int availableHeight, List<DrawCommand> commandList) {
    //     Size size = Commander.measure(ctx, id, availableWidth, availableHeight);
    //     String content = text.content().substring(0, size.width());
    //     DrawTextCommand command = new DrawTextCommand(x, y, content);
    //     commandList.add(command);
    // }

    // private static void drawBox(Context ctx, Box box, int x, int y, int availableWidth,
    //     int availableHeight, List<DrawCommand> commandList) {
    //     Size size = Commander.measureBox(ctx, box, availableWidth, availableHeight);
    //     DrawBoxCommand command = new DrawBoxCommand(x, y, size.width(), size.height());
    //     commandList.add(command);

    //     if (box.child() != null)
    //         Commander.compileCommands(
    //             ctx, box.child(), x, y, size.width(), size.height(), commandList);
    // }

    // private static void flexCommand(Context ctx, Flex flex, int x, int y, int availableWidth,
    //     int availableHeight, List<DrawCommand> commandList) {
    //     boolean isRow = flex.axis() == Direction.Row;
    //     int mainAvaialable =
    //         (isRow ? availableWidth : availableHeight) - flex.gap() * flex.children().size();

    //     int totalGrow = flex.children().stream().reduce(
    //         0, (sum, e) -> sum + (e.grow() > 0 ? e.grow() : 1), Integer::sum);

    //     totalGrow = totalGrow > 0 ? totalGrow : flex.children().size();

    //     int cursor = isRow ? x : y;
    //     for (int i = 0; i < flex.children().size(); i++) {
    //         FlexChild flexChild = flex.children().get(i);
    //         int grow = flexChild.grow() > 0 ? flexChild.grow() : 1;
    //         int size = (int) ((double) mainAvaialable * ((double) grow / (double) totalGrow));

    //         if (flex.children().size() > 0)
    //             size += (double) flex.gap() / (double) flex.children().size();

    //         int cx = isRow ? cursor : x;
    //         int cy = isRow ? y : cursor;
    //         int cw = isRow ? size : availableWidth;
    //         int ch = isRow ? availableHeight : size;

    //         cursor += size;

    //         if (i != flex.children().size() - 1) {
    //             cursor += flex.gap();
    //         } else {
    //             double remaining = (isRow ? availableWidth : availableHeight) - cursor;
    //             if (isRow)
    //                 cw += remaining;
    //             else
    //                 ch += remaining;
    //         }

    //         // Alignment
    //         Size internalSize = Commander.measure(ctx, flexChild.nodeId(), cw, ch);

    //         int jcx = cx;
    //         int jcy = cy;
    //         int jcw = cw;
    //         int jch = ch;

    //         double leftOver = (isRow ? cw - internalSize.width() : ch - internalSize.height());

    //         System.out.printf("[%s, %s] %s - %s\n", cw, ch, leftOver, flex.gap());

    //         if (flexChild.justify() == JustifySelf.Center) {
    //             leftOver /= 2;

    //             if (isRow)
    //                 jcx += leftOver;
    //             else
    //                 jcy += leftOver;
    //         } else if (flexChild.justify() == JustifySelf.End) {
    //             if (isRow)
    //                 jcx += leftOver;
    //             else
    //                 jcy += leftOver;
    //         }

    //         Commander.compileCommands(
    //             ctx, flexChild.nodeId(), jcx, jcy, jcw - jcx, jch - jcy, commandList);
    //     }
    // }

    // private static boolean overlap(int p1, int s1, int p2, int s2) {
    //     return p1 + s1 > p2 && p1 < s2 && (s1 != 0 || s2 != 0);
    // }

    // private static boolean outOfBounds(
    //     DrawCommand cmd, int x, int y, int availableWidth, int availableHeight) {
    //     if (cmd instanceof DrawTextCommand text) {
    //         if (text.content().length() == 0)
    //             return true;

    //         int textWidth = text.content().length();

    //         return !(Commander.overlap(text.x(), textWidth, x, availableWidth)
    //             && Commander.overlap(text.y(), 1, y, availableHeight));
    //     } else if (cmd instanceof DrawBoxCommand box) {
    //         if (box.width() <= 0 || box.height() <= 0)
    //             return true;

    //         return !(Commander.overlap(box.x(), box.width(), x, availableWidth)
    //             || Commander.overlap(box.y(), box.height(), y, availableHeight));
    //     } else {
    //         throw new IllegalStateException("unreachable");
    //     }
    // }

    // private static void viewportCommand(Context ctx, Viewport flex, int x, int y,
    //     int availableWidth, int availableHeight, List<DrawCommand> commandList) {
    //     List<DrawCommand> flexCommandList = new ArrayList<>();

    //     if (flex.child() != null)
    //         Commander.compileCommands(
    //             ctx, flex.child(), x, y, Integer.MAX_VALUE, Integer.MAX_VALUE, flexCommandList);

    //     for (var cmd : flexCommandList) {
    //         if (cmd instanceof DrawTextCommand text) {
    //             int newX = text.x() - flex.offsetX();
    //             int newY = text.y() - flex.offsetY();

    //             Size size =
    //                 measureText(ctx, new Text(text.content(), WrapMode.NoWrap), availableWidth);

    //             var newCommand =
    //                 new DrawTextCommand(newX, newY, text.content().substring(0, size.width()));

    //             if (!Commander.outOfBounds(newCommand, x, y, availableWidth, availableHeight)) {
    //                 commandList.add(newCommand);
    //             }
    //         } else if (cmd instanceof DrawBoxCommand box) {
    //             int newX = box.x() - flex.offsetX();
    //             int newY = box.y() - flex.offsetY();

    //             int newWidth = Math.min(newX + box.width(), availableWidth);
    //             int newHeight = Math.min(newY + box.height(), availableHeight);

    //             var newCommand = new DrawBoxCommand(x, y, newWidth, newHeight);
    //             if (!Commander.outOfBounds(newCommand, x, y, availableWidth, availableHeight))
    //                 commandList.add(newCommand);
    //         } else {
    //             throw new IllegalStateException("unreachable");
    //         }
    //     }
    // }

    private static void drawText(Context ctx, int id, int x, int y, int availableWidth,
        int availableHeight, List<DrawCommand> commandList) {}

    private static void compileCommands(Context ctx, int id, int x, int y, int availableWidth,
        int availableHeight, List<DrawCommand> commandList) {
        ctx.get(id).draw(ctx, x, y, availableWidth, availableHeight, commandList);

        // if (availableWidth < 0 || availableHeight < 0)
        //     return;

        // if (node instanceof Text text) {
        //     Commander.drawText(ctx, text, id, x, y, availableWidth, availableHeight,
        //     commandList);
        // } else if (node instanceof Box box) {
        //     // Commander.drawBox(ctx, box, x, y, availableWidth, availableHeight, commandList);
        // } else if (node instanceof Flex flex) {
        //     // Commander.flexCommand(ctx, flex, x, y, availableWidth, availableHeight,
        //     commandList);
        // } else if (node instanceof Viewport viewport) {
        //     // Commander.viewportCommand(ctx, viewport, x, y, availableWidth, availableHeight,
        //     // commandList);
        // } else {
        //     throw new IllegalStateException("unreachable");
        // }
    }

    public static void compileCommands(
        Context ctx, int root, int width, int height, List<DrawCommand> commandList) {
        Commander.compileCommands(ctx, root, 0, 0, width, height, commandList);
    }
}
