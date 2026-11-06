package net.gui.primitives.dimension;

import java.util.function.IntSupplier;

public final record Auto() implements Dimension {
    public int resolve(int available, IntSupplier measureContent) {
        return measureContent.getAsInt();
    }
}
