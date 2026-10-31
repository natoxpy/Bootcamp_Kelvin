package net.ecom.ui;

import org.jline.terminal.Size;

/*
 * Due to time constraints this has been set as the point of access
 * to the lower level API BufferMutationLayer. In the future this will
 * be the coordinate-translation layer.
 */
public class Ui {
    private BufferMutationLayer context = null;
    private Ui parent = null;
    private int xOffset = 0, yOffset = 0;

    public Ui(Size size) {
        BufferMutationLayer ctx = new BufferMutationLayer(size.getColumns(), size.getRows());
        this.context = ctx;
    }

    public Ui(Size size, Ui parent, int xOffset, int yOffset) {
        this.parent = parent;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public int getX(int x) {
        return this.xOffset + x;
    }

    public int getY(int y) {
        return this.yOffset + y;
    }

    public void drawRect(int width, int height, int x, int y, char outline, int fill) {
        if (this.parent == null) {
            this.context.drawRect(width, height, this.getX(x), this.getY(y), outline, fill);
            return;
        }

        this.parent.drawRect(width, height, this.getX(x), this.getY(y), outline, fill);
    }

    public void drawText(String txt, int x, int y) {
        if (this.parent == null) {
            this.context.drawText(txt, this.getX(x), this.getY(y));
            return;
        }

        this.parent.drawText(txt, this.getX(x), this.getY(y));
    }

    public Ui window(int w, int h, int x, int y, char border) {
        return this.window(w, h, x, y, border, 0);
    }

    public Ui window(int w, int h, int x, int y, char border, int fill) {
        if (w < 2 || h < 2)
            throw new RuntimeException("Cannot create window smaller than 2 width or 2 height.");

        this.drawRect(w, h, x, y, border, fill);

        return new Ui(Size.of(w - 2, h - 2), this, x + 1, y + 1);
    }

    public Ui windowBorderless(int w, int h, int x, int y) {
        this.drawRect(w, h, x, y, ' ', ' ');

        return new Ui(Size.of(w, h), this, x, y);
    }

    public BufferMutationLayer getContext() {
        if (this.context == null)
            return this.parent.getContext();
        return this.context;
    }
}
