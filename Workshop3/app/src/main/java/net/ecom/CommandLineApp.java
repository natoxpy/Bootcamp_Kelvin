package net.ecom;

import java.io.IOException;
import java.io.Writer;
import net.ecom.views.MainView;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp.Capability;

import org.jline.utils.NonBlockingReader;

public class CommandLineApp {
    private Terminal terminal;
    private Controller controller;

    public CommandLineApp() {
        try {
            this.terminal = TerminalBuilder.builder().system(true).build();
            this.terminal.enterRawMode();
        } catch (IOException e) {
            throw new RuntimeException(
                "I don't know what you could do on a commandline app without a commandline.");
        }

        MainView view = new MainView(this.terminal.getSize());
        Model model = new Model();
        this.controller = new Controller(view, model);


    }

    public void update() {
        this.controller.getView().update(this.controller.getModel());
    }

    public void render(Writer writer) {
        this.controller.getView().render(writer);
    }

    public void run() {
        NonBlockingReader reader = terminal.reader();
        Writer writer = terminal.writer();

        this.update();

        this.terminal.puts(Capability.clear_screen);
        this.terminal.puts(Capability.cursor_invisible);

        this.render(writer);
        this.terminal.flush();

        while (true) {
            int c;
            try {
                c = reader.read();
            } catch (IOException e) {
                throw new RuntimeException("Unable to read input");
            }

            if (c == 'q')
                break;

            this.update();

            this.terminal.puts(Capability.cursor_address, 0, 0);
            this.render(writer);
            this.terminal.puts(Capability.clr_eos);
            this.terminal.flush();
        }
    }

    public void close() {
        this.terminal.puts(Capability.cursor_normal);
        this.terminal.flush();

        try {
            this.terminal.close();
        } catch (IOException e) {
            throw new RuntimeException("Unable to close terminal");
        }
    }
}
