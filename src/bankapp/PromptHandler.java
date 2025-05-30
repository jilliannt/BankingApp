package bankapp;

import java.io.IOException;
import java.util.Scanner;

/**
 * Handles user prompts and interaction for bank account operations.
 */
public class PromptHandler {

	/**
	 * Opens a new bank account based on user input.
	 * 
	 * @param scanner The scanner for user input
	 * @return The newly created bank account
	 */
	public static BankAccount openAccount(Scanner scanner) {
		displayAccountTypeMenu();
		String choice = scanner.nextLine();

		if (choice.equals("1")) {
			return createCheckingAccount(scanner);
		} else if (choice.equals("2")) {
			return createSavingsAccount(scanner);
		} else {
			System.out.println("Invalid choice. Creating a default checking account.");
			return new CheckingAccount("Default Checking");
		}
	}

	/**
	 * Displays the account type selection menu.
	 */
	private static void displayAccountTypeMenu() {
		System.out.println("What kind of account would you like to open?");
		System.out.println("1. Checking Account");
		System.out.println("2. Savings Account");
	}

	/**
	 * Creates a new checking account with user input.
	 * 
	 * @param scanner The scanner for user input
	 * @return The new checking account
	 */
	private static CheckingAccount createCheckingAccount(Scanner scanner) {
		System.out.print("Enter a name for your checking account: ");
		String accountName = scanner.nextLine();

		CheckingAccount account = new CheckingAccount(accountName);
		configureOverdraftIfRequested(scanner, account);

		return account;
	}

	/**
	 * Creates a new savings account with user input.
	 * 
	 * @param scanner The scanner for user input
	 * @return The new savings account
	 */
	private static SavingsAccount createSavingsAccount(Scanner scanner) {
		System.out.print("Enter a name for your savings account: ");
		String accountName = scanner.nextLine();

		double interestRate = getSavingsInterestRate(scanner);
		SavingsAccount account = new SavingsAccount(accountName, interestRate);

		configureOverdraftIfRequested(scanner, account);

		return account;
	}

	/**
	 * Gets a valid interest rate for a savings account.
	 * 
	 * @param scanner The scanner for user input
	 * @return The interest rate
	 */
	private static double getSavingsInterestRate(Scanner scanner) {
		double interestRate = 1.5; // Default rate
		try {
			System.out.print("Enter interest rate (%): ");
			interestRate = Double.parseDouble(scanner.nextLine());
		} catch (NumberFormatException e) {
			System.out.println("Invalid interest rate. Using default rate of 1.5%");
		}
		return interestRate;
	}

	/**
	 * Configures overdraft protection if requested by the user.
	 * 
	 * @param scanner The scanner for user input
	 * @param account The account to configure
	 */
	private static void configureOverdraftIfRequested(Scanner scanner, BankAccount account) {
		System.out.print("Would you like to enable overdraft protection? (yes/no): ");
		String overdraftChoice = scanner.nextLine().trim().toLowerCase();

		if (!overdraftChoice.equals("yes")) {
			return;
		}

		double overdraftLimit = getValidOverdraftLimit(scanner);
		if (overdraftLimit <= 0) {
			System.out.println("Invalid overdraft limit. Overdraft protection not enabled.");
			return;
		}

		account.setOverdraftLimit(overdraftLimit);
		configureOverdraftInterestRate(scanner, account);
	}

	/**
	 * Gets a valid overdraft limit from the user.
	 * 
	 * @param scanner The scanner for user input
	 * @return The overdraft limit or 0 if invalid
	 */
	private static double getValidOverdraftLimit(Scanner scanner) {
		try {
			System.out.print("Enter overdraft limit ($): ");
			return Double.parseDouble(scanner.nextLine());
		} catch (NumberFormatException e) {
			System.out.println("Invalid input. Overdraft protection not enabled.");
			return 0;
		}
	}

	/**
	 * Configures the overdraft interest rate for an account.
	 * 
	 * @param scanner The scanner for user input
	 * @param account The account to configure
	 */
	private static void configureOverdraftInterestRate(Scanner scanner, BankAccount account) {
		try {
			System.out.print("Enter overdraft interest rate (%): ");
			double interestRate = Double.parseDouble(scanner.nextLine());
			if (interestRate > 0) {
				account.setOverdraftInterestRate(interestRate);
			} else {
				System.out.println("Invalid interest rate. Using default rate of 15%");
			}
		} catch (NumberFormatException e) {
			System.out.println("Invalid input. Using default rate of 15%");
		}
	}

