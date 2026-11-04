package net.gui.commands;

import java.util.List;
import net.gui.primitives.Rect;

public record DrawTextCommand(int x, int y, String content) implements DrawCommand {
    public void clip(Rect bounds, List<DrawCommand> cmds) {
        int boundsLeft = bounds.x();
        int boundsTop = bounds.y();
        int boundsRight = bounds.x() + bounds.size().width();
        int boundsBottom = bounds.y() + bounds.size().height();

        if (y() < boundsTop || y() >= boundsBottom || bounds.size().width() <= 0
            || bounds.size().height() <= 0) {
            return;
        }

        int textLeft = x();
        int textRight = x() + content().length();

        if (textRight <= boundsLeft || textLeft >= boundsRight) {
            return;
        }

        int visibleStart = Math.max(textLeft, boundsLeft);
        int visibleEnd = Math.min(textRight, boundsRight);

        int substrStart = visibleStart - textLeft;
        int substrEnd = visibleEnd - textLeft;

        String visibleContent = content().substring(substrStart, substrEnd);

        cmds.add(new DrawTextCommand(visibleStart, y(), visibleContent));
    }

    public void offset(int x, int y, List<DrawCommand> out) {
        out.add(new DrawTextCommand(this.x() - x, this.y() - y, this.content()));
    }
}
