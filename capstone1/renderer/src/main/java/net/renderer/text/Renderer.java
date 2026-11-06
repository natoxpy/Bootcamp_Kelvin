package net.renderer.text;

import java.io.Writer;

public class Renderer {
    private static final int CHAR_MASK = 0xFFFF;
    private static final int FG_SHIFT = 16;
    private static final int BG_SHIFT = 24;
    private static final int COLOR_MASK = 0xFF;
    private static final int NO_COLOR = 0;

    private int canvasWidth;
    private int canvasHeight;
    public int[] buffer;

    public Renderer(int canvasWidth, int canvasHeight) {
        this.buffer = new int[canvasWidth * canvasHeight];

        for (int i = 0; i < buffer.length; i++) {
            this.buffer[i] = ' ';
        }

        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
    }

    public int width() {
        return this.canvasWidth;
    }

    public int height() {
        return this.canvasHeight;
    }

    private int indexToX(int index) {
        return index % this.canvasWidth;
    }

    private int indexToY(int index) {
        return index / this.canvasWidth;
    }

    private int indexOf(int x, int y) {
        if (x < 0 || x >= canvasWidth || y < 0 || y >= canvasHeight)
            return -1;

        return x + y * canvasWidth;
    }

    private static int pack(char c, int fg, int bg) {
        return (c & CHAR_MASK) | ((fg & COLOR_MASK) << FG_SHIFT) | ((bg & COLOR_MASK) << BG_SHIFT);
    }

    private static char unpackChar(int cell) {
        return (char) (cell & CHAR_MASK);
    }

    private static int unpackFg(int cell) {
        return (cell >>> FG_SHIFT) & COLOR_MASK;
    }

    private static int unpackBg(int cell) {
        return (cell >>> BG_SHIFT) & COLOR_MASK;
    }

    private void setCell(int x, int y, Character c, ANSI fg, ANSI bg) {
        int n = indexOf(x, y);
        if (n == -1)
            return;

        int old = this.buffer[n];
        char ch = (c != null) ? c : unpackChar(old);
        int f = (fg != null) ? fg.fg : unpackFg(old);
        int b = (bg != null) ? bg.bg : unpackBg(old);
        this.buffer[n] = pack(ch, f, b);
    }

    private void setBufferAt(char c, int x, int y) {
        if (c == ' ')
            return;
        setCell(x, y, c, null, null);
    }

    /** @deprecated use {@link #drawBox(int, int, int, int, RectStyle)} */
    @Deprecated
    public void drawBox(int x, int y, int width, int height, char outline, char fill) {
        if (width < 0 || height < 0)
            throw new RuntimeException("Width or Height Less than Zero.");

        for (int i = 0; i < this.buffer.length; i++) {
            int xi = this.indexToX(i);
            int yi = this.indexToY(i);

            boolean withinRectXBound = xi >= x && xi < x + width;
            boolean withinRectYBound = yi >= y && yi < y + height;

            if (!withinRectXBound || !withinRectYBound)
                continue;

            boolean xOutlineCheck = xi == x || xi == x + (width - 1);
            boolean yOutlineCheck = yi == y || yi == y + (height - 1);

            if (xOutlineCheck || yOutlineCheck) {
                this.setBufferAt(outline, xi, yi);
                continue;
            }

            if (fill != 0)
                this.setBufferAt((char) fill, xi, yi);
        }
    }

    public void drawBox(int x, int y, int width, int height, RectStyle style) {
        if (width < 0 || height < 0)
            throw new RuntimeException("Width or Height Less than Zero.");

        OutlineStyle o = style.outline();
        ANSI outlineColor = style.outlineColor();
        ANSI bgColor = style.backgroundColor();

        int left = x - 1;
        int top = y - 1;
        int right = x + width;
        int bottom = y + height;

        // corners

        if (o != null) {
            setCell(left, top, o.topLeft(), outlineColor, bgColor);
            setCell(right, top, o.topRight(), outlineColor, bgColor);
            setCell(left, bottom, o.bottomLeft(), outlineColor, bgColor);
            setCell(right, bottom, o.bottomRight(), outlineColor, bgColor);

            // top/bottom edges
            for (int xi = x; xi < x + width; xi++) {
                setCell(xi, top, o.top(), outlineColor, bgColor);
                setCell(xi, bottom, o.bottom(), outlineColor, bgColor);
            }

            // left/right edges
            for (int yi = y; yi < y + height; yi++) {
                setCell(left, yi, o.Left(), outlineColor, bgColor);
                setCell(right, yi, o.Right(), outlineColor, bgColor);
            }
        }

        // interior: paint background only, leave whatever char is already there
        for (int yi = y; yi < y + height; yi++) {
            for (int xi = x; xi < x + width; xi++) {
                setCell(xi, yi, null, null, bgColor);
            }
        }
    }

    public void drawText(int x, int y, String content) {
        for (int i = 0; i < content.length(); i++) {
            setBufferAt(content.charAt(i), x + i, y);
        }
    }

    public void drawText(int x, int y, String content, ANSI fg, ANSI bg) {
        for (int i = 0; i < content.length(); i++) {
            setCell(x + i, y, content.charAt(i), fg, bg);
        }
    }

    public void write(Writer writer) {
        try {
            int prevFg = -1, prevBg = -1;

            for (int y = 0; y < canvasHeight; y++) {
                for (int x = 0; x < canvasWidth; x++) {
                    int cell = this.buffer[x + y * canvasWidth];
                    char ch = unpackChar(cell);
                    int fg = unpackFg(cell);
                    int bg = unpackBg(cell);

                    if (fg != prevFg || bg != prevBg) {
                        writer.write(ANSI.RESET_SEQ);
                        if (fg != NO_COLOR)
                            writer.write("\033[" + fg + "m");
                        if (bg != NO_COLOR)
                            writer.write("\033[" + bg + "m");
                        prevFg = fg;
                        prevBg = bg;
                    }
                    writer.write(ch);
                }
                if (y < canvasHeight - 1)
                    writer.write('\n');
            }
            writer.write("\u001B[0m");
        } catch (Exception e) {
            throw new RuntimeException("Writer issue or something.");
        }
    }

    public void clear(char c) {
        int packed = pack(c, 0, 0);
        for (int i = 0; i < buffer.length; i++) {
            this.buffer[i] = packed;
        }
    }
}
