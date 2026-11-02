package net.gui.primitives;

import java.util.List;

public final record Flex(Direction axis, int gap, List<FlexChild> children) implements Node {
    public Flex {
        children = List.copyOf(children);
    }
}
