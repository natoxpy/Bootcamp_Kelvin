package net.rosu.component;

import net.gui.Context;
import net.rosu.Rosu;

public sealed abstract interface SystemComponent
    extends Renderable permits TextComponent, BoxComponent, FlexComponent {
    int reconcileSelf(Context ctx, Rosu rosu, Integer existingId);
}
