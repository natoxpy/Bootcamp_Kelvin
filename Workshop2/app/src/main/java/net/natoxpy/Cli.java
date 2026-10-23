package net.natoxpy;

import java.util.Scanner;

public class Cli {
  private Book[] books;
  private String command = null;
  private Scanner input;
  private boolean terminate = false;

  public Cli() {
    Book[] books = {
      new Book(1, "9780140328721", "Fantastic Mr. Fox"),
      new Book(2, "9780743273565", "The Great Gatsby", true, "Alice Johnson"),
      new Book(3, "9780061120084", "To Kill a Mockingbird"),
      new Book(4, "9780451524935", "1984", true, "Marcus Lee"),
      new Book(5, "9780547928227", "The Hobbit"),
      new Book(6, "9780316769488", "The Catcher in the Rye"),
      new Book(7, "9780060850524", "Brave New World", true, "Priya Patel"),
      new Book(8, "9780141439518", "Pride and Prejudice"),
      new Book(9, "9780142437179", "Moby-Dick"),
      new Book(10, "9780486280615", "Adventures of Huckleberry Finn", true, "Daniel Kim"),
      new Book(11, "9780553213515", "The Count of Monte Cristo"),
      new Book(12, "9780679783268", "Crime and Punishment"),
      new Book(13, "9780439708180", "Harry Potter and the Sorcerer's Stone", true, "Ethan Brooks"),
      new Book(14, "9780618640157", "Dune"),
      new Book(15, "9780765311788", "Ender's Game"),
      new Book(16, "9780132350884", "Clean Code", true, "Tyrone Williams"),
      new Book(17, "9780134685991", "Effective Java"),
      new Book(18, "9780201633610", "Design Patterns"),
      new Book(19, "9780135957059", "The Pragmatic Programmer", true, "Grace Okafor"),
      new Book(20, "9780201485677", "Refactoring")
    };

    this.books = books;

    this.input = new Scanner(System.in);
  }

  public Book getBookById(int id) {
    Book foundBook = null;

    for (Book book : this.books) {
      if (book.getId() != id) continue;
      foundBook = book;
      break;
    }

    return foundBook;
  }

  public Book[] getBooks() {
    return this.books;
  }

  public Book[] getCheckOutBooks() {
    int checkedOutBooks = 0;
    for (Book book : this.getBooks()) if (book.isCheckedOut()) checkedOutBooks++;

    Book[] checkedOutLibrary = new Book[checkedOutBooks];

    int libraryCounter = 0;
    for (Book book : this.getBooks()) {
      if (!book.isCheckedOut()) continue;
      checkedOutLibrary[libraryCounter++] = book;
    }

    return checkedOutLibrary;
  }

  public Book[] getAvailableBooks() {
    int availableBooks = 0;
    for (Book book : this.getBooks()) if (!book.isCheckedOut()) availableBooks++;

    Book[] availableLibrary = new Book[availableBooks];

    int libraryCounter = 0;
    for (Book book : this.getBooks()) {
      if (book.isCheckedOut()) continue;
      availableLibrary[libraryCounter++] = book;
    }

    return availableLibrary;
  }

  public int getCommandAsInt() {
    try {
      return Integer.parseInt(this.command);
    } catch (NumberFormatException e) {
      return Integer.MAX_VALUE;
    }
  }

  public boolean terminated() {
    return this.terminate;
  }

  public String getCommand() {
    return this.command;
  }

  public void terminate() {
    this.terminate = true;
    this.input.close();
  }

  public String greeting() {
    return "Welcome To BMS (Book Management System)\n";
  }

  public String farewell() {
    return "Thanks for using BMS (Book Management System)\n";
  }

  public String showLibrary(Book[] library) {
    StringBuilder sb = new StringBuilder();

    int maxTitleWidth = 1;
    int maxIsbnWidth = 1;

    for (Book book : library) {
      int bookTitleWidth = book.getTitle().length();
      int bookIsbnMaxWidth = book.getIsbn().length();

      maxTitleWidth = bookTitleWidth > maxTitleWidth ? bookTitleWidth : maxTitleWidth;

      maxIsbnWidth = bookIsbnMaxWidth > maxIsbnWidth ? bookIsbnMaxWidth : maxIsbnWidth;
    }

    maxIsbnWidth += 2;
    maxTitleWidth += 2;

    String rawFormat = "%-4s %-" + maxIsbnWidth + "s %-" + maxTitleWidth + "s %s\n";

    sb.append(String.format(rawFormat, "ID", "ISBN", "Title", "Checked Out To"));

    for (Book book : library) {
      String checkedOutTo = book.getCheckedOutTo().equals("") ? "-" : book.getCheckedOutTo();
      sb.append(
          String.format(rawFormat, book.getId(), book.getIsbn(), book.getTitle(), checkedOutTo));
    }

    sb.append("\n");
    return sb.toString();
  }

  public String fullLibrary() {
    return this.showLibrary(this.getBooks());
  }

  public String checkedOutLibrary() {
    return this.showLibrary(this.getCheckOutBooks());
  }

  public String availableLibrary() {
    return this.showLibrary(this.getAvailableBooks());
  }

