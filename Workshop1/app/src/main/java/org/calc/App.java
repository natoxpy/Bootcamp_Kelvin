package org.calc;

import java.util.Scanner;

public class App {
  public static double inputDouble(Scanner s, String msg) {
    try {
      return s.nextDouble();
    } catch (Exception _e) {
      System.out.println(msg);
      s.nextLine();
      return -1;
    }
  }

  public static double persistantInputDouble(Scanner s, String msg, String errmsg) {
    double ret = -1;
    System.out.print(msg);
    while (0 > ret) {
      ret = inputDouble(s, errmsg);
    }
    return ret;
  }

  public static void calc1() {
    Scanner term = new Scanner(System.in);

    System.out.println("Mortgage Calculator");

    double p = persistantInputDouble(term, "Principal: ", "Invalid Principal [non-zero number]");
    double r =
        persistantInputDouble(term, "Annual Interest Rate: ", "Invalid Interest Rate [0%-100%]") / 100;
    double y =
        (int)
            persistantInputDouble(term, "Loan Term in Years: ", "Invalid Years [non-zero number]");

    double n = y * 12.0;
    double i = r / 12.0;

    double m = p * (i * Math.pow(1 + i, n)) / (Math.pow(1 + i, n) - 1);
    double t = (m * n) - p;

    System.out.printf("Monthly Payment: $%.2f%n", m);
    System.out.printf("Total Interest: $%.2f%n", t);

    term.close();
  }

  public static void calc2() {
    Scanner term = new Scanner(System.in);

    System.out.println("Future Value");

    double p = persistantInputDouble(term, "Principal: ", "Invalid Principal [non-zero number]");
    double r =
        persistantInputDouble(term, "Annual Interest Rate: ", "Invalid Interest Rate [0%-100%]") / 100;
    double t =
        (int) persistantInputDouble(term, "Number of Years: ", "Invalid Years [non-zero number]");

    double d = 365;

    double n = d * t;

    double fv = p * Math.pow(1 + (r / d), n);

    System.out.printf("Interest earned: %.2f%n", fv - p);

    term.close();
  }

  public static void calc3() {
    Scanner term = new Scanner(System.in);

    System.out.println("Future Value");

    double pmt =
        persistantInputDouble(term, "Montly Payout: ", "Invalid Montly Payout [non-zero number]");
    double r =
        persistantInputDouble(term, "Annual Interest Rate: ", "Invalid Interest Rate [0%-100%]") / 100;
    double t =
        (int) persistantInputDouble(term, "Number of Years: ", "Invalid Years [non-zero number]");

    double n = t * 12;

    double pv = pmt * ((1 - Math.pow(1 + r, -n)) / r);

    System.out.printf("Annuity: %.2f%n", pv);

    term.close();
  }

  public static void main(String[] args) throws Exception {
    Scanner term = new Scanner(System.in);

    System.out.println("Chose a calculator");
    System.out.println("  [m] Mortgage calculator");
    System.out.println("  [f] Future Value");
    System.out.println("  [p] Present Value");

    char option = term.next().charAt(0);

    switch (option) {
      case 'M':
      case 'm':
        App.calc1();
        break;
      case 'F':
      case 'f':
        App.calc2();
        break;
      case 'P':
      case 'p':
        App.calc3();
        break;
      default:
        System.out.printf("%s not a valid option!", option);
        break;
    }

    term.close();
  }
}
