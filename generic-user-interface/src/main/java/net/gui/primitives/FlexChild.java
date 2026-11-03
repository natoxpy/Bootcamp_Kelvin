package net.gui.primitives;

public record FlexChild(int nodeId, int grow, JustifySelf justify) {
    public FlexChild(int nodeId, JustifySelf justify) {
        this(nodeId, 0, justify);
    }
}
