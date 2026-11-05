package net.rosu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.gui.Context;
import net.gui.commands.BoxStyle;
import net.gui.commands.DrawCommand;
import net.gui.commands.Size;
import net.gui.primitives.Box;
import net.gui.primitives.LayoutConstraint;
import net.gui.primitives.Node;
import net.gui.primitives.Overflow;
import net.gui.primitives.Rect;
import net.gui.primitives.dimension.Fixed;
import net.rosu.component.BoxComponent;
import net.rosu.component.Clickable;
import net.rosu.component.Component;
import net.rosu.component.FlexComponent;
import net.rosu.component.FocusManager;
import net.rosu.component.Focusable;
import net.rosu.component.Renderable;
import net.rosu.component.SystemComponent;
import org.jline.terminal.Terminal;
import org.jline.terminal.Terminal.Signal;
import org.jline.terminal.Terminal.SignalHandler;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp.Capability;
import org.jline.utils.NonBlockingReader;

public class Rosu {
    private Renderable root;
    private Context context = new Context();
    private Size size;
    private Terminal terminal;
    private FocusManager focusManager = new FocusManager();
    private Renderer renderer;

    public Rosu() {
        try {
            this.terminal = TerminalBuilder.builder().system(true).build();
            this.terminal.handle(Signal.INT, SignalHandler.SIG_IGN);
            this.terminal.enterRawMode();
            this.terminal.trackMouse(Terminal.MouseTracking.Normal);
            this.terminal.flush();
        } catch (IOException e) {
            throw new RuntimeException(
                "I don't know what you could do on a commandline app without a commandline.");
        }

        this.size =
            new Size(this.terminal.getSize().getColumns(), this.terminal.getSize().getRows());
        this.renderer = new Renderer(this.terminal);
    }

    public void setRoot(Renderable root) {
        this.root = root;
    }

    public Component getFocus() {
        return focusManager.getFocus();
    }

    public void setFocus(Component focus) {
        focusManager.setFocus(focus);
    }

    public void start() throws IOException {
        int rootChildId = this.reconcile(this.context, null, this.root);

        Box root = new Box(rootChildId, Overflow.Hidden,
            new LayoutConstraint(new Fixed(this.size.width()), new Fixed(this.size.height())),
            new BoxStyle(null, null), null);

        int rootId = this.context.insert(root);

        Node rootNode = this.context.get(rootId);

        List<DrawCommand> cmds = new ArrayList<>();

        this.terminal.puts(Capability.clear_screen);
        this.terminal.puts(Capability.cursor_invisible);

        for (;;) {
            this.reconcile(this.context, rootChildId, this.root);

            rootNode.draw(this.context, rootId, 0, 0, this.size.width(), this.size.height(),
                new Rect(0, 0, this.size), cmds);

            this.renderer.render(cmds);

            this.tickFocused();

            int r = this.input();
            if (r == 1)
                break;

            cmds.clear();
        }
    }

    private int input() throws IOException {
        NonBlockingReader reader = this.terminal.reader();

        char bu[] = new char[255];
        int l;

        l = reader.readBuffered(bu, 10);
        if (l == 0)
            return 0;

        // Full/partial mouse sequence already assembled
        if (l > 2 && bu[0] == '\033' && bu[1] == '[' && bu[2] == '<') {
            return handleMouseSequence(bu, l);
        }

        if (bu[0] == 27) {
            if (l == 1) {
                int extra = reader.readBuffered(bu, l, bu.length - l, 15);
                if (extra > 0) {
                    l += extra;
                    if (l > 2 && bu[0] == '\033' && bu[1] == '[' && bu[2] == '<') {
                        return handleMouseSequence(bu, l);
                    }
                    // not a mouse sequence either, fall through and ignore,
                    // or handle other escape-prefixed keys here if you support them
                    return 0;
                }
                // Nothing followed within the grace window — genuinely a real ESC key
                if (this.focusManager.getFocus() == null)
                    return 1;
                else
                    this.focusManager.setFocus(null);
                return 0;
            } else {
                // l > 1 but didn't match "[<", some other escape sequence you're
                // not handling yet; ignore for now
                return 0;
            }
        }

        if ((char) bu[0] == '\t') {
            this.focusManager.focusNext();
            return 0;
        }

        Component f = this.getFocus();
        if (f != null && l >= 1) {
            f.onKey((char) bu[0]);
        }

        return 0;
    }

    private int handleMouseSequence(char[] bu, int l) {
        String s = new String(bu, 0, l);

        boolean release =
            s.indexOf('m') != -1 && (s.indexOf('M') == -1 || s.indexOf('m') < s.indexOf('M'));
        int end = release ? s.indexOf('m') : s.indexOf('M');

        if (end != -1) {
            try {
                String[] parts = s.substring(3, end).split(";");
                if (parts.length == 3) {
                    int b = Integer.parseInt(parts[0]);
                    int x = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[2]);

                    boolean wheel = (b & 64) != 0;
                    boolean press = !release && !wheel;

                    if (press) {
                        clickElements(this.root, x, y);
                    }
                }
            } catch (NumberFormatException e) {
            }
        }
        return 0;
    }

    private void tickFocused() {
        Renderable f = this.focusManager.getFocus();
        if (f instanceof Tickable ic) {
            ic.tick(System.currentTimeMillis());
        }
    }

    private void clickElements(Renderable r, int x, int y) {
        if (r instanceof Component c) {
            this.clickComponent(c, x, y);
        } else if (r instanceof SystemComponent cs) {
            if (cs instanceof BoxComponent csbc) {
                this.clickElements(csbc.getChild(), x, y);
            } else if (cs instanceof FlexComponent csfc) {
                csfc.getChildren().forEach((fc) -> this.clickElements(fc.child(), x, y));
            }
        }
    }

    private void clickComponent(Component c, int x, int y) {
        if (c instanceof Clickable cb)
            cb.click(x, y);

        if (c.getRenderable() != null)
            this.clickElements(c.getRenderable(), x, y);
    }

    public Integer reconcile(Context ctx, Integer existingId, Renderable r) {
        if (r == null)
            return null;

        if (r instanceof Component c) {
            if (!this.focusManager.has(c) && c instanceof Focusable) {
                c.attachFocusManager(this.focusManager);
                this.focusManager.register(c);
            }

            Integer ownId = this.reconcile(ctx, existingId, c.getRenderable());

            if (ownId != null && c instanceof IdAware ca) {
                ca.onIdAssigned(ctx, ownId);
            }

			if (c instanceof Tickable tc) tc.tick(System.currentTimeMillis());

            return ownId;

        } else if (r instanceof SystemComponent sc) {
            return sc.reconcileSelf(ctx, this, existingId);
        }

        throw new IllegalStateException("unreachable");
    }

    public Terminal getRawTerminal() {
        return this.terminal;
    }

    public void close() {
        try {
            this.terminal.puts(Capability.cursor_normal);
            this.terminal.trackMouse(Terminal.MouseTracking.Off);
            this.terminal.flush();
            this.terminal.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