	/**
	 * Manages multiple bank accounts for a user.
	 * 
	 * @param accountManager The account manager for the user
	 * @param scanner The scanner for user input
	 */
	public static void manageAccounts(AccountManager accountManager, Scanner scanner) {
		boolean continueManaging = true;

		while (continueManaging) {
			displayAccountManagementMenu();
			String choice = getMenuChoice(scanner);
			continueManaging = processAccountManagementChoice(choice, accountManager, scanner);
		}
	}

	/**
	 * Displays the account management menu.
	 */
	private static void displayAccountManagementMenu() {
		System.out.println("\n===== ACCOUNT MANAGEMENT =====");
		System.out.println("1. List All Accounts");
		System.out.println("2. Create New Checking Account");
		System.out.println("3. Create New Savings Account");
		System.out.println("4. Select Account to Manage");
		System.out.println("5. Apply Overdraft Interest to All Accounts");
		System.out.println("6. Apply Savings Interest to All Accounts");
		System.out.println("0. Back to Main Menu");
		System.out.println("=============================");
	}

	/**
	 * Gets the user's menu choice.
	 * 
	 * @param scanner The scanner for user input
	 * @return The user's choice
	 */
	private static String getMenuChoice(Scanner scanner) {
		System.out.print("Enter your choice: ");
		return scanner.nextLine();
	}

	/**
	 * Processes the user's account management menu choice.
	 * 
	 * @param choice The user's choice
	 * @param accountManager The account manager
	 * @param scanner The scanner for user input
	 * @return true to continue managing accounts, false to exit
	 */
	private static boolean processAccountManagementChoice(String choice, AccountManager accountManager, Scanner scanner) {
		switch (choice) {
		case "1":
			accountManager.listAllAccounts();
			return true;
		case "2":
			createCheckingAccount(accountManager, scanner);
			return true;
		case "3":
			createSavingsAccount(accountManager, scanner);
			return true;
		case "4":
			selectAccountToManage(accountManager, scanner);
			return true;
		case "5":
			applyOverdraftInterest(accountManager);
			return true;
		case "6":
			applySavingsInterest(accountManager);
			return true;
		case "0":
			return false;
		default:
			System.out.println("Invalid option. Please try again.");
			return true;
		}
	}

	/**
	 * Applies overdraft interest to all accounts.
	 * 
	 * @param accountManager The account manager
	 */
	private static void applyOverdraftInterest(AccountManager accountManager) {
		double interestCharged = accountManager.applyOverdraftInterestToAllAccounts();
		if (interestCharged > 0) {
			System.out.printf("Applied overdraft interest. Total interest charged: $%.2f\n", interestCharged);
		} else {
			System.out.println("No accounts are currently in overdraft.");
		}
	}

	/**
	 * Applies interest to all savings accounts.
	 * 
	 * @param accountManager The account manager
	 */
	private static void applySavingsInterest(AccountManager accountManager) {
		accountManager.applyInterestToAllSavingsAccounts();
		System.out.println("Applied interest to all savings accounts.");
	}

	/**
	 * Creates a new checking account.
	 * 
	 * @param accountManager The account manager for the user
	 * @param scanner The scanner for user input
	 */
	private static void createCheckingAccount(AccountManager accountManager, Scanner scanner) {
		System.out.println("\n----- Create Checking Account -----");
		System.out.print("Enter a name for your checking account: ");
		String accountName = scanner.nextLine();

		double overdraftLimit = promptForOverdraftLimit(scanner);

		if (accountManager.addCheckingAccount(accountName, overdraftLimit)) {
			System.out.println("Checking account '" + accountName + "' created successfully!");

			if (overdraftLimit > 0) {
				configureOverdraftInterestRate(accountManager, accountName, scanner);
			}
		}
	}

	/**
	 * Prompts for and validates an overdraft limit.
	 * 
	 * @param scanner The scanner for user input
	 * @return The overdraft limit
	 */
	private static double promptForOverdraftLimit(Scanner scanner) {
		double overdraftLimit = 0.0; // Default: no overdraft

		System.out.print("Would you like to enable overdraft protection? (yes/no): ");
		String overdraftChoice = scanner.nextLine().trim().toLowerCase();

		if (overdraftChoice.equals("yes")) {
			overdraftLimit = getValidPositiveDouble(scanner, "Enter overdraft limit ($): ", 
					"Overdraft limit must be greater than zero.");
		}

		return overdraftLimit;
	}

