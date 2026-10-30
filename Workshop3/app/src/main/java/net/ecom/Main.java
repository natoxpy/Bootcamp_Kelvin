package net.ecom;

public class Main {
    public static void main(String[] args) {
        CommandLineApp app = new CommandLineApp();
        try {
            app.run();
        } finally {
            app.close();
        }
    }
}
