package net.gui.primitives;

import java.util.List;

import net.gui.Context;
import net.gui.commands.DrawCommand;
import net.gui.commands.Size;

public sealed interface Node permits Text, Box, Flex, Viewport {
    public abstract Size measure(Context ctx, int avalWidth, int avalHeight);
	public abstract boolean isFillAxis(Direction direction);
    public abstract void draw(Context ctx, int x, int y, int avalWidth, int avalHeight, List<DrawCommand> cmds);
}
