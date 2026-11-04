package net.rosu.component;

import net.gui.primitives.AlignSelf;
import net.gui.primitives.JustifySelf;

public record FlexItem(Renderable child, JustifySelf justify, AlignSelf align) {}