	/**
	 * Gets a valid positive double value from the user.
	 * 
	 * @param scanner The scanner for user input
	 * @param prompt The prompt to display
	 * @param errorMessage The error message to display for invalid input
	 * @return The valid double value or 0 if invalid
	 */
	private static double getValidPositiveDouble(Scanner scanner, String prompt, String errorMessage) {
		boolean validInput = false;
		double value = 0.0;

		while (!validInput) {
			System.out.print(prompt);
			try {
				value = Double.parseDouble(scanner.nextLine());
				if (value <= 0) {
					System.out.println(errorMessage);
				} else {
					validInput = true;
				}
			} catch (NumberFormatException e) {
				System.out.println("Invalid input. Please enter a number.");
			}
		}

		return value;
	}

	/**
	 * Configures the overdraft interest rate for an account.
	 * 
	 * @param accountManager The account manager
	 * @param accountName The name of the account
	 * @param scanner The scanner for user input
	 */
	private static void configureOverdraftInterestRate(AccountManager accountManager, String accountName, Scanner scanner) {
		try {
			System.out.print("Enter overdraft interest rate (%): ");
			double interestRate = Double.parseDouble(scanner.nextLine());
			if (interestRate > 0) {
				accountManager.setOverdraftInterestRate(accountName, interestRate);
				System.out.println("Overdraft interest rate set to " + interestRate + "%");
			} else {
				System.out.println("Interest rate must be positive. Using default rate of 15%");
			}
		} catch (NumberFormatException e) {
			System.out.println("Invalid interest rate. Using default rate of 15%");
		}
	}

	/**
	 * Creates a new savings account.
	 * 
	 * @param accountManager The account manager for the user
	 * @param scanner The scanner for user input
	 */
	private static void createSavingsAccount(AccountManager accountManager, Scanner scanner) {
		System.out.println("\n----- Create Savings Account -----");
		System.out.print("Enter a name for your savings account: ");
		String accountName = scanner.nextLine();

		double interestRate = getValidPositiveDouble(scanner, "Enter interest rate (%): ", 
				"Interest rate must be greater than zero.");

		if (accountManager.addSavingsAccount(accountName, interestRate)) {
			System.out.println("Savings account '" + accountName + "' created successfully!");
			configureSavingsAccountOverdraft(accountManager, accountName, scanner);
		}
	}

	/**
	 * Configures overdraft for a savings account.
	 * 
	 * @param accountManager The account manager
	 * @param accountName The name of the account
	 * @param scanner The scanner for user input
	 */
	private static void configureSavingsAccountOverdraft(AccountManager accountManager, String accountName, Scanner scanner) {
		System.out.print("Would you like to enable overdraft protection? (yes/no): ");
		String overdraftChoice = scanner.nextLine().trim().toLowerCase();

		if (!overdraftChoice.equals("yes")) {
			return;
		}

		double overdraftLimit = getValidPositiveDouble(scanner, "Enter overdraft limit ($): ", 
				"Overdraft limit must be greater than zero.");

		if (accountManager.setOverdraftLimit(accountName, overdraftLimit)) {
			configureOverdraftInterestRate(accountManager, accountName, scanner);
		}
	}

	/**
	 * Selects an account to manage based on its name.
	 * 
	 * @param accountManager The account manager for the user
	 * @param scanner The scanner for user input
	 */
	private static void selectAccountToManage(AccountManager accountManager, Scanner scanner) {
		accountManager.listAllAccounts();

		if (accountManager.getCheckingAccounts().isEmpty() && accountManager.getSavingsAccounts().isEmpty()) {
			System.out.println("You don't have any accounts to manage. Please create an account first.");
			return;
		}

		System.out.print("\nEnter the name of the account you want to manage: ");
		String accountName = scanner.nextLine();

		BankAccount selectedAccount = accountManager.getAccountByName(accountName);

		if (selectedAccount == null) {
			System.out.println("Account not found. Please check the name and try again.");
			return;
		}

		System.out.println("Now managing account: " + selectedAccount.getAccountName());
		commandLoop(accountManager, selectedAccount, scanner);
	}

	/**
	 * Displays the account's balance.
	 * 
	 * @param account The bank account
	 */
	private static void displayBalance(BankAccount account) {
		System.out.printf("Current balance in %s: $%.2f\n", account.getAccountName(), account.getBalance());

		if (account.getBalance() < 0) {
			displayOverdraftInfo(account);
		}

		if (account.getOverdraftLimit() > 0) {
			displayAvailableFundsInfo(account);
		}
	}

