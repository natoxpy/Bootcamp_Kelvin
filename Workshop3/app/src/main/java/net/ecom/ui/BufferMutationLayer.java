package net.ecom.ui;

import java.nio.CharBuffer;

public class BufferMutationLayer {
    private int canvasWidth;
    private int canvasHeight;

    public int[] buffer;

    public BufferMutationLayer(int canvasWidth, int canvasHeight) {
        this.buffer = new int[canvasWidth * canvasHeight];

        for (int i = 0; i < buffer.length; i++) {
            this.buffer[i] = ' ';
        }

        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
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

    // PERF: Optimized Render To not use entire buffer
    public void drawRect(int width, int height, int x, int y, char outline, int fill) {
        if (width <= 0 || height <= 0)
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

    // PERF: Optimized Render To not use entire buffer
    public void drawText(String txt, int x, int y) {
        char[] ch = new char[txt.length()];
        txt.getChars(0, txt.length(), ch, 0);

        CharBuffer cb = CharBuffer.wrap(ch);

        for (int i = 0; i < this.buffer.length; i++) {
            int xi = this.indexToX(i);
            int yi = this.indexToY(i);
            int h = 1;
            int w = txt.length();

            boolean withinRectXBound = xi >= x && xi < x + w;
            boolean withinRectYBound = yi >= y && yi < y + h;

            if (!withinRectXBound || !withinRectYBound)
                continue;

            this.setBufferAt(cb.get(), xi, yi);
        }
    }

    // PERF: Optimize for memory utilization
    public String[] getLines() {
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

    public void clearContext() {
        for (int i = 0; i < buffer.length; i++) {
            this.buffer[i] = ' ';
        }
    }

    // TODO: Resize Context
    public void resizeContext() {}
}
