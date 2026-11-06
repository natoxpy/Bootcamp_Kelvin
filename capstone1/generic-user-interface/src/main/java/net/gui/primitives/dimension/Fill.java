package net.gui.primitives.dimension;

import java.util.function.IntSupplier;

public final record Fill() implements Dimension {
    public int resolve(int available, IntSupplier measureContent) {
        if (available == Integer.MAX_VALUE)
            return measureContent.getAsInt();

        return available;
    }
}
