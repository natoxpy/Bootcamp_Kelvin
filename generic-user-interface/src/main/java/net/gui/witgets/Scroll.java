package net.gui.witgets;

import net.gui.Context;
import net.gui.commands.Size;
import net.gui.primitives.Clamp;
import net.gui.primitives.Direction;
import net.gui.primitives.Rect;
import net.gui.primitives.Viewport;

public class Scroll {
    public static int create(Context ctx, Integer child) {
        int viewportId = ctx.insert(new Viewport(0, 0, Clamp.Content, Clamp.Content, child));
        ctx.updateState(viewportId, new ScrollState(0, 0));
        return viewportId;
    }

    public static void handleScroll(Context ctx, int id, Direction dir, int offset) {
        ScrollState state = (ScrollState) ctx.getState(id);
        Viewport viewport = (Viewport) ctx.get(id);

        int offsetX = dir == Direction.Row ? state.offsetX() + offset : state.offsetX();
        int offsetY = dir == Direction.Column ? state.offsetY() + offset : state.offsetY();

        Rect resolved = ctx.drawtime.getRect(id);

        if (resolved != null
            && (viewport.clampX() == Clamp.Content || viewport.clampY() == Clamp.Content)) {
            Size viewportSize = resolved.size();
            Size contentSize;

            if (viewport.child() == null)
                contentSize = new Size(0, 0);
            else
                contentSize =
                    ctx.get(viewport.child()).measure(ctx, Integer.MAX_VALUE, Integer.MAX_VALUE);

            if (viewport.clampX() == Clamp.Content) {
                int maxX = Math.max(0, contentSize.width() - viewportSize.width());
                offsetX = Math.max(0, Math.min(offsetX, maxX));
            }
            if (viewport.clampY() == Clamp.Content) {
                int maxY = Math.max(0, contentSize.height() - viewportSize.height());
                offsetY = Math.max(0, Math.min(offsetY, maxY));
            }
        }

        ScrollState newState = new ScrollState(offsetX, offsetY);
        ctx.updateState(id, newState);
        Scroll.reconstruct(ctx, id, newState);
    }

    private static void reconstruct(Context ctx, int id, ScrollState newState) {
        Viewport currentViewport = (Viewport) ctx.get(id);
        Viewport newViewport = new Viewport(newState.offsetX(), newState.offsetY(),
            currentViewport.clampX(), currentViewport.clampY(), currentViewport.child());

        ctx.update(id, newViewport);
    }
}
