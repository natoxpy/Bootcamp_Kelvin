package net.rosu.component;

public non-sealed abstract class Component implements Renderable {
    private boolean dirty = false;
    private Renderable cached;
    private FocusManager focusManager;

    public abstract Renderable render();
    public void onKey(char key){};

    public void attachFocusManager(FocusManager fm) {
        this.focusManager = fm;
    }

    protected final boolean isFocused() {
        return focusManager != null && focusManager.getFocus() == this;
    }

    public void requestFocus() {
        if (focusManager != null) {
            focusManager.setFocus(this);
        }
    }

    public final void defocus() {
        if (focusManager != null)
            focusManager.requestDefocus(this);
    }

    protected final void redraw() {
        this.dirty = true;
    }

    public final Renderable getRenderable() {
        if (dirty || cached == null) {
            if (cached != null && cached instanceof Focusable) {
                if (cached instanceof Component c)
                    this.focusManager.unregister(c);
            }
            cached = render();
            dirty = false;
        }

        return cached;
    }
}
