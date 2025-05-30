package bankapp;

import java.io.IOException;
import java.util.Scanner;

/**
 * Handles money transfers between accounts.
 */
public class TransferHandler {

	/**
	 * Handles the transfer process from one account to another.
	 * 
	 * @param accountManager The account manager
	 * @param sourceAccount The source account
	 * @param scanner The scanner for user input
	 */
	public static void handleTransfer(AccountManager accountManager, BankAccount sourceAccount, Scanner scanner) {
		// Display available accounts
		displayAvailableTargetAccounts(accountManager, sourceAccount);

		// Get target account
		BankAccount targetAccount = getTargetAccount(accountManager, sourceAccount, scanner);
		if (targetAccount == null) {
			return;
		}

		// Get transfer amount
		double amount = getTransferAmount(scanner);
		if (amount <= 0) {
			System.out.println("Transfer amount must be positive.");
			return;
		}

		// Execute transfer
		executeTransfer(accountManager, sourceAccount, targetAccount, amount);
	}

	/**
	 * Displays accounts available for transfer (excluding the source account).
	 * 
	 * @param accountManager The account manager
	 * @param sourceAccount The source account
	 */
	private static void displayAvailableTargetAccounts(AccountManager accountManager, BankAccount sourceAccount) {
	    System.out.println("\n===== AVAILABLE ACCOUNTS FOR TRANSFER =====");
	    System.out.printf("From: %s (Current Balance: $%.2f)\n", 
	                    sourceAccount.getAccountName(), sourceAccount.getBalance());
	    System.out.printf("Transfer Limit: $%.2f\n", sourceAccount.getTransferLimit());
	    
	    // Display checking accounts
	    displayAvailableCheckingAccounts(accountManager, sourceAccount);
	    
	    // Display savings accounts
	    displayAvailableSavingsAccounts(accountManager, sourceAccount);
	    
	    System.out.println("==========================================");
	}

	/**
	 * Displays checking accounts available for transfer.
	 * 
	 * @param accountManager The account manager
	 * @param sourceAccount The source account
	 */
	private static void displayAvailableCheckingAccounts(AccountManager accountManager, BankAccount sourceAccount) {
		System.out.println("\nChecking Accounts:");
		boolean hasOtherAccounts = false;

		for (CheckingAccount account : accountManager.getCheckingAccounts()) {
			if (!account.getAccountName().equals(sourceAccount.getAccountName())) {
				System.out.printf("- %s: $%.2f%s\n", 
						account.getAccountName(), 
						account.getBalance(),
						account.isFrozen() ? " [FROZEN]" : "");
				hasOtherAccounts = true;
			}
		}

		if (!hasOtherAccounts) {
			System.out.println("(None available)");
		}
	}

	/**
	 * Displays savings accounts available for transfer.
	 * 
	 * @param accountManager The account manager
	 * @param sourceAccount The source account
	 */
	private static void displayAvailableSavingsAccounts(AccountManager accountManager, BankAccount sourceAccount) {
		System.out.println("\nSavings Accounts:");
		boolean hasOtherAccounts = false;

		for (SavingsAccount account : accountManager.getSavingsAccounts()) {
			if (!account.getAccountName().equals(sourceAccount.getAccountName())) {
				System.out.printf("- %s: $%.2f%s\n", 
						account.getAccountName(), 
						account.getBalance(),
						account.isFrozen() ? " [FROZEN]" : "");
				hasOtherAccounts = true;
			}
		}

		if (!hasOtherAccounts) {
			System.out.println("(None available)");
		}
	}

	/**
	 * Gets the target account for transfer.
	 * 
	 * @param accountManager The account manager
	 * @param sourceAccount The source account
	 * @param scanner The scanner for user input
	 * @return The target account, or null if invalid
	 */
	private static BankAccount getTargetAccount(AccountManager accountManager, 
			BankAccount sourceAccount, 
			Scanner scanner) {
		System.out.print("\nEnter the name of the account to transfer to: ");
		String targetAccountName = scanner.nextLine();

		BankAccount targetAccount = accountManager.getAccountByName(targetAccountName);

		if (targetAccount == null) {
			System.out.println("Account not found: " + targetAccountName);
			return null;
		}

		if (targetAccount.getAccountName().equals(sourceAccount.getAccountName())) {
			System.out.println("Cannot transfer to the same account.");
			return null;
		}

		if (targetAccount.isFrozen()) {
			System.out.println("Target account is frozen. Cannot transfer funds to it.");
			return null;
		}

		return targetAccount;
	}

	/**
	 * Gets the transfer amount from the user.
	 * 
	 * @param scanner The scanner for user input
	 * @return The transfer amount, or 0 if invalid
	 */
	private static double getTransferAmount(Scanner scanner) {
		System.out.print("Enter amount to transfer: $");

		try {
			return Double.parseDouble(scanner.nextLine());
		} catch (NumberFormatException e) {
			System.out.println("Invalid amount. Please enter a number.");
			return 0;
		}
	}

	/**
	 * Executes the transfer between accounts.
	 * 
	 * @param accountManager The account manager
	 * @param sourceAccount The source account
	 * @param targetAccount The target account
	 * @param amount The transfer amount
	 */
	private static void executeTransfer(AccountManager accountManager, 
			BankAccount sourceAccount, 
			BankAccount targetAccount, 
			double amount) {
		// First check the transfer limit
		if (amount > sourceAccount.getTransferLimit()) {
			System.out.printf("Transfer exceeds the limit of $%.2f for this account.\n", 
					sourceAccount.getTransferLimit());
			return;
		}

		// Check if source account has sufficient funds
		if (!sourceAccount.withdraw(amount)) {
			return; // Withdrawal failed (error message already displayed)
		}

		// Deposit to target account
		targetAccount.deposit(amount);

		// Record the transactions
		recordTransferTransactions(accountManager, sourceAccount, targetAccount, amount);

		// Success message
		System.out.printf("Successfully transferred $%.2f from %s to %s.\n", 
				amount, sourceAccount.getAccountName(), targetAccount.getAccountName());

		// Show new balances
		System.out.printf("New balance in %s: $%.2f\n", 
				sourceAccount.getAccountName(), sourceAccount.getBalance());
		System.out.printf("New balance in %s: $%.2f\n", 
				targetAccount.getAccountName(), targetAccount.getBalance());
	}

	/**
	 * Records transfer transactions in the account history.
	 * 
	 * @param accountManager The account manager
	 * @param sourceAccount The source account
	 * @param targetAccount The target account
	 * @param amount The transfer amount
	 */
	private static void recordTransferTransactions(AccountManager accountManager, 
			BankAccount sourceAccount, 
			BankAccount targetAccount, 
			double amount) {
		AccountStorage accountStorage = new AccountStorage();
		String username = accountManager.getUsername();

		try {
			// Record the withdrawal from source account
			accountStorage.recordTransaction(username, sourceAccount.getAccountName(), 
					String.format("Transfer to %s: $%.2f", targetAccount.getAccountName(), amount));

			// Record the deposit to target account
			accountStorage.recordTransaction(username, targetAccount.getAccountName(), 
					String.format("Transfer from %s: $%.2f", sourceAccount.getAccountName(), amount));
		} catch (IOException e) {
			System.err.println("Error recording transfer transactions: " + e.getMessage());
		}
	}
}