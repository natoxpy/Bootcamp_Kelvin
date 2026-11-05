package net.example.demo;

import net.rosu.Rosu;
import net.rosu.component.Component;

public class Demo {
    public static void main(String[] args) {
        Rosu app = new Rosu();

        Component window = new MainWindow();

        app.setRoot(window);

        try {
            app.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        app.close();
    }
}
