package net.ecom.components;

import java.util.ArrayList;
import net.ecom.ui.Ui;

public class Window extends UiComponent<Window> {
    private int w, h;
    private char border = '#';

    public Window() {
        super(0, 0);
    }

    public Window(int w, int h) {
        super(0, 0);
        this.w = w;
        this.h = h;
    }

    public Window(int w, int h, int x, int y, char border) {
        super(x, y);
        this.w = w;
        this.h = h;
        this.border = border;
    }

    public int getW() {
        return this.w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return this.h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public char getBorder() {
        return this.border;
    }

    public void setBorder(char border) {
        this.border = border;
    }

    public void propagateBorder() {
        for (var children : this.getChildren()) {
            if (children instanceof Window) {
				((Window)children).setBorder(this.border);
				((Window)children).propagateBorder();
            }
        }
    }

    public UiComponent<Window> updateUi(ComponentUpdate<Window> f) {
        f.update(this);
        return this;
    }

    public void createUi(Ui ui) {
        Ui parent = ui.window(this.getW(), this.getH(), this.getX(), this.getY(), this.border);
        super.createUi(parent);
    }

    @Override
    public void addChildren(UiComponent<? extends UiComponent<?>> c) {
        if (c instanceof Window) {
            ((Window) c).setBorder(this.border);
        }

        super.addChildren(c);
    }
}
