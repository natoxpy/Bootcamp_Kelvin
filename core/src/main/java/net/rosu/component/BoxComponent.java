package net.rosu.component;

import net.gui.Context;
import net.gui.commands.BoxStyle;
import net.gui.primitives.Box;
import net.gui.primitives.LayoutConstraint;
import net.gui.primitives.Overflow;
import net.gui.primitives.dimension.Dimension;
import net.gui.primitives.dimension.Fill;
import net.rosu.Rosu;

public final class BoxComponent implements SystemComponent {
    private Renderable child = null;
    private LayoutConstraint constraint = new LayoutConstraint(new Fill(), new Fill());
    private Overflow overflow = Overflow.Visible;
    private BoxStyle style = new BoxStyle(null, null);
    private OutlineDesign outlineDesign = null;

    public BoxComponent() {}

    public BoxComponent(Renderable child) {
        this.child = child;
    }

    public BoxComponent(Dimension width, Dimension height) {
        this.constraint = new LayoutConstraint(width, height);
    }

    public BoxComponent(Renderable child, LayoutConstraint constraint, Overflow overflow) {
        this.child = child;
        this.constraint = constraint;
        this.overflow = overflow;
    }

    public Renderable getChild() {
        return this.child;
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

    public BoxComponent setStyle(BoxStyle style) {
        this.style = style;
        return this;
    }

	public BoxComponent setOutlineDesign(OutlineDesign od) {
		this.outlineDesign = od;
		return this;
	}

    @Override
    public int reconcileSelf(Context ctx, Rosu rosu, Integer existingId) {
        Box existingBox = existingId != null ? (Box) ctx.get(existingId) : null;
        Integer existingChildId = existingBox != null ? existingBox.child() : null;

        Integer childId = rosu.reconcile(ctx, existingChildId, this.child);
        Box box = new Box(childId, this.overflow, this.constraint, this.style, this.outlineDesign);

        if (existingId == null)
            return ctx.insert(box);
        else
            ctx.update(existingId, box);

        return existingId;
    }
}
