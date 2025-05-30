package bankapp;

public class SavingsAccount extends BankAccount {
    private double interestRate;
    
    public SavingsAccount(String accountName, double interestRate) {
        super(accountName);
        this.interestRate = interestRate;
        setTransferLimit(1000.0); // Standard limit for savings accounts
    }

    @Override
    public String getAccountType() {
        return "savings";
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void applyInterest() {
        double interest = accountBalance * (interestRate / 100);
        accountBalance += interest;
        System.out.printf("Interest of %.2f applied. New balance: %.2f\n", interest, accountBalance);
    }
}
