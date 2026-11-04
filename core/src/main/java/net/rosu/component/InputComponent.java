package net.rosu.component;

import java.util.regex.Pattern;
import net.rosu.Tickable;

public class InputComponent extends Component implements Focusable, Tickable {
    private String buffer = "";
    private int cursor = 0;
    private String regex = "[a-zA-Z0-9\\s]";

    public InputComponent() {}

    public InputComponent(String regexAcceptable) {
        this.regex = regexAcceptable;
    }

    private boolean cursorVisible = true;
    private long lastBlinkToggle = System.currentTimeMillis();
    private static final long BLINK_INTERVAL_MS = 500;

    @Override
    public Renderable render() {
        String display;
        if (isFocused() && cursorVisible) {
            display = buffer.substring(0, cursor) + "│" + buffer.substring(cursor);
        } else {
            display = buffer;
        }
        return new TextComponent(display);
    }

    @Override
    public void onKey(char key) {
        if (key == '\b' || key == 127) {
            backspace();
        }

        if (Pattern.matches(this.regex, String.valueOf(key))) {
            insertChar(key);
        }

        resetBlink();
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
        buffer = buffer.substring(0, cursor) + c + buffer.substring(cursor);
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
}
