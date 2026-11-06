package net.rosu.component;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;
import net.gui.Context;
import net.gui.primitives.Rect;
import net.gui.primitives.dimension.Auto;
import net.gui.primitives.dimension.Dimension;
import net.gui.primitives.dimension.Fill;
import net.rosu.IdAware;
import net.rosu.Tickable;

public class InputComponent extends Component implements Focusable, Tickable, IdAware, Clickable {
    private String buffer = "";
    private int cursor = 0;
    private String regex = "[a-zA-Z0-9\\s\\-\\.\\']";
    private Function<String, Boolean> validator = null;
    private Consumer<String> onInput = null;
    private Context ctx;
    private Integer id;
    private boolean cursorVisible = true;
    private long lastBlinkToggle = System.currentTimeMillis();
    private static final long BLINK_INTERVAL_MS = 500;
    private Dimension width = new Fill();
    private String placeholder = "_";

    public InputComponent() {}

    public InputComponent(Consumer<String> onInput) {
        this.onInput = onInput;
    }

    public InputComponent(Function<String, Boolean> validator) {
        this.validator = validator;
    }

    public InputComponent(String regexAcceptable) {
        this.regex = regexAcceptable;
    }

    public InputComponent setWidth(Dimension width) {
        this.width = width;
        return this;
    }

    public InputComponent setRegex(String regex) {
		this.regex = regex;
        return this;
    }

    public InputComponent setValidator(Function<String, Boolean> validator) {
        this.validator = validator;
        return this;
    }

    public InputComponent setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
        return this;
    }

    @Override
    public Renderable render() {
        String display;

        if (isFocused() && cursorVisible) {
            display = buffer.substring(0, cursor) + "│" + buffer.substring(cursor);
        } else {
            display = buffer.substring(0, cursor) + " " + buffer.substring(cursor);
        }

        if (this.buffer.trim().length() == 0 && !this.isFocused())
            display = this.placeholder;

        return new BoxComponent(new TextComponent(display)).setHeight(new Auto()).setWidth(width);
    }

    @Override
    public final void requestFocus() {
        this.lastBlinkToggle = System.currentTimeMillis() + InputComponent.BLINK_INTERVAL_MS;
        super.requestFocus();
    }

    @Override
    public void onKey(char key) {
        resetBlink();

        if (key == '\b' || key == 127) {
            backspace();
            if (this.onInput != null)
                this.onInput.accept(this.buffer);
        }

        if (Pattern.matches(this.regex, String.valueOf(key))) {
            insertChar(key);

            if (this.onInput != null)
                this.onInput.accept(this.buffer);
        }
    }

    private void resetBlink() {
        cursorVisible = true;
        lastBlinkToggle = System.currentTimeMillis();
    }

    @Override
    public void tick(long now) {
        if (!isFocused())
            return;

        if (now - lastBlinkToggle >= BLINK_INTERVAL_MS) {
            cursorVisible = !cursorVisible;
            lastBlinkToggle = now;
            redraw();
        }
    }

    private void insertChar(char c) {
        var nbuffer = buffer.substring(0, cursor) + c + buffer.substring(cursor);

        if (this.validator != null && !this.validator.apply(nbuffer))
            return;

        this.buffer = nbuffer;

        cursor++;
        redraw();
    }

    private void backspace() {
        if (cursor == 0)
            return;
        buffer = buffer.substring(0, cursor - 1) + buffer.substring(cursor);
        cursor--;
        redraw();
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

        if (r.contains(x, y - 1)) {
            this.requestFocus();
            this.resetBlink();
            redraw();
        } else {
            this.defocus();
            this.resetBlink();
            redraw();
        }
    }
}
