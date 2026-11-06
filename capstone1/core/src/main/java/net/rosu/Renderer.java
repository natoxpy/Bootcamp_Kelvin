package net.rosu;

import java.util.List;
import net.gui.commands.Color;
import net.gui.commands.DrawBoxCommand;
import net.gui.commands.DrawCommand;
import net.gui.commands.DrawTextCommand;
import net.renderer.text.ANSI;
import net.renderer.text.OutlineStyle;
import net.renderer.text.RectStyle;
import net.rosu.component.OutlineDesign;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp.Capability;

public class Renderer {
    private net.renderer.text.Renderer engine;
    private Terminal terminal;

    public Renderer(Terminal terminal) {
        this.engine = new net.renderer.text.Renderer(
            terminal.getSize().getColumns(), terminal.getSize().getRows());
        this.terminal = terminal;
    }

    public static ANSI toANSI(Color color) {
        switch (color) {
            case Black:
                return ANSI.BLACK;
            case Red:
                return ANSI.RED;
            case Green:
                return ANSI.GREEN;
            case Blue:
                return ANSI.BLUE;
            case Yellow:
                return ANSI.YELLOW;
            case Purple:
                return ANSI.MAGENTA;
            case Cyan:
                return ANSI.CYAN;
            case White:
                return ANSI.WHITE;
            default:
                throw new IllegalArgumentException("Unmapped color: " + color);
        }
    }

    private void drawBox(DrawTextCommand tcmd) {
        var fgcolor =
            tcmd.style().fgcolor() == null ? ANSI.RESET : Renderer.toANSI(tcmd.style().fgcolor());

        var bgcolor =
            tcmd.style().bgcolor() == null ? ANSI.RESET : Renderer.toANSI(tcmd.style().bgcolor());

        this.engine.drawText(tcmd.x(), tcmd.y(), tcmd.content(), fgcolor, bgcolor);
    }

    private void drawText(DrawBoxCommand bcmd) {
        var outline = bcmd.boxStyle().outlineColor() == null
            ? ANSI.RESET
            : Renderer.toANSI(bcmd.boxStyle().outlineColor());

        var background = bcmd.boxStyle().backgroundColor() == null
            ? ANSI.RESET
            : Renderer.toANSI(bcmd.boxStyle().backgroundColor());

        OutlineDesign od = (OutlineDesign) bcmd.data();

        OutlineStyle outlineStyle = null;

        if (od != null)
            outlineStyle = new OutlineStyle(od.top(), od.bottom(), od.Left(), od.Right(), od.topLeft(),
                od.topRight(), od.bottomLeft(), od.bottomRight());

        var rect = new RectStyle(outlineStyle, background, outline);

        this.engine.drawBox(bcmd.x(), bcmd.y(), bcmd.width(), bcmd.height(), rect);
    }

    public void render(List<DrawCommand> cmds) {
        this.engine.clear(' ');

        for (var cmd : cmds) {
            if (cmd instanceof DrawTextCommand tcmd) {
                this.drawBox(tcmd);
            } else if (cmd instanceof DrawBoxCommand bcmd) {
                this.drawText(bcmd);
            }
        }

        // this.renderer.write(new OutputStreamWriter(System.out));

        this.terminal.puts(Capability.cursor_address, 0, 0);
        this.engine.write(this.terminal.writer());
        this.terminal.puts(Capability.clr_eos);
        this.terminal.flush();
    }

    public void clear() {
        this.engine.clear(' ');
    }
}