  public String availableCommands() {
    StringBuilder sb = new StringBuilder();

    String rawFormat = "   %-23s %s\n";

    sb.append("Available Commands\n");
    sb.append(String.format(rawFormat, "Command", "Description"));
    sb.append(String.format(rawFormat, "h  | help", "This Page"));
    sb.append(String.format(rawFormat, "b  | books", "Show all books"));
    sb.append(String.format(rawFormat, "ba | booksavailable", "Show books available"));
    sb.append(String.format(rawFormat, "bc | bookscheckout", "Show books checkedout"));
    sb.append(String.format(rawFormat, "ci | checkin", "Checkin a book"));
    sb.append(String.format(rawFormat, "co | checkout", "Checkout a book"));
    sb.append(String.format(rawFormat, "q  | quit", "Quit"));

    sb.append("\n");
    return sb.toString();
  }

  public String invalidCommand() {
    return "Invalid command. Use command h or help to see available commands.\n";
  }

  public String commandPrompt(String prompt) {
    if (prompt == null) return "> ";
    return prompt;
  }

  public void requestUserCommand() {
    this.requestUserCommand(null);
  }

  public void requestUserCommand(String prompt) {
    System.out.print(this.commandPrompt(prompt));
    this.command = this.input.nextLine();
  }

  public void handleCheckin() {
    this.handleCheckin(0);
  }

  public void handleCheckin(int recursed_code) {
    final int FIRST_RECURSION = 0, INVALID_ID_FORMAT = 1, ID_NOT_FOUND = 2, BOOK_NOT_CHECKOUT = 3;

    switch (recursed_code) {
      case FIRST_RECURSION:
        System.out.print("Checking In a Book.");
        break;
      case INVALID_ID_FORMAT:
        System.out.print("Error, Id must be a number.");
        break;
      case ID_NOT_FOUND:
        System.out.print("Error, A book with that Id was not found.");
        break;
      case BOOK_NOT_CHECKOUT:
        System.out.print("Error, Book is not checked out.");
        break;
      default:
        System.out.print("Error, Something went wrong, try again. ");
        break;
    }

    System.out.println(" Type x to exit");

    this.requestUserCommand("Book Id: ");

    if (this.command.toLowerCase().equals("x")) return;

    int bookId = this.getCommandAsInt();

    if (bookId == Integer.MAX_VALUE) {
      this.handleCheckin(INVALID_ID_FORMAT);
      return;
    }

    Book checkingIn = this.getBookById(bookId);

    if (checkingIn == null) {
      this.handleCheckin(ID_NOT_FOUND);
      return;
    }

    if (!checkingIn.isCheckedOut()) {
      this.handleCheckin(BOOK_NOT_CHECKOUT);
      return;
    }

    checkingIn.checkIn();

    System.out.printf("Checked In '%s'.\n", checkingIn.getTitle());
  }

  public void handleCheckout() {
    this.handleCheckout(0);
  }

  public void handleCheckout(int recursed_code) {
    final int FIRST_RECURSION = 0, INVALID_ID_FORMAT = 1, ID_NOT_FOUND = 2, BOOK_CHECKOUT = 3;

    switch (recursed_code) {
      case FIRST_RECURSION:
        System.out.print("Checking out a Book.");
        break;
      case INVALID_ID_FORMAT:
        System.out.print("Error, Id must be a number.");
        break;
      case ID_NOT_FOUND:
        System.out.print("Error, A book with that Id was not found.");
        break;
      case BOOK_CHECKOUT:
        System.out.print("Error, Book is already checked out.Checked out by");
        break;
      default:
        System.out.print("Error, Something went wrong, try again.");
        break;
    }

    System.out.println(" Type x to exit");

    this.requestUserCommand("Book Id: ");
    if (this.command.toLowerCase().equals("x")) return;

    int bookId = this.getCommandAsInt();

    if (bookId == Integer.MAX_VALUE) {
      this.handleCheckin(INVALID_ID_FORMAT);
      return;
    }

    Book checkingOut = this.getBookById(bookId);

    if (checkingOut == null) {
      this.handleCheckout(ID_NOT_FOUND);
      return;
    }

    if (checkingOut.isCheckedOut()) {
      this.handleCheckout(BOOK_CHECKOUT);
      return;
    }

    this.requestUserCommand("Borrower: ");
    if (this.command.toLowerCase().equals("x")) return;

    String borrower = this.command;

    checkingOut.checkOut(borrower);

    System.out.printf("Checked out '%s' to '%s'.\n", checkingOut.getTitle(), borrower);
  }

  public void handleCommand() {
    switch (this.command.toLowerCase()) {
      case "h", "help":
        System.out.print(this.availableCommands());
        break;
      case "b", "books":
        System.out.print(this.fullLibrary());
        break;
      case "ba", "booksavailable":
        System.out.print(this.availableLibrary());
        break;
      case "bc", "bookscheckout":
        System.out.print(this.checkedOutLibrary());
        break;
      case "ci", "checkin":
        this.handleCheckin();
        break;
      case "co", "checkout":
        this.handleCheckout();
        break;
      case "q", "quit":
        this.terminate();
        break;
      default:
        System.out.print(this.invalidCommand());
        break;
    }

    this.command = null;
  }

  public void commandEntryPoint() {
    requestUserCommand();
    handleCommand();
  }

  public void run() {
    System.out.print(this.greeting());

    do this.commandEntryPoint();
    while (!this.terminated());

    System.out.print(this.farewell());
  }
}
