package net.rosu.component;

import net.gui.Context;
import net.gui.commands.BoxStyle;
import net.gui.commands.Color;
import net.gui.commands.Size;
import net.gui.primitives.Rect;
import net.gui.primitives.dimension.Dimension;
import net.gui.primitives.dimension.Fill;
import net.gui.primitives.dimension.Fixed;
import net.rosu.IdAware;
import net.rosu.Tickable;

public class ButtonComponent extends Component implements Focusable, IdAware, Clickable, Tickable {
    private Runnable onClick = null;
    private Context ctx;
    private Integer id;
    private String text;
    private Color bgColor;
    private Color fgColor;
    private Dimension width = new Fill();
    private Dimension height = new Fixed(3);
    private OutlineDesign outlineDesign = null;
    private Color bgClicked;
    private long lastClick = 0;
    private boolean wasActive = false;
    private static int clickedEffectDuration = 100;

    public ButtonComponent(String text) {
        this.text = text;
    }

    public ButtonComponent(String text, Runnable onClick) {
        this.text = text;
        this.onClick = onClick;
    }

    public ButtonComponent setColor(Color color) {
        this.fgColor = color;
        return this;
    }

    public ButtonComponent setBackground(Color color) {
        this.bgColor = color;
        return this;
    }

    public ButtonComponent setClickedBg(Color color) {
        this.bgClicked = color;
        return this;
    }

    public ButtonComponent setWidth(Dimension d) {
        this.width = d;
        return this;
    }

    public ButtonComponent setHeight(Dimension d) {
        this.height = d;
        return this;
    }

    public ButtonComponent setOutlineDesign(OutlineDesign od) {
        this.outlineDesign = od;
        return this;
    }

    public ButtonComponent onClick(Runnable onclick) {
        this.onClick = onclick;
        return this;
    }

    public void setBackgroundAndRedraw(Color bg) {
        this.setBackground(bg);
        this.redraw();
    }

    @Override
    public Renderable render() {
        var bg = this.bgColor;

        if (this.wasActive)
            bg = this.bgClicked;

        return new BoxComponent(this.width, this.height)
            .setStyle(new BoxStyle(bg))
            .setOutlineDesign(this.outlineDesign)
            .setChild(
                new Centered(new TextComponent(text).setColor(this.fgColor).setBackground(bg)));
    }

    public void triggerRerender() {
        this.redraw();
    }

    @Override
    public void onIdAssigned(Context ctx, int id) {
        this.ctx = ctx;
        this.id = id;
    }

    @Override
    public void click(int x, int y) {
        if (this.ctx == null)
            return;

        Rect r = this.ctx.drawtime.getRect(this.id);
        if (r == null)
            return;

        Rect p = new Rect(r.x(), r.y(), new Size(r.size().width(), 4));

        if (p.intersects(new Rect(x, y, new Size(0, 0)))) {
            this.lastClick = System.currentTimeMillis();
            this.wasActive = true;
            if (this.onClick != null) {
                this.onClick.run();
            }

            this.redraw();
        }
    }

    @Override
    public void tick(long now) {
        boolean isActive = now - this.lastClick < ButtonComponent.clickedEffectDuration;

        if (this.wasActive && !isActive) {
            this.wasActive = false;
            redraw();
        }
    }
}
