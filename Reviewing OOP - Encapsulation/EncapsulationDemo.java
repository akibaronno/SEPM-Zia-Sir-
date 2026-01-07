class BankAccount {

    // Private data members (data hiding)
    private String accountNumber;
    private double balance;

    // Constructor to initialize account details
    public BankAccount(String accNo, double initialBalance) {
        accountNumber = accNo;
        balance = initialBalance;
    }

    // Public getter method to read balance
    public double getBalance() {
        return balance;
    }

    // Public method to deposit money
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Deposited: " + amount);
        } else {
            System.out.println("Invalid deposit amount");
        }
    }

    // Public method to withdraw money with validation
    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            System.out.println("Withdrawn: " + amount);
        } else {
            System.out.println("Insufficient balance or invalid amount");
        }
    }
}

public class EncapsulationDemo {
    public static void main(String[] args) {

        // Creating object of BankAccount class
        BankAccount account = new BankAccount("ACC12345", 5000);

        // Accessing data through public methods only
        account.deposit(2000);
        account.withdraw(1500);

        // Reading balance using getter
        System.out.println("Current Balance: " + account.getBalance());

        // Direct access is not allowed
        // account.balance = 10000; // ❌ Compile-time error
    }
}
