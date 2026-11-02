package net.gui.primitives.dimension;

import java.util.function.IntSupplier;

public final record Fixed(int value) implements Dimension {
    public int resolve(int available, IntSupplier measureContent) {
        return value;
    }
}
