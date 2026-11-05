package net.rosu.component;

import java.util.ArrayList;
import java.util.List;
import net.gui.Context;
import net.gui.primitives.Direction;
import net.gui.primitives.Flex;
import net.gui.primitives.FlexChild;
import net.rosu.Rosu;

public final class FlexComponent implements SystemComponent {
    private Direction axis = Direction.Row;
    private int gap = 0;
    private List<FlexItem> children = new ArrayList<>();

    public FlexComponent() {}

    public FlexComponent(Direction axis, int gap) {
        this.axis = axis;
        this.gap = gap;
    }

    public FlexComponent setDirection(Direction axis) {
        this.axis = axis;
        return this;
    }

    public FlexComponent setGap(int gap) {
        this.gap = gap;
        return this;
    }
 
    public List<FlexItem> getChildren() {
        return this.children;
    }

    public FlexComponent addChild(FlexItem child) {
        this.children.add(child);
        return this;
    }

    @Override
    public int reconcileSelf(Context ctx, Rosu rosu, Integer existingId) {
        Flex existingFlex = existingId != null ? (Flex) ctx.get(existingId) : null;
        List<FlexChild> oldChildren = existingFlex != null ? existingFlex.children() : List.of();

        List<FlexChild> newChildren = new ArrayList<>();

        for (FlexItem item : this.children) {
            int childId = rosu.reconcile(ctx, null, item.child());
            newChildren.add(new FlexChild(childId, item.grow(), item.justify(), item.align()));
        }

        for (var child : oldChildren) {
            ctx.remove(child.nodeId());
        }

        Flex nflex = new Flex(this.axis, this.gap, newChildren);

        if (existingId == null) {
            return ctx.insert(nflex);
        } else {
            ctx.update(existingId, nflex);
            return existingId;
        }
    }
}
