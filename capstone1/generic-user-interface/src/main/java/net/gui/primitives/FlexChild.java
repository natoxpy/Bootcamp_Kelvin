package net.gui.primitives;

public record FlexChild(int nodeId, int grow, JustifySelf justify, AlignSelf align) {
    public FlexChild(int nodeId, JustifySelf justify, AlignSelf align) {
        this(nodeId, 0, justify, align);
    }
}
