package net.rosu.component;

import net.gui.Context;
import net.gui.primitives.Clamp;
import net.gui.primitives.Node;
import net.gui.primitives.Viewport;
import net.rosu.Rosu;

public final class ViewportComponent implements SystemComponent {
    private final int offsetX;
    private final int offsetY;
    private final Clamp clampX;
    private final Clamp clampY;
    private final Renderable child;

    public ViewportComponent(
        int offsetX, int offsetY, Clamp clampX, Clamp clampY, Renderable child) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.clampX = clampX;
        this.clampY = clampY;
        this.child = child;
    }

    @Override
    public int reconcileSelf(Context ctx, Rosu rosu, Integer existingId) {
        Integer existingChildId = existingId == null ? null : previousChildId(ctx, existingId);
        int childId = rosu.reconcile(ctx, existingChildId, child);

        Viewport node = new Viewport(offsetX, offsetY, clampX, clampY, childId);

        if (existingId == null) {
            return ctx.insert(node);
        }

        ctx.update(existingId, node);
        return existingId;
    }

    private Integer previousChildId(Context ctx, int id) {
        Node n = ctx.get(id);
        return (n instanceof Viewport v) ? v.child() : null;
    }
}
