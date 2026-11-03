package net.rosu.component;

import net.gui.Context;

public sealed abstract interface SystemComponent extends Renderable permits TextComponent {
    int reconcileSelf(Context ctx, Integer existingId);
}