	/**
	 * Displays overdraft information for an account.
	 * 
	 * @param account The bank account
	 */
	private static void displayOverdraftInfo(BankAccount account) {
		System.out.printf("Your account is in overdraft by $%.2f\n", Math.abs(account.getBalance()));
		System.out.printf("Overdraft interest rate: %.2f%%\n", account.getOverdraftInterestRate());
	}

	/**
	 * Displays available funds information for an account.
	 * 
	 * @param account The bank account
	 */
	private static void displayAvailableFundsInfo(BankAccount account) {
		double availableFunds = calculateAvailableFunds(account);
		System.out.printf("Overdraft limit: $%.2f\n", account.getOverdraftLimit());
		System.out.printf("Available funds (including overdraft): $%.2f\n", availableFunds);
	}

	/**
	 * Calculates available funds including overdraft.
	 * 
	 * @param account The bank account
	 * @return Total available funds
	 */
	private static double calculateAvailableFunds(BankAccount account) {
		return account.getBalance() >= 0 ? 
				account.getBalance() + account.getOverdraftLimit() : 
					account.getOverdraftLimit() - Math.abs(account.getBalance());
	}

	/**
	 * Handles the deposit process for an account.
	 * 
	 * @param accountManager The account manager
	 * @param account The bank account
	 * @param scanner The scanner for user input
	 */
	public static void handleDeposit(AccountManager accountManager, BankAccount account, Scanner scanner) {
		if (account.isFrozen()) {
			System.out.println("This account is frozen. Unfreeze it first to make deposits.");
			return;
		}

		System.out.print("Enter amount to deposit: $");
		double amount = parseAmount(scanner);

		if (amount <= 0) {
			System.out.println("Deposit amount must be positive.");
			return;
		}

		account.deposit(amount);
		recordDepositTransaction(accountManager, account, amount);
		displayDepositResult(amount, account.getBalance());
	}

	/**
	 * Parses and validates an amount from user input.
	 * 
	 * @param scanner The scanner for user input
	 * @return The amount entered, or -1 if invalid
	 */
	private static double parseAmount(Scanner scanner) {
		try {
			return Double.parseDouble(scanner.nextLine());
		} catch (NumberFormatException e) {
			System.out.println("Invalid amount. Please enter a valid number.");
			return -1;
		}
	}

	/**
	 * Records a deposit transaction in account history.
	 * 
	 * @param accountManager The account manager
	 * @param account The bank account
	 * @param amount The deposit amount
	 */
	private static void recordDepositTransaction(AccountManager accountManager, BankAccount account, double amount) {
		AccountStorage accountStorage = new AccountStorage();
		try {
			accountStorage.recordTransaction(
					accountManager.getUsername(), 
					account.getAccountName(), 
					"Deposit: $" + amount
					);
		} catch (IOException e) {
			System.err.println("Error recording transaction: " + e.getMessage());
		}
	}

	/**
	 * Displays the result of a deposit operation.
	 * 
	 * @param amount The amount deposited
	 * @param newBalance The new account balance
	 */
	private static void displayDepositResult(double amount, double newBalance) {
		System.out.printf("Deposited $%.2f. New balance: $%.2f\n", amount, newBalance);
	}

	/**
	 * Handles the withdrawal process for an account.
	 * 
	 * @param accountManager The account manager
	 * @param account The bank account
	 * @param scanner The scanner for user input
	 */
	public static void handleWithdrawal(AccountManager accountManager, BankAccount account, Scanner scanner) {
		if (account.isFrozen()) {
			System.out.println("This account is frozen. Unfreeze it first to make withdrawals.");
			return;
		}

		displayWithdrawalLimits(account);
		System.out.print("Enter amount to withdraw: $");
		double amount = parseAmount(scanner);

		if (amount <= 0) {
			System.out.println("Withdrawal amount must be positive.");
			return;
		}

		if (!account.withdraw(amount)) {
			return; // Withdrawal failed (error message already displayed in BankAccount.withdraw)
		}

		recordWithdrawalTransaction(accountManager, account, amount);
		displayWithdrawalResult(amount, account.getBalance());

		if (account.getBalance() < 0) {
			displayOverdraftWarning(account);
		}
	}

