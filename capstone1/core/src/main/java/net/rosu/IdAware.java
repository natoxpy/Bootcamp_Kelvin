package net.rosu;

import net.gui.Context;

public interface IdAware {
    void onIdAssigned(Context ctx, int id);
}
