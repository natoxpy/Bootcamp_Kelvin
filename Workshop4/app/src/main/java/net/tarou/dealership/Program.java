package net.tarou.dealership;

/** Entry point. {@link UserInterface} owns the dealership; {@code Program} does not touch one. */
public class Program {
    public static void main(String[] args) {
        UserInterface userInterface = new UserInterface();
        userInterface.display();
    }
}