	/**
	 * Displays withdrawal limits for an account.
	 * 
	 * @param account The bank account
	 */
	private static void displayWithdrawalLimits(BankAccount account) {
		System.out.printf("(Per-transaction limit: $%.2f)\n", account.getWithdrawalLimit());

		if (account.getOverdraftLimit() > 0) {
			double availableFunds = calculateAvailableFunds(account);
			System.out.printf("Available funds (including overdraft): $%.2f\n", availableFunds);
		}
	}

	/**
	 * Records a withdrawal transaction in account history.
	 * 
	 * @param accountManager The account manager
	 * @param account The bank account
	 * @param amount The withdrawal amount
	 */
	private static void recordWithdrawalTransaction(AccountManager accountManager, BankAccount account, double amount) {
		AccountStorage accountStorage = new AccountStorage();
		try {
			accountStorage.recordTransaction(
					accountManager.getUsername(), 
					account.getAccountName(), 
					"Withdraw: $" + amount
					);
		} catch (IOException e) {
			System.err.println("Error recording transaction: " + e.getMessage());
		}
	}

	/**
	 * Displays the result of a withdrawal operation.
	 * 
	 * @param amount The amount withdrawn
	 * @param newBalance The new account balance
	 */
	private static void displayWithdrawalResult(double amount, double newBalance) {
		System.out.printf("Withdrew $%.2f. New balance: $%.2f\n", amount, newBalance);
	}

	/**
	 * Displays a warning when account is in overdraft.
	 * 
	 * @param account The bank account
	 */
	private static void displayOverdraftWarning(BankAccount account) {
		System.out.printf("Your account is now in overdraft by $%.2f\n", 
				Math.abs(account.getBalance()));
		System.out.printf("You will be charged %.2f%% interest on this amount until it is repaid.\n", 
				account.getOverdraftInterestRate());
	}

	/**
	 * Handles applying interest to a savings account.
	 * 
	 * @param account The bank account
	 */
	public static void handleInterest(BankAccount account) {
		if (account.isFrozen()) {
			System.out.println("This account is frozen. Unfreeze it first to apply interest.");
			return;
		}

		if (account instanceof SavingsAccount) {
			SavingsAccount savingsAccount = (SavingsAccount) account;
			savingsAccount.applyInterest();
			System.out.printf("Interest applied. New balance: $%.2f\n", account.getBalance());
		} else {
			System.out.println("This account does not earn interest.");
		}
	}

	/**
	 * Handles ordering checks and debit cards for a checking account.
	 * 
	 * @param account The bank account
	 */
	public static void handleOrderChecksAndDebitCards(AccountManager accountManager, BankAccount account, Scanner scanner) {

		if (account.isFrozen()) {
			System.out.println("This account is frozen. Unfreeze it first to order checks.");
			return;
		}

		if (account instanceof CheckingAccount) {
			System.out.println("Would you like to order checks or a debit card?");
			System.out.println("1. Checks");
			System.out.println("2. Debit Card");
			System.out.println("=============================");
			System.out.print("Enter your choice: ");
			String choice = scanner.nextLine();
			CheckingAccount checkingAccount = (CheckingAccount) account;
			if (choice.equals("1")) {
				checkingAccount.orderChecks();
				AccountStorage accountStorage = new AccountStorage();
				try {
					accountStorage.recordTransaction(accountManager.getUsername(), account.getAccountName(), "Ordered checks");
				} catch (IOException e) {
					System.out.println("Error recording transaction: " + e.getMessage());
				}
			} else if (choice.equals("2")) {
				String lastFour = checkingAccount.orderDebitCard();
				AccountStorage accountStorage = new AccountStorage();
				try {
					accountStorage.recordTransaction(accountManager.getUsername(), account.getAccountName(), "Ordered debit card ending in " + lastFour);
				} catch (IOException e) {
					System.out.println("Error recording transaction: " + e.getMessage());
				}
			} else {
				System.out.println("Invalid choice. Please try again.");
			}
		} else {
			System.out.println("You can only order checks for a checking account.");
		}
	}

	/**
	 * Handles modifying overdraft settings for an account.
	 * 
	 * @param accountManager The account manager for the user
	 * @param account The bank account
	 * @param scanner The scanner for user input
	 */
	public static void handleOverdraftSettings(AccountManager accountManager, BankAccount account, Scanner scanner) {
		displayOverdraftSettingsMenu(account);
		String choice = scanner.nextLine();
		processOverdraftSettingsChoice(choice, accountManager, account, scanner);
	}

