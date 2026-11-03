package net.gui.commands;

import java.util.List;
import net.gui.primitives.Rect;

public record DrawTextCommand(int x, int y, String content) implements DrawCommand {
    public void clip(Rect bounds, List<DrawCommand> cmds) {
        int len = Math.min(content().length(), bounds.size().width());
        if (len == 0)
            return;

        String content = content().substring(0, len);

        cmds.add(new DrawTextCommand(x(), y(), content));
    }
}
