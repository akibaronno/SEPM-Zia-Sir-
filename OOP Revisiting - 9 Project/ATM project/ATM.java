import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class BankAccount {

    // Encapsulated data
    private double balance;
    private final int pin;
    private final List<String> transactions;

    // Constructor
    public BankAccount(double initialBalance, int pin) {
        this.balance = initialBalance;
        this.pin = pin;
        this.transactions = new ArrayList<>();
        transactions.add("Account opened with balance: " + initialBalance);
    }

    // PIN verification
    public boolean verifyPin(int enteredPin) {
        return pin == enteredPin;
    }

    // Deposit money
    public void deposit(double amount) {
        if (amount <= 0) {
            System.out.println("Invalid deposit amount!");
            return;
        }
        balance += amount;
        transactions.add("Deposited: " + amount);
        System.out.println("Deposit successful.");
    }

    // Withdraw money
    public void withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Invalid withdrawal amount!");
        } else if (amount > balance) {
            System.out.println("Insufficient balance!");
        } else {
            balance -= amount;
            transactions.add("Withdrawn: " + amount);
            System.out.println("Withdrawal successful.");
        }
    }

    // Check balance
    public void checkBalance() {
        System.out.println("Current Balance: " + balance);
    }

    // Show transaction history
    public void showTransactions() {
        System.out.println("\n--- Transaction History ---");
        for (String t : transactions) {
            System.out.println(t);
        }
    }
}

public class ATM {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        // Create account
        BankAccount account = new BankAccount(1000.0, 1234);

        System.out.println("===== Welcome to ATM System =====");

        // Authentication
        if (!authenticate(account)) {
            System.out.println("Too many incorrect attempts. Card blocked!");
            return;
        }

        // ATM menu loop
        while (true) {
            showMenu();
            int choice = scanner.nextInt();

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter deposit amount: ");
                    account.deposit(scanner.nextDouble());
                }
                case 2 -> {
                    System.out.print("Enter withdrawal amount: ");
                    account.withdraw(scanner.nextDouble());
                }
                case 3 -> account.checkBalance();
                case 4 -> account.showTransactions();
                case 5 -> {
                    System.out.println("Thank you for using ATM. Goodbye!");
                    System.exit(0);
                }
                default -> System.out.println("Invalid option. Try again!");
            }
        }
    }

    // Menu display
    private static void showMenu() {
        System.out.println("""
                
                ----- ATM MENU -----
                1. Deposit
                2. Withdraw
                3. Check Balance
                4. Transaction History
                5. Exit
                --------------------
                Enter your choice:
                """);
    }

    // PIN authentication
    private static boolean authenticate(BankAccount account) {
        int attempts = 3;

        while (attempts > 0) {
            System.out.print("Enter PIN: ");
            int enteredPin = scanner.nextInt();

            if (account.verifyPin(enteredPin)) {
                System.out.println("Authentication successful.\n");
                return true;
            } else {
                attempts--;
                System.out.println("Incorrect PIN. Attempts left: " + attempts);
            }
        }
        return false;
    }
}