	/**
	 * Displays the overdraft settings menu.
	 * 
	 * @param account The bank account
	 */
	private static void displayOverdraftSettingsMenu(BankAccount account) {
		System.out.println("\n----- Overdraft Settings -----");
		System.out.printf("Current overdraft limit: $%.2f\n", account.getOverdraftLimit());
		System.out.printf("Current overdraft interest rate: %.2f%%\n", account.getOverdraftInterestRate());

		System.out.println("\n1. Update Overdraft Limit");
		System.out.println("2. Update Overdraft Interest Rate");
		System.out.println("0. Back");

		System.out.print("\nEnter your choice: ");
	}

	/**
	 * Processes the user's overdraft settings choice.
	 * 
	 * @param choice The user's choice
	 * @param accountManager The account manager
	 * @param account The bank account
	 * @param scanner The scanner for user input
	 */
	private static void processOverdraftSettingsChoice(String choice, AccountManager accountManager, 
			BankAccount account, Scanner scanner) {
		switch (choice) {
		case "1":
			updateOverdraftLimit(accountManager, account, scanner);
			break;

		case "2":
			updateOverdraftInterestRate(accountManager, account, scanner);
			break;

		case "0":
			// Do nothing, just go back
			break;

		default:
			System.out.println("Invalid option. Please try again.");
		}
	}

	/**
	 * Updates the overdraft limit for an account.
	 * 
	 * @param accountManager The account manager
	 * @param account The bank account
	 * @param scanner The scanner for user input
	 */
	private static void updateOverdraftLimit(AccountManager accountManager, BankAccount account, Scanner scanner) {
		System.out.print("Enter new overdraft limit ($): ");
		try {
			double newLimit = Double.parseDouble(scanner.nextLine());
			if (newLimit >= 0) {
				accountManager.setOverdraftLimit(account.getAccountName(), newLimit);
				System.out.printf("Overdraft limit updated to $%.2f\n", newLimit);
			} else {
				System.out.println("Overdraft limit cannot be negative.");
			}
		} catch (NumberFormatException e) {
			System.out.println("Invalid input. Please enter a number.");
		}
	}

	/**
	 * Updates the overdraft interest rate for an account.
	 * 
	 * @param accountManager The account manager
	 * @param account The bank account
	 * @param scanner The scanner for user input
	 */
	private static void updateOverdraftInterestRate(AccountManager accountManager, BankAccount account, Scanner scanner) {
		System.out.print("Enter new overdraft interest rate (%): ");
		try {
			double newRate = Double.parseDouble(scanner.nextLine());
			if (newRate >= 0) {
				accountManager.setOverdraftInterestRate(account.getAccountName(), newRate);
				System.out.printf("Overdraft interest rate updated to %.2f%%\n", newRate);
			} else {
				System.out.println("Interest rate cannot be negative.");
			}
		} catch (NumberFormatException e) {
			System.out.println("Invalid input. Please enter a number.");
		}
	}

	/**
	 * Handles freezing or unfreezing an account.
	 *
	 * @param accountManager The account manager for the user
	 * @param account The bank account
	 * @param scanner The scanner for user input
	 */
	public static void handleFreezeUnfreezeAccount(AccountManager accountManager, BankAccount account, Scanner scanner) {
		if (account.isFrozen()) {
			handleUnfreezeAccount(accountManager, account, scanner);
		} else {
			handleFreezeAccountAction(accountManager, account, scanner);
		}
	}

	/**
	 * Handles unfreezing a frozen account.
	 *
	 * @param accountManager The account manager
	 * @param account The bank account
	 * @param scanner The scanner for user input
	 */
	private static void handleUnfreezeAccount(AccountManager accountManager, BankAccount account, Scanner scanner) {
		System.out.println("This account is currently frozen. Would you like to unfreeze it? (yes/no): ");
		String choice = scanner.nextLine().trim().toLowerCase();

		if (choice.equals("yes")) {
			accountManager.unfreezeAccount(account.getAccountName());
			System.out.println("Account unfrozen successfully.");
		}
	}

	/**
	 * Handles freezing an active account.
	 *
	 * @param accountManager The account manager
	 * @param account The bank account
	 * @param scanner The scanner for user input
	 */
	private static void handleFreezeAccountAction(AccountManager accountManager, BankAccount account, Scanner scanner) {
		System.out.println("Are you sure you want to freeze this account? This will prevent any transactions until unfrozen. (yes/no): ");
		String choice = scanner.nextLine().trim().toLowerCase();

		if (choice.equals("yes")) {
			accountManager.freezeAccount(account.getAccountName());
			System.out.println("Account frozen successfully.");
		}
	}
	
