interface BankService {
    void withdraw(double amount);
    void checkBalance();
}

abstract class ATMBase implements BankService {

    protected double balance;

    public ATMBase(double balance) {
        this.balance = balance;
    }

    public void authenticate(int pin) {
        if (pin == 1234) {
            System.out.println("Authentication successful.");
        } else {
            System.out.println("Authentication failed.");
            System.exit(0);
        }
    }

    public abstract void deposit(double amount);
}

class BankATM extends ATMBase {

    public BankATM(double balance) {
        super(balance);
    }

    @Override
    public void deposit(double amount) {
        balance += amount;
        System.out.println("Deposited: " + amount);
    }

    @Override
    public void withdraw(double amount) {
        if (amount <= balance) {
            balance -= amount;
            System.out.println("Withdrawn: " + amount);
        } else {
            System.out.println("Insufficient balance.");
        }
    }

    @Override
    public void checkBalance() {
        System.out.println("Current Balance: " + balance);
    }
}

public class Main {

    public static void main(String[] args) {

        BankATM atm = new BankATM(1000);

        atm.authenticate(1234);
        atm.deposit(500);
        atm.withdraw(300);
        atm.checkBalance();
    }
}
