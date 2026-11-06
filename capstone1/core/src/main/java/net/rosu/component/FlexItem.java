package net.rosu.component;

import net.gui.primitives.AlignSelf;
import net.gui.primitives.JustifySelf;

public record FlexItem(Renderable child, JustifySelf justify, AlignSelf align, int grow) {
    public FlexItem(Renderable child) {
        this(child, JustifySelf.Start, AlignSelf.Start, 0);
    }

    public FlexItem(Renderable child, int grow) {
        this(child, JustifySelf.Start, AlignSelf.Start, grow);
    }
}