	/**
	 * Handles updating the transfer limit for an account.
	 * 
	 * @param accountManager The account manager
	 * @param account The bank account
	 * @param scanner The scanner for user input
	 */
	private static void handleTransferLimit(AccountManager accountManager, BankAccount account, Scanner scanner) {
	    System.out.println("\n----- Update Transfer Limit -----");
	    System.out.printf("Current transfer limit: $%.2f\n", account.getTransferLimit());
	    
	    System.out.print("Enter new transfer limit: $");
	    try {
	        double newLimit = Double.parseDouble(scanner.nextLine());
	        if (newLimit <= 0) {
	            System.out.println("Transfer limit must be greater than zero.");
	            return;
	        }
	        
	        // Update the limit
	        accountManager.setTransferLimit(account.getAccountName(), newLimit);
	        System.out.printf("Transfer limit updated to $%.2f\n", newLimit);
	    } catch (NumberFormatException e) {
	        System.out.println("Invalid input. Please enter a valid number.");
	    }
	}

	/**
	 * Handles the process of closing a bank account.
	 * 
	 * The account can only be closed if its balance is zero or positive.
	 * If the balance is negative, the user needs to repay the overdraft first.
	 * The user must also confirm the closure with a "yes" input.
	 * 
	 * @param accountManager The manager containing all of the user's accounts
	 * @param account The account to be closed
	 * @param scanner The scanner used to read user input
	 * @return true if the account was successfully closed, false otherwise
	 */
	public static boolean handleCloseAccount(AccountManager accountManager, BankAccount account, Scanner scanner) {
		if (!account.canClose()) {
			System.out.printf("Cannot close account '%s'. Balance is $%.2f — please deposit funds to cover the overdraft first.\n",
					account.getAccountName(), account.getBalance());
			return false;
		}

		System.out.print("Are you sure you want to close this account? This action cannot be undone. (yes/no): ");
		String confirmation = scanner.nextLine().trim().toLowerCase();

		if (!confirmation.equals("yes")) {
			System.out.println("Account closure canceled.");
			return false;
		}

		boolean removed = accountManager.closeAccount(account.getAccountName());
		if (removed) {
			System.out.println("Account closed successfully.");
			return true; 
		} else {
			System.out.println("Error closing account.");
			return false;
		}
	}

	public static void handleDepositChecks(AccountManager accountManager, BankAccount account, Scanner scanner) {
		if (account.isFrozen()) {
			System.out.println("This account is frozen. Unfreeze it first to deposit checks.");
			return;
		}

		System.out.print("Enter amount to deposit: $");
		double amount = parseAmount(scanner);

		if (amount <= 0) {
			System.out.println("Deposit amount must be positive.");
			return;
		}

		account.deposit(amount);
		System.out.print("Enter check number: ");
		String checkNumber = scanner.nextLine();
		AccountStorage accountStorage = new AccountStorage();
		try {
			accountStorage.recordTransaction(accountManager.getUsername(), account.getAccountName(), "Deposited Check #" + checkNumber + ": $" + amount);
		} catch (IOException e) {
			System.err.println("Error recording transaction: " + e.getMessage());
		}

	}

	/**
	 * Main command loop for account operations.
	 * 
	 * @param accountManager The account manager
	 * @param account The bank account to operate on
	 * @param scanner The scanner for user input
	 */
	public static void commandLoop(AccountManager accountManager, BankAccount account, Scanner scanner) {
		boolean exitRequested = false;

		while (!exitRequested) {

			displayOperationsMenu(account);
			String choice = scanner.nextLine();
			exitRequested = processAccountOperationChoice(choice, accountManager, account, scanner);
		}
	}

