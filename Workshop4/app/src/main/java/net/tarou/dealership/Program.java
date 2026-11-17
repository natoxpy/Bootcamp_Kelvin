package net.tarou.dealership;

/**
 * Entry point: build the user interface and start it.
 *
 * {@code main()} stays this thin on purpose. The workshop says {@code Program} must not create a
 * {@link Dealership} itself, because a later version of the app serves several dealerships and
 * {@link UserInterface} is the class that owns them.
 */
public class Program {
    public static void main(String[] args) {
        UserInterface userInterface = new UserInterface();
        userInterface.display();
    }
}
