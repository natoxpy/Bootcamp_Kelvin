package net.rosu.component;

import net.gui.Context;
import net.gui.commands.Size;
import net.gui.primitives.Clamp;
import net.gui.primitives.Node;
import net.gui.primitives.Rect;
import net.gui.primitives.Viewport;
import net.rosu.IdAware;

public class ScrollableComponent extends Component implements Focusable, IdAware {
    private static final int SCROLL_STEP = 1;

    private final Renderable child;
    private int offsetX = 0;
    private int offsetY = 0;

    private Context ctx;
    private Integer viewportId;

    public ScrollableComponent(Renderable child) {
        this.child = child;
    }

    @Override
    public Renderable render() {
        return new ViewportComponent(offsetX, offsetY, Clamp.NoClamp, Clamp.NoClamp, child);
    }

    @Override
    public void onKey(char key) {
        switch (key) {
            case 'k' -> scrollBy(0, -SCROLL_STEP);
            case 'j' -> scrollBy(0, SCROLL_STEP);
            case 'h' -> scrollBy(-SCROLL_STEP, 0);
            case 'l' -> scrollBy(SCROLL_STEP, 0);
        }
    }

    @Override
    public void onIdAssigned(Context ctx, int id) {
        this.ctx = ctx;
        this.viewportId = id;
    }

    private void scrollBy(int dx, int dy) {
        int newX = offsetX + dx;
        int newY = offsetY + dy;

        if (ctx != null && viewportId != null) {
            Viewport viewport = (Viewport) ctx.get(viewportId);
            Rect resolved = ctx.drawtime.getRect(viewportId);

            if (resolved != null && viewport.child() != null) {
                Size viewportSize = resolved.size();
                Node contentNode = ctx.get(viewport.child());
                Size contentSize = contentNode.measure(ctx, Integer.MAX_VALUE, Integer.MAX_VALUE);

                int maxX = Math.max(0, contentSize.width() - viewportSize.width());
                int maxY = Math.max(0, contentSize.height() - viewportSize.height());

                newX = Math.max(0, Math.min(newX, maxX));
                newY = Math.max(0, Math.min(newY, maxY));
            }
        }

        offsetX = newX;
        offsetY = newY;
        redraw();
    }
}
