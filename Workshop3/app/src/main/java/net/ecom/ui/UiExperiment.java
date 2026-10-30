package net.ecom.ui;

import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp.Capability;
import org.jline.utils.NonBlockingReader;

/// Temporary Name UIX Suggested name Buffer-Mutation-Layer (BufferMutationLayer)
class Bml {
  private int canvasWidth;
  private int canvasHeight;

  public int[] buffer;

  public Bml(int canvasWidth, int canvasHeight) {
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
  public void drawRect(int width, int height, int x, int y, char outline, char fill) {
    if (width <= 0 || height <= 0) throw new RuntimeException("Width or Height Less than Zero.");

    for (int i = 0; i < this.buffer.length; i++) {
      int xi = this.indexToX(i);
      int yi = this.indexToY(i);

      boolean withinRectXBound = xi >= x && xi < x + width;
      boolean withinRectYBound = yi >= y && yi < y + height;

      if (!withinRectXBound || !withinRectYBound) continue;

      boolean xOutlineCheck = xi == x || xi == x + (width - 1);
      boolean yOutlineCheck = yi == y || yi == y + (height - 1);

      if (xOutlineCheck || yOutlineCheck) {
        this.setBufferAt(outline, xi, yi);
        continue;
      }

      this.setBufferAt(fill, xi, yi);
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

      if (!withinRectXBound || !withinRectYBound) continue;

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

      if (xi != this.canvasWidth - 1) continue;

      lines[yi] = sb.toString();
      sb = new StringBuilder();
    }

    return lines;
  }
}

/// Coordinate-Translation Layer (CTL)
class Ctl {
  private BufferMutationLayer context = null;
  private Ctl parent = null;
  private int xOffset = 0, yOffset = 0;

  public Ctl(Size size) {
    BufferMutationLayer ctx = new BufferMutationLayer(size.getColumns(), size.getRows());
    this.context = ctx;
  }

  public Ctl(Size size, Ctl parent, int xOffset, int yOffset) {
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

  public void drawRect(int width, int height, int x, int y, char outline, char fill) {
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

  public Ctl window(int w, int h, int x, int y, char edges) {
    if (w < 2 || h < 2)
      throw new RuntimeException("Cannot create window smaller than 2 width or 2 height.");

    this.drawRect(w, h, x, y, edges, ' ');

    return new Ctl(Size.of(w - 2, h - 2), this, x + 1, y + 1);
  }

  public Ctl windowBorderless(int w, int h, int x, int y) {
    this.drawRect(w, h, x, y, ' ', ' ');

    return new Ctl(Size.of(w, h), this, x, y);
  }

  public BufferMutationLayer getContext() {
    if (this.context == null) return this.parent.getContext();
    return this.context;
  }
}

public class UiExperiment {
  public static void main(String[] args) throws IOException {
    Terminal terminal = TerminalBuilder.builder().system(true).build();
    terminal.enterRawMode();

    terminal.puts(Capability.clear_screen);
    terminal.flush();

    Writer writer = terminal.writer();
    NonBlockingReader reader = terminal.reader();
    Size size = terminal.getSize();

    // Ui

    int uiWidth = size.getColumns();
    int uiHeight = size.getRows();
    Ctl ui = new Ctl(size);

    int bodyW = 100;
    int bodyH = uiHeight / 2;
    int bodyX = (uiWidth / 2) - (bodyW / 2);
    int bodyY = (uiHeight / 2) - (bodyH / 2);

    char border = '#';

    Ctl body = ui.window(bodyW, bodyH, bodyX, bodyY, border);

    Ctl sidebar = body.window(21, bodyH, -1, -1, border);

    sidebar.drawText(" Store Application", 0, 0);

    Ctl categories = sidebar.window(21, 15, -1, 1, border);

    Ctl filter = sidebar.window(21, 10, -1, 15, border);

    Ctl footer = ui.window(bodyW, 3, bodyX, bodyH + bodyY - 1, border);

    // UiS window = ui.window(30, 35, 20, 8, '*');
    // UiS window2 = window.windowBorderless(20, 25, 1, 2);
    // UiS window3 = window2.window(18, 22, 0, 1, '@');

    // window2.drawRect(5, 6, 2, 24, '&', ' ');

    // window3.drawText("Hello", (18 / 2) - 3, 0);

    for (String line : ui.getContext().getLines()) {
      writer.write(line);
      writer.write('\n');
    }

    /// UiX uix = new UiX(size.getColumns(), size.getRows());
    /// String[] lines = uix.getLines();

    // Ui

    terminal.flush();

    // while (true) {
    //   int c = reader.read(3000);
    //   if (c == 'q') break;
    // }

    terminal.close();
  }

  public static void example002() throws IOException {
    Terminal terminal = TerminalBuilder.builder().system(true).build();
    terminal.enterRawMode();

    Writer writer = terminal.writer();

    writer.write("Hello, World");

    // NonBlockingReader reader = terminal.reader();

    // int c = reader.read(1000);

    // System.out.println("c: " + (char) c);

    // terminal.writer().println("\nTime's up!");

    terminal.close();
  }

  public static void example001() {
    // Terminal terminal = TerminalBuilder.builder().system(true).build();
    // terminal.enterRawMode();

    // NonBlockingReader reader = terminal.reader();

    // int c = reader.read(1000);

    // System.out.println("c: " + (char) c);

    // terminal.writer().println("\nTime's up!");

    // terminal.close();
  }
}
