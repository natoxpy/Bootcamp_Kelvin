package net.natoxpy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BookTest {
  @Test
  public void standardBookDisplay() {
    Book book = new Book(1, "abc", "123");

    String expected =
        "Book [getId()=1, getIsbn()=abc, getTitle()=123, isCheckedOut()=false,"
            + " getCheckedOutTo()=]";

    assertEquals(expected, book.toString());
  }

  @Test
  public void bookCheckout() {
    Book book = new Book(1, "abc", "123");

    book.checkOut("Lamy");

    assertTrue(book.isCheckedOut());
    assertEquals(book.getCheckedOutTo(), "Lamy");
  }

  @Test
  public void bookCheckin() {
    Book book = new Book(1, "abc", "123", true, "lamy");
    book.checkIn();

    assertFalse(book.isCheckedOut());
    assertEquals(book.getCheckedOutTo(), "");
  }
}
