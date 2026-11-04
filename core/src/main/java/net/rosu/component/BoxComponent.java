package net.rosu.component;

import net.gui.Context;
import net.gui.primitives.Box;
import net.gui.primitives.LayoutConstraint;
import net.gui.primitives.Overflow;
import net.gui.primitives.dimension.Dimension;
import net.gui.primitives.dimension.Fill;
import net.rosu.Rosu;

public final class BoxComponent implements SystemComponent {
    public Renderable child = null;
    public LayoutConstraint constraint = new LayoutConstraint(new Fill(), new Fill());
    public Overflow overflow = Overflow.Visible;

    public BoxComponent() {}

    public BoxComponent(Dimension width, Dimension height) {
        this.constraint = new LayoutConstraint(width, height);
    }

    public BoxComponent(Renderable child, LayoutConstraint constraint, Overflow overflow) {
        this.child = child;
        this.constraint = constraint;
        this.overflow = overflow;
    }

    public BoxComponent setChild(Renderable child) {
        this.child = child;
        return this;
    }

    public BoxComponent setConstraint(LayoutConstraint constraint) {
        this.constraint = constraint;
        return this;
    }

    public BoxComponent setWidth(Dimension width) {
        this.constraint = new LayoutConstraint(width, this.constraint.height());
        return this;
    }

    public BoxComponent setHeight(Dimension height) {
        this.constraint = new LayoutConstraint(this.constraint.width(), height);
        return this;
    }

    public BoxComponent setOverflow(Overflow overflow) {
        this.overflow = overflow;
        return this;
    }

    @Override
    public int reconcileSelf(Context ctx, Rosu rosu, Integer existingId) {
        Box existingBox = existingId != null ? (Box) ctx.get(existingId) : null;
        Integer existingChildId = existingBox != null ? existingBox.child() : null;

        Integer childId = rosu.reconcile(ctx, existingChildId, this.child);
        Box box = new Box(childId, this.overflow, this.constraint);

        if (existingId == null)
            return ctx.insert(box);
        else
            ctx.update(existingId, box);

        return existingId;
    }
}
