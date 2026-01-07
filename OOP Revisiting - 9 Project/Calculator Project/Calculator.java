import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/* =============================
   Calculator Logic (Encapsulation)
   ============================= */
class CalculatorEngine {

    private final List<String> history = new ArrayList<>();

    public double add(double a, double b) {
        double r = a + b;
        history.add(a + " + " + b + " = " + r);
        return r;
    }

    public double subtract(double a, double b) {
        double r = a - b;
        history.add(a + " - " + b + " = " + r);
        return r;
    }

    public double multiply(double a, double b) {
        double r = a * b;
        history.add(a + " * " + b + " = " + r);
        return r;
    }

    public double divide(double a, double b) {
        if (b == 0) {
            throw new ArithmeticException("Division by zero not allowed");
        }
        double r = a / b;
        history.add(a + " / " + b + " = " + r);
        return r;
    }

    public double power(double base, double exp) {
        double r = Math.pow(base, exp);
        history.add(base + " ^ " + exp + " = " + r);
        return r;
    }

    public double sqrt(double num) {
        if (num < 0) {
            throw new ArithmeticException("Square root of negative number");
        }
        double r = Math.sqrt(num);
        history.add("sqrt(" + num + ") = " + r);
        return r;
    }

    public void showHistory() {
        if (history.isEmpty()) {
            System.out.println("No calculations yet.");
        } else {
            System.out.println("\n--- Calculation History ---");
            for (String h : history) {
                System.out.println(h);
            }
        }
    }
}

/* =============================
   User Interface (Main Class)
   ============================= */
public class Calculator {

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        CalculatorEngine engine = new CalculatorEngine();
        boolean running = true;

        System.out.println("===== ADVANCED CALCULATOR =====");

        while (running) {
            showMenu();
            int choice = readInt();

            try {
                switch (choice) {
                    case 1 -> {
                        double[] n = readTwoNumbers();
                        System.out.println("Result: " + engine.add(n[0], n[1]));
                    }
                    case 2 -> {
                        double[] n = readTwoNumbers();
                        System.out.println("Result: " + engine.subtract(n[0], n[1]));
                    }
                    case 3 -> {
                        double[] n = readTwoNumbers();
                        System.out.println("Result: " + engine.multiply(n[0], n[1]));
                    }
                    case 4 -> {
                        double[] n = readTwoNumbers();
                        System.out.println("Result: " + engine.divide(n[0], n[1]));
                    }
                    case 5 -> {
                        System.out.print("Enter number: ");
                        double n = readDouble();
                        System.out.println("Result: " + engine.sqrt(n));
                    }
                    case 6 -> {
                        System.out.print("Enter base: ");
                        double base = readDouble();
                        System.out.print("Enter exponent: ");
                        double exp = readDouble();
                        System.out.println("Result: " + engine.power(base, exp));
                    }
                    case 7 -> engine.showHistory();
                    case 8 -> {
                        running = false;
                        System.out.println("Thank you for using Calculator.");
                    }
                    default -> System.out.println("Invalid choice!");
                }
            } catch (ArithmeticException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // ---------------- Utility Methods ----------------

    private static void showMenu() {
        System.out.println("""
                
                -------- MENU --------
                1. Addition
                2. Subtraction
                3. Multiplication
                4. Division
                5. Square Root
                6. Power
                7. View History
                8. Exit
                ----------------------
                Enter your choice:
                """);
    }

    private static double[] readTwoNumbers() {
        System.out.print("Enter first number: ");
        double a = readDouble();
        System.out.print("Enter second number: ");
        double b = readDouble();
        return new double[]{a, b};
    }

    private static double readDouble() {
        while (!sc.hasNextDouble()) {
            System.out.print("Invalid input. Enter a number: ");
            sc.next();
        }
        return sc.nextDouble();
    }

    private static int readInt() {
        while (!sc.hasNextInt()) {
            System.out.print("Invalid input. Enter an integer: ");
            sc.next();
        }
        return sc.nextInt();
    }
}
