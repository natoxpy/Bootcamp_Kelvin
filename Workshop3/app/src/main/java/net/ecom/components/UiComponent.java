package net.ecom.components;

import java.util.ArrayList;
import net.ecom.ui.Ui;

public abstract class UiComponent<T extends UiComponent<T>> {
    private int x, y;
    // TODO: Reconsider Position of Children
    private ArrayList<UiComponent<? extends UiComponent<?>>> children =
        new ArrayList<UiComponent<? extends UiComponent<?>>>();

    public UiComponent(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void addChildren(UiComponent<? extends UiComponent<?>> c) {
        this.children.add(c);
    }

    public void removeChildren(UiComponent<? extends UiComponent<?>> c) {
        this.children.remove(c);
    }

    public void clearChildren() {
        this.children.clear();
    }

    public ArrayList<? extends UiComponent<?>> getChildren() {
        return this.children;
    }

    public void createUi(Ui ui) {
        for (UiComponent<? extends UiComponent<?>> children : this.getChildren()) {
            children.createUi(ui);
        }
    }

    abstract public UiComponent<T> updateUi(ComponentUpdate<T> update);

    public static int getCenter(int parentLength, int childLength) {
        return parentLength / 2 - childLength / 2;
    }

    public static <B extends UiComponent<B>> ComponentUpdate<B> SetPosition(int x, int y) {
        ComponentUpdate<B> f = (c) -> {
            c.setX(x);
            c.setY(y);
        };

        return f;
    }

    public static <B extends UiComponent<B>> ComponentUpdate<B> SetX(int x) {
        ComponentUpdate<B> f = (c) -> {
            c.setX(x);
        };

        return f;
    }

    public static <B extends UiComponent<B>> ComponentUpdate<B> SetY(int y) {
        ComponentUpdate<B> f = (c) -> {
            c.setY(y);
        };

        return f;
    }
}
