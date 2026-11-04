package net.rosu.component;

import net.gui.Context;
import net.gui.primitives.Text;
import net.gui.primitives.WrapMode;
import net.rosu.Rosu;

public final class TextComponent implements SystemComponent {
    private final String text;

    public TextComponent(String text) {
        this.text = text;
    }

    @Override
    public int reconcileSelf(Context ctx, Rosu rosu, Integer existingId) {
        if (existingId == null) {
            int id = ctx.insert(new Text(this.text, WrapMode.NoWrap));
            return id;
        } else {
            Text t = (Text) ctx.get(existingId);
            ctx.update(existingId, new Text(this.text, t.wrap()));
            return existingId;
        }
    }
}
