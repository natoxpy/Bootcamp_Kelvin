package net.rosu.component;

import net.gui.Context;
import net.gui.commands.Color;
import net.gui.commands.TextStyle;
import net.gui.primitives.Text;
import net.gui.primitives.WrapMode;
import net.rosu.Rosu;

public final class TextComponent implements SystemComponent {
    private final String text;
    private Color color = null;
    private Color background = null;

    public TextComponent(String text) {
        this.text = text;
    }

    public TextComponent setColor(Color color) {
        this.color = color;
        return this;
    }

    public TextComponent setBackground(Color color) {
        this.background = color;
        return this;
    }

    @Override
    public int reconcileSelf(Context ctx, Rosu rosu, Integer existingId) {
        if (existingId == null) {
            int id = ctx.insert(
                new Text(this.text, WrapMode.NoWrap, new TextStyle(this.color, this.background)));
            return id;
        } else {
            Text t = (Text) ctx.get(existingId);
            ctx.update(existingId,
                new Text(this.text, t.wrap(), new TextStyle(this.color, this.background)));
            return existingId;
        }
    }
}
