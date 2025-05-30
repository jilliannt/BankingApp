package bankapp;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages multiple bank accounts for a user, including both checking and savings accounts.
 * Handles account creation, retrieval, and persistence operations.
 */
public class AccountManager {
    private String username;
    private List<CheckingAccount> checkingAccounts;
    private List<SavingsAccount> savingsAccounts;
    
    // Constants
    private static final int MAX_CHECKING_ACCOUNTS = 2;
    private static final int MAX_SAVINGS_ACCOUNTS = 3;
    private static final String ACCOUNTS_DIRECTORY = "data/Accounts/";
    
    /**
     * Creates a new AccountManager for the specified user.
     * 
     * @param username The username this account manager belongs to
     */
    public AccountManager(String username) {
        this.username = username;
        this.checkingAccounts = new ArrayList<>();
        this.savingsAccounts = new ArrayList<>();
        createAccountsDirectory();
    }

    /**
     * Gets the username associated with this account manager
     * 
     * @return The username
     */
    public String getUsername() {
        return this.username; 
    }

    /**
     * Creates the directory structure for storing account data.
     */
    private void createAccountsDirectory() {
        try {
            Path userAccountsDir = Paths.get(ACCOUNTS_DIRECTORY + username);
            Files.createDirectories(userAccountsDir);
        } catch (IOException exception) {
            System.err.println("Failed to create accounts directory: " + exception.getMessage());
        }
    }
    
    /**
     * Adds a new checking account with the given name.
     * 
     * @param accountName The name for the new checking account
     * @param overdraftLimit Optional overdraft limit
     * @return true if the account was successfully added, false otherwise
     */
    public boolean addCheckingAccount(String accountName, double overdraftLimit) {
        if (isMaxCheckingAccountsReached()) {
            return false;
        }
        
        if (isAccountNameTaken(accountName)) {
            return false;
        }
        
        CheckingAccount newAccount = createNewCheckingAccount(accountName, overdraftLimit);
        checkingAccounts.add(newAccount);
        saveAccounts(); // Save after adding a new account
        return true;
    }
    
    /**
     * Checks if the maximum number of checking accounts has been reached.
     * 
     * @return true if maximum reached, false otherwise
     */
    private boolean isMaxCheckingAccountsReached() {
        if (checkingAccounts.size() >= MAX_CHECKING_ACCOUNTS) {
            System.out.println("Maximum number of checking accounts (" + MAX_CHECKING_ACCOUNTS + ") reached.");
            return true;
        }
        return false;
    }
    
    /**
     * Creates a new checking account with the specified name and overdraft limit.
     * 
     * @param accountName The account name
     * @param overdraftLimit The overdraft limit
     * @return The new checking account
     */
    private CheckingAccount createNewCheckingAccount(String accountName, double overdraftLimit) {
        CheckingAccount newAccount = new CheckingAccount(accountName);
        newAccount.setOverdraftLimit(overdraftLimit);
        return newAccount;
    }
    
    /**
     * Adds a new checking account with the given name and default overdraft limit of 0.
     * 
     * @param accountName The name for the new checking account
     * @return true if the account was successfully added, false otherwise
     */
    public boolean addCheckingAccount(String accountName) {
        return addCheckingAccount(accountName, 0.0); // Default: no overdraft
    }
    
    /**
     * Adds a new savings account with the given name and interest rate.
     * 
     * @param accountName The name for the new savings account
     * @param interestRate The interest rate for the savings account
     * @return true if the account was successfully added, false otherwise
     */
    public boolean addSavingsAccount(String accountName, double interestRate) {
        if (isMaxSavingsAccountsReached()) {
            return false;
        }
        
        if (isAccountNameTaken(accountName)) {
            return false;
        }
        
        SavingsAccount newAccount = createNewSavingsAccount(accountName, interestRate);
        savingsAccounts.add(newAccount);
        saveAccounts(); // Save after adding a new account
        return true;
    }
    
