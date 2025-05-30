package bankapp;
public abstract class BankAccount {
	protected double accountBalance;
	protected String accountName;
	protected double withdrawalLimit;
	protected double overdraftLimit;       // Maximum amount of overdraft allowed
	protected double overdraftInterestRate; // Interest rate charged on overdrafts
	protected boolean isFrozen;            // Flag to indicate if account is frozen
	protected double overdraftAmount;      // Current amount in overdraft (for tracking)
	protected double transferLimit;


	//initialization constructor with account name
	public BankAccount(String accountName) {
		this.accountBalance = 0.0;
		this.accountName = accountName;
		this.withdrawalLimit = 10000.0; 
		this.overdraftLimit = 0.0;  // Default: no overdraft allowed
		this.overdraftInterestRate = 15.0; // Default: 15% interest rate on overdrafts
		this.isFrozen = false;
		this.overdraftAmount = 0.0;
		this.transferLimit = 1000.0;
	}

	// Getter and setter for transfer limit
	public double getTransferLimit() {
		return transferLimit;
	}

	public void setTransferLimit(double transferLimit) {
	    if (transferLimit > 0) {
	        this.transferLimit = transferLimit;
	    } else {
	        System.out.println("Transfer limit must be positive.");
	    }
	}


	public void setWithdrawalLimit(double limit) {
		this.withdrawalLimit = limit;
	}

	public double getWithdrawalLimit() {
		return withdrawalLimit;
	}

	public void setOverdraftLimit(double limit) {
		this.overdraftLimit = limit;
	}

	public double getOverdraftLimit() {
		return overdraftLimit;
	}

	public void setOverdraftInterestRate(double rate) {
		this.overdraftInterestRate = rate;
	}

	public double getOverdraftInterestRate() {
		return overdraftInterestRate;
	}

	public double getOverdraftAmount() {
		return Math.max(0, -accountBalance);
	}

	public boolean isFrozen() {
		return isFrozen;
	}

	public void freezeAccount() {
		this.isFrozen = true;
	}

	public void unfreezeAccount() {
		this.isFrozen = false;
	}

	public String getAccountName() {
		return accountName;
	}

	public double getBalance() {
		//simple getter function
		return accountBalance;
	}

	public void deposit(double amount) {
		if (isFrozen) {
			System.out.println("Cannot deposit to a frozen account.");
			return;
		}
		accountBalance += amount;
	}

	public boolean withdraw(double amount) {
		if (isFrozen) {
			System.out.println("Cannot withdraw from a frozen account.");
			return false;
		}

		if (amount > withdrawalLimit) {
			System.out.printf("Withdrawal exceeds the limit of $%.2f per transaction.\n", withdrawalLimit);
			return false;
		}

		// Check if withdrawal is within balance + overdraft limit
		if (accountBalance >= amount || Math.abs(accountBalance - amount) <= overdraftLimit) {
			accountBalance -= amount;
			return true;
		} else {
			System.out.printf("Insufficient funds. Your maximum withdrawal amount is $%.2f.\n", 
					accountBalance + overdraftLimit);
			return false;
		}
	}

	/**
	 * Applies overdraft interest to the account if it's in overdraft.
	 * @return The amount of interest charged
	 */
	public double applyOverdraftInterest() {
		if (accountBalance < 0) {
			double overdraftAmount = Math.abs(accountBalance);
			double interestAmount = overdraftAmount * (overdraftInterestRate / 100);
			accountBalance -= interestAmount;
			return interestAmount;
		}
		return 0.0;
	}

	/**
	 * Checks if this account can be closed.
	 * An account can only be closed if its balance is zero or positive.
	 * @return true if the account can be closed, false otherwise
	 */
	public boolean canClose() {
		return accountBalance >= 0;
	}

	public abstract String getAccountType(); // implemented by subclasses
}