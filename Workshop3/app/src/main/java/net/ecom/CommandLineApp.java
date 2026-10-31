package net.ecom;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import net.ecom.data.Product;
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
        Model model = this.readData();
        this.controller = new Controller(view, model);
    }

    public Model readData() {
        CsvMapper mapper = new CsvMapper();
        mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);

        CsvSchema schema = mapper.schemaFor(Product.class).withHeader();
        ArrayList<Product> products = new ArrayList<>();

        try {
            MappingIterator<Product> it =
                mapper.readerFor(Product.class)
                    .with(schema)
                    .readValues(new File("app/src/main/resources/products.csv"));

            products = it.readAll(new ArrayList<>());

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        HashMap<String, HashSet<String>> categories = new HashMap<>();
        HashMap<String, ArrayList<Product>> modelProducts = new HashMap<>();

        Model model = new Model(categories, modelProducts);

        for (Product p : products) {
            String[] category = p.category().split("-");
            HashSet<String> c = categories.get(category[0]);
            if (!category[0].equals("Groceries"))
                model.addCollapse(category[0]);

            if (c == null) {
                c = new HashSet<>();
                categories.put(category[0], c);
            }

            c.add(category[1]);

            ArrayList<Product> b = modelProducts.get(category[1]);

            if (b == null) {
                b = new ArrayList<>();
                modelProducts.put(category[1], b);
            }

            b.add(p);
        }

        return model;
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
            try {
                int r = this.controller.handleInput(reader);
                if (r == -1)
                    break;
            } catch (IOException e) {
                throw new RuntimeException("Unable to read input");
            }

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