    /**
     * Checks if the maximum number of savings accounts has been reached.
     * 
     * @return true if maximum reached, false otherwise
     */
    private boolean isMaxSavingsAccountsReached() {
        if (savingsAccounts.size() >= MAX_SAVINGS_ACCOUNTS) {
            System.out.println("Maximum number of savings accounts (" + MAX_SAVINGS_ACCOUNTS + ") reached.");
            return true;
        }
        return false;
    }
    
    /**
     * Creates a new savings account with the specified name and interest rate.
     * 
     * @param accountName The account name
     * @param interestRate The interest rate
     * @return The new savings account
     */
    private SavingsAccount createNewSavingsAccount(String accountName, double interestRate) {
        return new SavingsAccount(accountName, interestRate);
    }
    
    /**
     * Checks if an account name is already in use.
     * 
     * @param accountName The account name to check
     * @return true if the name is already taken, false otherwise
     */
    private boolean isAccountNameTaken(String accountName) {
        // Check if name exists in checking accounts
        for (CheckingAccount account : checkingAccounts) {
            if (account.getAccountName().equalsIgnoreCase(accountName)) {
                System.out.println("Account name '" + accountName + "' is already in use.");
                return true;
            }
        }
        
        // Check if name exists in savings accounts
        for (SavingsAccount account : savingsAccounts) {
            if (account.getAccountName().equalsIgnoreCase(accountName)) {
                System.out.println("Account name '" + accountName + "' is already in use.");
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Gets all checking accounts for the user.
     * 
     * @return List of checking accounts
     */
    public List<CheckingAccount> getCheckingAccounts() {
        return checkingAccounts;
    }
    
    /**
     * Gets all savings accounts for the user.
     * 
     * @return List of savings accounts
     */
    public List<SavingsAccount> getSavingsAccounts() {
        return savingsAccounts;
    }
    
    /**
     * Gets a specific account by its name.
     * 
     * @param accountName The name of the account to retrieve
     * @return The bank account, or null if not found
     */
    public BankAccount getAccountByName(String accountName) {
        // Check checking accounts
        for (CheckingAccount account : checkingAccounts) {
            if (account.getAccountName().equalsIgnoreCase(accountName)) {
                return account;
            }
        }
        
        // Check savings accounts
        for (SavingsAccount account : savingsAccounts) {
            if (account.getAccountName().equalsIgnoreCase(accountName)) {
                return account;
            }
        }
        
        return null; // Account not found
    }
    
    /**
     * Displays a list of all accounts and their balances.
     */
    public void listAllAccounts() {
        System.out.println("\n===== YOUR ACCOUNTS =====");
        
        if (checkingAccounts.isEmpty() && savingsAccounts.isEmpty()) {
            System.out.println("You don't have any accounts yet.");
            return;
        }
        
        displayCheckingAccounts();
        displaySavingsAccounts();
        
        System.out.println("==========================");
    }
    
    /**
     * Displays all checking accounts and their details.
     */
    private void displayCheckingAccounts() {
        if (!checkingAccounts.isEmpty()) {
            System.out.println("\nChecking Accounts:");
            for (CheckingAccount account : checkingAccounts) {
                displayAccountInfo(account);
            }
        }
    }
    
    /**
     * Displays all savings accounts and their details.
     */
    private void displaySavingsAccounts() {
        if (!savingsAccounts.isEmpty()) {
            System.out.println("\nSavings Accounts:");
            for (SavingsAccount account : savingsAccounts) {
                displayAccountInfo(account);
            }
        }
    }
    
    /**
     * Displays information for a specific account.
     * 
     * @param account The account to display
     */
    private void displayAccountInfo(BankAccount account) {
        String statusText = account.isFrozen() ? " [FROZEN]" : "";
        
        if (account instanceof CheckingAccount) {
            displayCheckingAccountInfo((CheckingAccount)account, statusText);
        } else if (account instanceof SavingsAccount) {
            displaySavingsAccountInfo((SavingsAccount)account, statusText);
        }
    }
    
    /**
     * Displays information for a checking account.
     * 
     * @param account The checking account
     * @param statusText Text indicating if account is frozen
     */
    private void displayCheckingAccountInfo(CheckingAccount account, String statusText) {
        String overdraftText = "";
        if (account.getOverdraftLimit() > 0) {
            overdraftText = String.format(" (Overdraft Limit: $%.2f, Rate: %.2f%%)", 
                                         account.getOverdraftLimit(), 
                                         account.getOverdraftInterestRate());
        }
        
        System.out.printf("- %s: $%.2f%s%s\n", 
                         account.getAccountName(), 
                         account.getBalance(),
                         overdraftText,
                         statusText);
    }
    
    /**
     * Displays information for a savings account.
     * 
     * @param account The savings account
     * @param statusText Text indicating if account is frozen
     */
    private void displaySavingsAccountInfo(SavingsAccount account, String statusText) {
        System.out.printf("- %s: $%.2f (Interest Rate: %.2f%%)%s\n", 
                account.getAccountName(), account.getBalance(), account.getInterestRate(), statusText);
    }
    
    /**
     * Saves all accounts to the filesystem.
     * 
     * @return true if saving was successful, false otherwise
     */
    public boolean saveAccounts() {
        createAccountsDirectory();
        
        try {
            // Save checking accounts
            saveAccountList(checkingAccounts, "checking");
            
            // Save savings accounts
            saveAccountList(savingsAccounts, "savings");
            
            return true;
        } catch (IOException exception) {
            System.err.println("Error saving accounts for " + username + ": " + exception.getMessage());
            return false;
        }
    }
    
    /**
     * Saves a list of accounts to a file.
     * 
     * @param accounts The list of accounts to save
     * @param accountType The type of accounts ("checking" or "savings")
     * @throws IOException If there's an error writing to the file
     */
    private <T extends BankAccount> void saveAccountList(List<T> accounts, String accountType) throws IOException {
        Path filePath = Paths.get(ACCOUNTS_DIRECTORY + username + "/" + accountType + ".txt");
        
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (T account : accounts) {
                writeAccountToFile(writer, account);
            }
        }
    }
    
    /**
     * Writes a single account's data to a file.
     * 
     * @param writer The writer to use
     * @param account The account to write
     * @throws IOException If there's an error writing to the file
     */
    private <T extends BankAccount> void writeAccountToFile(BufferedWriter writer, T account) throws IOException {
        StringBuilder builder = new StringBuilder();
        
        // Add basic account information
        builder.append(account.getAccountName()).append(",")
               .append(account.getBalance()).append(",")
               .append(account.isFrozen()).append(",")
               .append(account.getOverdraftLimit()).append(",")
               .append(account.getOverdraftInterestRate()).append(",")
               .append(account.getWithdrawalLimit());
        
        // Add savings-specific information
        if (account instanceof SavingsAccount) {
            SavingsAccount savingsAccount = (SavingsAccount) account;
            builder.append(",").append(savingsAccount.getInterestRate());
        }
        
        writer.write(builder.toString());
        writer.newLine();
    }
    
    /**
     * Loads all accounts for the user from the filesystem.
     * 
     * @return true if loading was successful, false otherwise
     */
    public boolean loadAccounts() {
        // Clear existing accounts
        checkingAccounts.clear();
        savingsAccounts.clear();
        
        try {
            // Load checking accounts
            Path checkingPath = Paths.get(ACCOUNTS_DIRECTORY + username + "/checking.txt");
            if (Files.exists(checkingPath)) {
                loadCheckingAccounts(checkingPath);
            }
            
            // Load savings accounts
            Path savingsPath = Paths.get(ACCOUNTS_DIRECTORY + username + "/savings.txt");
            if (Files.exists(savingsPath)) {
                loadSavingsAccounts(savingsPath);
            }
            
            return true;
        } catch (IOException exception) {
            System.err.println("Error loading accounts for " + username + ": " + exception.getMessage());
            return false;
        }
    }
    
    /**
     * Loads checking accounts from a file.
     * 
     * @param filePath The path to the checking accounts file
     * @throws IOException If there's an error reading from the file
     */
    private void loadCheckingAccounts(Path filePath) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 2) {
                    continue; // Skip invalid lines
                }
                
                CheckingAccount account = parseCheckingAccount(parts);
                if (account != null) {
                    checkingAccounts.add(account);
                }
            }
        }
    }
    
    /**
     * Parses a checking account from its string representation.
     * 
     * @param parts The parts of the account string
     * @return The parsed checking account, or null if invalid
     */
    private CheckingAccount parseCheckingAccount(String[] parts) {
        try {
            String accountName = parts[0];
            double balance = Double.parseDouble(parts[1]);
            
            CheckingAccount account = new CheckingAccount(accountName);
            account.deposit(balance); // Set the balance
            
            // Set overdraft settings if present in file (newer format)
            if (parts.length >= 6) {
                configureAccountFromParts(account, parts);
            }
            
            return account;
        } catch (NumberFormatException e) {
            System.err.println("Error parsing checking account: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Configures an account with settings from parsed file data.
     * 
     * @param account The account to configure
     * @param parts The parts of the account string
     */
    private void configureAccountFromParts(BankAccount account, String[] parts) {
        try {
            boolean isFrozen = Boolean.parseBoolean(parts[2]);
            double overdraftLimit = Double.parseDouble(parts[3]);
            double overdraftRate = Double.parseDouble(parts[4]);
            double withdrawalLimit = Double.parseDouble(parts[5]);
            
            if (isFrozen) account.freezeAccount();
            account.setOverdraftLimit(overdraftLimit);
            account.setOverdraftInterestRate(overdraftRate);
            account.setWithdrawalLimit(withdrawalLimit);
        } catch (NumberFormatException e) {
            System.err.println("Error configuring account: " + e.getMessage());
        }
    }
    
    /**
     * Loads savings accounts from a file.
     * 
     * @param filePath The path to the savings accounts file
     * @throws IOException If there's an error reading from the file
     */
    private void loadSavingsAccounts(Path filePath) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 3) {
                    continue; // Skip invalid lines
                }
                
                SavingsAccount account = parseSavingsAccount(parts);
                if (account != null) {
                    savingsAccounts.add(account);
                }
            }
        }
    }
    
    /**
     * Parses a savings account from its string representation.
     * 
     * @param parts The parts of the account string
     * @return The parsed savings account, or null if invalid
     */
    private SavingsAccount parseSavingsAccount(String[] parts) {
        try {
            String accountName = parts[0];
            double balance = Double.parseDouble(parts[1]);
            
            // In newer format, interest rate is at index 6
            double interestRate = parts.length >= 7 ? 
                                 Double.parseDouble(parts[6]) : 
                                 Double.parseDouble(parts[2]);
            
            SavingsAccount account = new SavingsAccount(accountName, interestRate);
            account.deposit(balance); // Set the balance
            
            // Set overdraft settings if present in file (newer format)
            if (parts.length >= 6) {
                configureAccountFromParts(account, parts);
            }
            
            return account;
        } catch (NumberFormatException e) {
            System.err.println("Error parsing savings account: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Applies interest to all savings accounts.
     */
    public void applyInterestToAllSavingsAccounts() {
        for (SavingsAccount account : savingsAccounts) {
            if (!account.isFrozen()) {
                account.applyInterest();
            }
        }
        saveAccounts(); // Save changes after applying interest
    }
    
    /**
     * Applies overdraft interest to all accounts that are in overdraft.
     * 
     * @return The total interest charged across all accounts
     */
    public double applyOverdraftInterestToAllAccounts() {
        double totalInterestCharged = 0.0;
        AccountStorage storage = new AccountStorage();
        
        // Apply interest to checking accounts
        totalInterestCharged += applyOverdraftInterestToAccounts(checkingAccounts, storage);
        
        // Apply interest to savings accounts (although they typically don't allow overdraft)
        totalInterestCharged += applyOverdraftInterestToAccounts(savingsAccounts, storage);
        
        saveAccounts(); // Save changes after applying interest
        return totalInterestCharged;
    }
    
    /**
     * Applies overdraft interest to a list of accounts.
     * 
     * @param accounts The list of accounts
     * @param storage The account storage for recording transactions
     * @return The total interest charged
     */
    private <T extends BankAccount> double applyOverdraftInterestToAccounts(List<T> accounts, AccountStorage storage) {
        double totalInterestCharged = 0.0;
        
        for (T account : accounts) {
            if (!account.isFrozen() && account.getBalance() < 0) {
                double interestAmount = account.applyOverdraftInterest();
                totalInterestCharged += interestAmount;
                
                // Record the transaction
                recordOverdraftInterestTransaction(storage, account, interestAmount);
            }
        }
        
        return totalInterestCharged;
    }
    
    /**
     * Records an overdraft interest transaction.
     * 
     * @param storage The account storage
     * @param account The account
     * @param interestAmount The interest amount
     */
    private void recordOverdraftInterestTransaction(AccountStorage storage, BankAccount account, double interestAmount) {
        try {
            storage.recordTransaction(username, account.getAccountName(),
                String.format("Overdraft Interest Charged: $%.2f", interestAmount));
        } catch (IOException e) {
            System.err.println("Error recording overdraft interest transaction: " + e.getMessage());
        }
    }
    
    /**
     * Freezes an account to prevent transactions.
     * 
     * @param accountName The name of the account to freeze
     * @return true if the account was successfully frozen, false otherwise
     */
    public boolean freezeAccount(String accountName) {
        BankAccount account = getAccountByName(accountName);
        if (account == null) {
            System.out.println("Account not found: " + accountName);
            return false;
        }
        
        account.freezeAccount();
        saveAccounts();
        return true;
    }
    
    /**
     * Unfreezes an account to allow transactions.
     * 
     * @param accountName The name of the account to unfreeze
     * @return true if the account was successfully unfrozen, false otherwise
     */
    public boolean unfreezeAccount(String accountName) {
        BankAccount account = getAccountByName(accountName);
        if (account == null) {
            System.out.println("Account not found: " + accountName);
            return false;
        }
        
        account.unfreezeAccount();
        saveAccounts();
        return true;
    }
    
    /**
     * Updates the overdraft limit for an account.
     * 
     * @param accountName The name of the account to update
     * @param overdraftLimit The new overdraft limit
     * @return true if the limit was successfully updated, false otherwise
     */
    public boolean setOverdraftLimit(String accountName, double overdraftLimit) {
        if (overdraftLimit < 0) {
            System.out.println("Overdraft limit cannot be negative.");
            return false;
        }
        
        BankAccount account = getAccountByName(accountName);
        if (account == null) {
            System.out.println("Account not found: " + accountName);
            return false;
        }
        
        account.setOverdraftLimit(overdraftLimit);
        saveAccounts();
        return true;
    }
    
    /**
     * Updates the overdraft interest rate for an account.
     * 
     * @param accountName The name of the account to update
     * @param interestRate The new interest rate
     * @return true if the rate was successfully updated, false otherwise
     */
    public boolean setOverdraftInterestRate(String accountName, double interestRate) {
        if (interestRate < 0) {
            System.out.println("Interest rate cannot be negative.");
            return false;
        }
        
        BankAccount account = getAccountByName(accountName);
        if (account == null) {
            System.out.println("Account not found: " + accountName);
            return false;
        }
        
        account.setOverdraftInterestRate(interestRate);
        saveAccounts();
        return true;
    }
    
    /**
     * Sets the transfer limit for an account.
     * 
     * @param accountName The name of the account
     * @param transferLimit The new transfer limit
     * @return true if successful, false if account not found
     */
    public boolean setTransferLimit(String accountName, double transferLimit) {
        BankAccount account = getAccountByName(accountName);
        if (account != null) {
            account.setTransferLimit(transferLimit);
            return true;
        }
        return false;
    }
    
    /**
     * Closes an account if it has a zero or positive balance.
     * 
     * @param accountName The name of the account to close
     * @return true if the account was successfully closed, false otherwise
     */
    public boolean closeAccount(String accountName) {
        BankAccount account = getAccountByName(accountName);
        if (account == null) {
            System.out.println("Account not found: " + accountName);
            return false;
        }
        
        if (!account.canClose()) {
            System.out.println("Cannot close account: balance must be zero or positive.");
            return false;
        }
        
        return removeAccount(accountName);
    }
    
    /**
     * Migrates an existing single account to the multi-account system.
     * This is useful for backward compatibility.
     * 
     * @param existingAccount The existing single account to migrate
     * @return true if migration was successful, false otherwise
     */
    public boolean migrateExistingAccount(BankAccount existingAccount) {
        if (existingAccount == null) {
            return false;
        }
        
        String accountName = generateMigratedAccountName(existingAccount);
        
        if (existingAccount instanceof CheckingAccount) {
            return migrateCheckingAccount((CheckingAccount)existingAccount, accountName);
        } else if (existingAccount instanceof SavingsAccount) {
            return migrateSavingsAccount((SavingsAccount)existingAccount, accountName);
        }
        
        return false;
    }
    
    /**
     * Generates a name for a migrated account.
     * 
     * @param existingAccount The existing account
     * @return The generated name
     */
    private String generateMigratedAccountName(BankAccount existingAccount) {
        // Default name for migrated accounts
        String defaultName = "Primary " + existingAccount.getAccountType().substring(0, 1).toUpperCase() + 
                           existingAccount.getAccountType().substring(1);
        
        // If name is taken, append a number
        String accountName = defaultName;
        int suffix = 1;
        while (isAccountNameTaken(accountName)) {
            accountName = defaultName + " " + suffix;
            suffix++;
        }
        
        return accountName;
    }
    
    /**
     * Migrates a checking account.
     * 
     * @param oldAccount The old checking account
     * @param accountName The new account name
     * @return true if successful, false otherwise
     */
    private boolean migrateCheckingAccount(CheckingAccount oldAccount, String accountName) {
        if (checkingAccounts.size() >= MAX_CHECKING_ACCOUNTS) {
            System.out.println("Cannot migrate existing checking account: maximum number reached.");
            return false;
        }
        
        CheckingAccount newAccount = new CheckingAccount(accountName);
        newAccount.deposit(oldAccount.getBalance());
        newAccount.setOverdraftLimit(oldAccount.getOverdraftLimit());
        newAccount.setOverdraftInterestRate(oldAccount.getOverdraftInterestRate());
        checkingAccounts.add(newAccount);
        
        saveAccounts();
        return true;
    }
    
    /**
     * Migrates a savings account.
     * 
     * @param oldAccount The old savings account
     * @param accountName The new account name
     * @return true if successful, false otherwise
     */
    private boolean migrateSavingsAccount(SavingsAccount oldAccount, String accountName) {
        if (savingsAccounts.size() >= MAX_SAVINGS_ACCOUNTS) {
            System.out.println("Cannot migrate existing savings account: maximum number reached.");
            return false;
        }
        
        SavingsAccount newAccount = new SavingsAccount(accountName, oldAccount.getInterestRate());
        newAccount.deposit(oldAccount.getBalance());
        newAccount.setOverdraftLimit(oldAccount.getOverdraftLimit());
        newAccount.setOverdraftInterestRate(oldAccount.getOverdraftInterestRate());
        savingsAccounts.add(newAccount);
        
        saveAccounts();
        return true;
    }
    
    /**
     * Removes an account regardless of its balance.
     * 
     * @param accountName The name of the account to remove
     * @return true if the account was successfully removed, false otherwise
     */
    public boolean removeAccount(String accountName) {
        for (CheckingAccount account : checkingAccounts) {
            if (account.getAccountName().equalsIgnoreCase(accountName)) {
                checkingAccounts.remove(account);
                saveAccounts();
                return true;
            }
        }

        for (SavingsAccount account : savingsAccounts) {
            if (account.getAccountName().equalsIgnoreCase(accountName)){
                savingsAccounts.remove(account);
                saveAccounts();
                return true;
            }
        }

        return false; // Account not found
    }
}