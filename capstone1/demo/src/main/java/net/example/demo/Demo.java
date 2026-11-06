package net.example.demo;

import net.example.demo.data.Database;
import net.rosu.Rosu;
import net.rosu.component.Component;

public class Demo {
    public static void main(String[] args) {
        Rosu app = new Rosu();
        Database db = new Database();

        Component window = new MainWindow(db);

        app.setRoot(window);

        try {
            app.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        app.close();
    }
}
