package net.renderer.text;

import java.io.Writer;
import java.nio.CharBuffer;

public class Renderer {
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

    private void setBufferAt(char c, int x, int y) {
        int n = x + y * this.canvasWidth;
        if (n >= this.buffer.length)
            throw new IndexOutOfBoundsException("Tried to access buffer beyond it's define size");
        this.buffer[n] = c;
    }

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

    public void drawText(int x, int y, String content) {
        char[] ch = new char[content.length()];
        content.getChars(0, content.length(), ch, 0);

        CharBuffer cb = CharBuffer.wrap(ch);

        for (int i = 0; i < this.buffer.length; i++) {
            int xi = this.indexToX(i);
            int yi = this.indexToY(i);
            int h = 1;
            int w = content.length();

            boolean withinRectXBound = xi >= x && xi < x + w;
            boolean withinRectYBound = yi >= y && yi < y + h;

            if (!withinRectXBound || !withinRectYBound)
                continue;

            this.setBufferAt(cb.get(), xi, yi);
        }
    }

    private String[] getLines() {
        String[] lines = new String[this.canvasHeight];
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < this.buffer.length; i++) {
            int xi = this.indexToX(i);
            int yi = this.indexToY(i);

            sb.append((char) this.buffer[i]);

            if (xi != this.canvasWidth - 1)
                continue;

            lines[yi] = sb.toString();
            sb = new StringBuilder();
        }

        return lines;
    }

    public void write(Writer writer) {
        String[] lines = this.getLines();

        try {
            for (int i = 0; i < lines.length; i++) {
                writer.write(lines[i]);
                if (i < lines.length - 1)
                    writer.write('\n');
            }

            writer.flush();

        } catch (Exception e) {
            throw new RuntimeException("Writer issue or something.");
        }
    }

    public void clear(char c) {
        for (int i = 0; i < buffer.length; i++) {
            this.buffer[i] = c;
        }
    }
}
