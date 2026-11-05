package net.rosu.component;

import net.gui.Context;
import net.gui.commands.Size;
import net.gui.primitives.Rect;
import net.gui.primitives.dimension.Fixed;
import net.rosu.IdAware;

public class ButtonComponent extends Component implements Focusable, IdAware, Clickable {
    private Runnable onClick = null;
    private Context ctx;
    private Integer id;
    private String text;

    public ButtonComponent(String text) {
        this.text = text;
    }

    public ButtonComponent(String text, Runnable onClick) {
        this.text = text;
        this.onClick = onClick;
    }

    @Override
    public Renderable render() {
        return new BoxComponent(new Fixed(text.length()), new Fixed(1))
            .setChild(new TextComponent(text));
    }

    @Override
    public void onIdAssigned(Context ctx, int id) {
        this.ctx = ctx;
        this.id = id;
    }

    @Override
    public void click(int x, int y) {
        Rect r = this.ctx.drawtime.getRect(this.id);
        if (r == null)
            return;

        if (r.intersects(new Rect(x, y, new Size(1, 1)))) {
            if (this.onClick != null)
                this.onClick.run();
        }
    }
}
