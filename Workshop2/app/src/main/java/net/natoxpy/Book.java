package net.natoxpy;

public class Book {
  private int id;
  private String isbn;
  private String title;
  private boolean isCheckedOut;
  // Exposed data assuming application will be used as an internal library tool and not available to
  // the public
  private String checkedOutTo;

  public Book(int id, String isbn, String title) {
    this.id = id;
    this.isbn = isbn;
    this.title = title;
    this.isCheckedOut = false;
    this.checkedOutTo = "";
  }

  public Book(int id, String isbn, String title, boolean isCheckedOut, String checkedOutTo) {
    this.id = id;
    this.isbn = isbn;
    this.title = title;
    this.isCheckedOut = isCheckedOut;
    this.checkedOutTo = checkedOutTo;
  }

  public int getId() {
    return this.id;
  }

  public String getIsbn() {
    return this.isbn;
  }

  public String getTitle() {
    return this.title;
  }

  public boolean isCheckedOut() {
    return this.isCheckedOut;
  }

  public String getCheckedOutTo() {
    return this.checkedOutTo;
  }

  public void checkOut(String name) {
    this.isCheckedOut = true;
    this.checkedOutTo = name;
  }

  public void checkIn() {
    this.isCheckedOut = false;
    this.checkedOutTo = "";
  }

  @Override
  public String toString() {
    return "Book [getId()="
        + getId()
        + ", getIsbn()="
        + getIsbn()
        + ", getTitle()="
        + getTitle()
        + ", isCheckedOut()="
        + isCheckedOut()
        + ", getCheckedOutTo()="
        + getCheckedOutTo()
        + "]";
  }
}