	/**
	 * Displays the account operations menu.
	 * 
	 * @param account The bank account
	 */
	private static void displayOperationsMenu(BankAccount account) {
		System.out.println("\n===== ACCOUNT OPERATIONS =====");
		System.out.printf("Account: %s (%s)%s\n", 
				account.getAccountName(), 
				account.getAccountType(),
				account.isFrozen() ? " [FROZEN]" : "");
		System.out.println("1. Check Balance");
		System.out.println("2. Make Deposit");
		System.out.println("3. Make Withdrawal");

		// Display account-specific options
		if (account instanceof SavingsAccount) {
			System.out.println("4. Apply Interest");
		} else if (account instanceof CheckingAccount) {
			System.out.println("4. Order Checks or a Debit Card");
		}

		System.out.println("5. View Transaction History");
		System.out.println("6. View Last 5 Transactions");
		System.out.println("7. Transfer to Another Account");
		System.out.println("8. Overdraft Settings");
		System.out.println("9. " + (account.isFrozen() ? "Unfreeze" : "Freeze") + " Account");
		System.out.println("10. Deposit Checks");
		System.out.println("11. Update Transfer Limit"); // Add this new option
		System.out.println("12. Close This Account"); // Move this from 11 to 12
		System.out.println("0. Return to Account Selection");
		System.out.println("==============================");

		System.out.print("Enter your choice: ");
	}

	/**
	 * Processes the user's account operation choice.
	 * 
	 * @param choice The user's choice
	 * @param accountManager The account manager
	 * @param account The bank account
	 * @param scanner The scanner for user input
	 * @return true if the user wants to exit, false otherwise
	 */
	private static boolean processAccountOperationChoice(String choice, AccountManager accountManager,
			BankAccount account, Scanner scanner) {
		switch (choice) {
		case "1":
			displayBalance(account);
			return false;
		case "2":
			handleDeposit(accountManager, account, scanner);
			return false;
		case "3":
			handleWithdrawal(accountManager, account, scanner);
			return false;
		case "4":
			handleOption4(accountManager, account, scanner);
			return false;
		case "5":
			viewTransactionHistory(accountManager, account);
			return false;
		case "6":
			viewRecentTransactions(accountManager, account);
			return false;
		case "7":
			handleTransfer(accountManager, account, scanner);
			return false;
		case "8":
			handleOverdraftSettings(accountManager, account, scanner);
			return false;
		case "9":
			handleFreezeUnfreezeAccount(accountManager, account, scanner);
			return false;
		case "10":
			handleDepositChecks(accountManager, account, scanner);
			return false;
		case "11":
			handleTransferLimit(accountManager, account, scanner); // Add this new case
			return false;
		case "12": // Changed from 11 to 12
			return handleCloseAccount(accountManager, account, scanner);
		case "0":
			return true;
		default:
			System.out.println("Invalid choice. Please try again.");
			return false;
		}
	}
	

	/**
	 * Handles option 4 which varies based on account type.
	 * 
	 * @param account The bank account
	 */
	private static void handleOption4(AccountManager accountManager, BankAccount account, Scanner scanner) {
		if (account instanceof SavingsAccount) {
			handleInterest(account);
		} else if (account instanceof CheckingAccount) {
			handleOrderChecksAndDebitCards(accountManager, account, scanner);
		} else {
			System.out.println("Invalid option for this account type.");
		}
	}

	/**
	 * Displays transaction history for an account.
	 * 
	 * @param accountManager The account manager
	 * @param account The bank account
	 */
	private static void viewTransactionHistory(AccountManager accountManager, BankAccount account) {
		AccountStorage accountStorage = new AccountStorage();
		try {
			System.out.println("Transaction History:");
			for (String transaction : accountStorage.getAccountHistory(
					accountManager.getUsername(), account.getAccountName())) {
				System.out.println(transaction);
			}
		} catch (IOException e) {
			System.out.println("Error retrieving transaction history: " + e.getMessage());
		}
	}

	/**
	 * Displays recent transactions for an account.
	 * 
	 * @param accountManager The account manager
	 * @param account The bank account
	 */
	private static void viewRecentTransactions(AccountManager accountManager, BankAccount account) {
		AccountStorage accountStorage = new AccountStorage();
		try {
			System.out.println("Last 5 Transactions:");
			for (String transaction : accountStorage.getLastFiveTransactions(
					accountManager.getUsername(), account.getAccountName())) {
				System.out.println(transaction);

			}
		} catch (IOException e) {
			System.out.println("Error retrieving last 5 transactions: " + e.getMessage());
		}
	}

	/**
	 * Handles transferring money between accounts.
	 * 
	 * @param accountManager The account manager
	 * @param account The source bank account
	 * @param scanner The scanner for user input
	 */
	private static void handleTransfer(AccountManager accountManager, BankAccount account, Scanner scanner) {
		if (account.isFrozen()) {
			System.out.println("This account is frozen. Unfreeze it first to make transfers.");
		} else {
			TransferHandler.handleTransfer(accountManager, account, scanner);
		}
	}
}