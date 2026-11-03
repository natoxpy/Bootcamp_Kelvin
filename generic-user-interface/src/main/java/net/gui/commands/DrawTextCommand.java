package net.gui.commands;

import java.util.List;
import net.gui.primitives.Rect;

public record DrawTextCommand(int x, int y, String content) implements DrawCommand {
    public void clip(Rect bounds, List<DrawCommand> cmds) {
        int len = Math.min(content().length(), bounds.size().width());
        if (len == 0 || bounds.size().height() == 0)
            return;

        String content = content().substring(0, len);

        cmds.add(new DrawTextCommand(x(), y(), content));
    }

    public void offset(int x, int y, List<DrawCommand> out) {
        out.add(new DrawTextCommand(this.x() - x, this.y() - y, this.content()));
    }
}
