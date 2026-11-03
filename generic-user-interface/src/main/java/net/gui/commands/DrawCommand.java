package net.gui.commands;

import java.util.List;
import net.gui.primitives.Rect;

public sealed interface DrawCommand permits DrawTextCommand, DrawBoxCommand {
    public abstract void clip(Rect bounds, List<DrawCommand> out);
}
