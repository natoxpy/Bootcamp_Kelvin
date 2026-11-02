package net.gui.primitives.dimension;

import java.util.function.IntSupplier;

public sealed interface Dimension permits Fixed, Fill, Auto {
    int resolve(int available, IntSupplier measureContent);
}
