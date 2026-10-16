package org.calc;

import java.util.Scanner;

public class App {
  public static void calc1() {
    Scanner term = new Scanner(System.in);

    System.out.println("Mortgage Calculator");

    System.out.printf("Principal: ");
    double p = (double) term.nextInt();

    System.out.printf("Annual Interest Rate: ");
    double r = term.nextDouble() / 100;

    System.out.printf("Loan Term in Years: ");
    double y = (double) term.nextInt();

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

    System.out.printf("Principal: ");
    double p = (double) term.nextInt();

    System.out.printf("Annual Interest Rate: ");
    double r = (double) term.nextDouble();

    System.out.printf("Number of Years: ");
    double t = (double) term.nextInt();

    double d = 365;

    double n = d * t;

    double fv = p * Math.pow(1 + (r / d), n);

    System.out.printf("Interest earned: %.2f%n", fv - p);

    term.close();
  }

  public static void calc3() {
    Scanner term = new Scanner(System.in);

    System.out.println("Future Value");

    System.out.printf("Montly Payout Amount: ");
    double pmt = term.nextInt();

    System.out.printf("Interest Rate: ");
    double r = term.nextDouble() / 12;

    System.out.printf("Years Payout: ");
    double t = term.nextInt();

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
