package net.rosu;

import java.io.IOException;
// import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import net.gui.Context;
import net.gui.commands.DrawBoxCommand;
import net.gui.commands.DrawCommand;
import net.gui.commands.DrawTextCommand;
import net.gui.commands.Size;
import net.gui.primitives.Box;
import net.gui.primitives.LayoutConstraint;
import net.gui.primitives.Node;
import net.gui.primitives.Overflow;
import net.gui.primitives.Rect;
import net.gui.primitives.dimension.Fixed;
import net.renderer.text.Renderer;
import net.rosu.component.Component;
import net.rosu.component.FocusManager;
import net.rosu.component.Focusable;
import net.rosu.component.Renderable;
import net.rosu.component.SystemComponent;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp.Capability;
import org.jline.utils.NonBlockingReader;

public class Rosu {
    private Renderable root;
    private Context context = new Context();
    private Size size;
    private Renderer renderer;
    private Terminal terminal;
    private FocusManager focusManager = new FocusManager();

    public Rosu() {
        try {
            this.terminal = TerminalBuilder.builder().system(true).build();
            this.terminal.enterRawMode();
        } catch (IOException e) {
            throw new RuntimeException(
                "I don't know what you could do on a commandline app without a commandline.");
        }

        this.size =
            new Size(this.terminal.getSize().getColumns(), this.terminal.getSize().getRows());
        this.renderer = new Renderer(size.width(), size.height());
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

    public void start() {
        int rootChildId = this.reconcile(this.context, null, this.root);

        Box root = new Box(rootChildId, Overflow.Hidden,
            new LayoutConstraint(new Fixed(this.size.width()), new Fixed(this.size.height())));

        int rootId = this.context.insert(root);

        Node rootNode = this.context.get(rootId);

        List<DrawCommand> cmds = new ArrayList<>();

        this.terminal.puts(Capability.clear_screen);
        this.terminal.puts(Capability.cursor_invisible);

        for (;;) {
            this.reconcile(this.context, rootChildId, this.root);

            rootNode.draw(this.context, rootId, 0, 0, this.size.width(), this.size.height(),
                new Rect(0, 0, this.size), cmds);

            this.render(cmds);
            this.focusManager.focusNext();

            this.tickFocused();

            int r = this.input();
            if (r == 1)
                break;

            cmds.clear();
        }
    }

    private int input() {
        NonBlockingReader reader = this.terminal.reader();

        try {
            int c = reader.read(10);
            if (c < 0)
                return 0;
            if (c == 'q')
                return 1;

            if (c == 0x1B) { // ESC — likely the start of an arrow key / escape sequence
                int next1 = reader.read(50);
                if (next1 == '[') {
                    int next2 = reader.read(50);
                    switch (next2) {
                        case 'A': /* Up arrow */
                            break;
                        case 'B': /* Down arrow */
                            break;
                        case 'C': /* Right Arrow */
                            break;
                        case 'D': /* Right Arrow */
                            break;
                    }
                }
                // else: lone ESC key press, or unrecognized sequence — swallow it either way
                return 0;
            }

            if (c == '\t')
                this.focusManager.focusNext();

            Component f = this.getFocus();
            if (f != null)
                f.onKey((char) c);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    private void tickFocused() {
        Renderable f = this.focusManager.getFocus();
        if (f instanceof Tickable ic) {
            ic.tick(System.currentTimeMillis());
        }
    }

    private void render(List<DrawCommand> cmds) {
        this.renderer.clear(' ');

        for (var cmd : cmds) {
            if (cmd instanceof DrawTextCommand tcmd) {
                this.renderer.drawText(tcmd.x(), tcmd.y(), tcmd.content());
            } else if (cmd instanceof DrawBoxCommand bcmd) {
                this.renderer.drawBox(bcmd.x(), bcmd.y(), bcmd.width(), bcmd.height(), '#', ' ');
            }
        }

        // this.renderer.write(new OutputStreamWriter(System.out));

        this.terminal.puts(Capability.cursor_address, 0, 0);
        this.renderer.write(this.terminal.writer());
        this.terminal.puts(Capability.clr_eos);
        this.terminal.flush();
    }

    public Integer reconcile(Context ctx, Integer existingId, Renderable r) {
        if (r == null)
            return null;

        if (r instanceof Component c) {
            if (c instanceof Focusable) {
                this.focusManager.register(c);
            }
            c.attachFocusManager(this.focusManager);

            return this.reconcile(ctx, existingId, c.getRenderable());
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
            this.terminal.flush();
            this.terminal.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
