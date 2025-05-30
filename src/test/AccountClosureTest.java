package test;

import bankapp.*;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;
import bankapp.PromptHandler;

import static org.junit.jupiter.api.Assertions.*;

public class AccountClosureTest {

    @Test
    void testCloseAccountSuccessWithConfirmation() {
        // Setup accounts
        AccountManager manager = new AccountManager("testuser");
        BankAccount account = new CheckingAccount("TestAccount");
        manager.getCheckingAccounts().add((CheckingAccount) account);

        String input = "yes\n"; // simulate user confirming
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        boolean result = PromptHandler.handleCloseAccount(manager, account, scanner);

        assertTrue(result, "Account should be closed successfully");
        assertNull(manager.getAccountByName("TestAccount"), "Account should no longer exist");
    }

    @Test
    void testCloseAccountFailsDueToNonZeroBalance() {
        // Setup account with a non-closable balance
        AccountManager manager = new AccountManager("testuser");
        BankAccount account = new CheckingAccount("TestAccount");
        
        // First enable overdraft on the account
        account.setOverdraftLimit(200.0);
        
        // Now deposit and then withdraw more to create a negative balance
        account.deposit(100.0);
        account.withdraw(150.0); // This will create a negative balance of -50.0
        
        // Verify the account has a negative balance
        assertTrue(account.getBalance() < 0, "Account should have a negative balance for this test");
        
        manager.getCheckingAccounts().add((CheckingAccount) account);

        String input = "yes\n"; // User confirms, but balance is negative
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        boolean result = PromptHandler.handleCloseAccount(manager, account, scanner);

        assertFalse(result, "Account should not be closed due to negative balance");
        assertNotNull(manager.getAccountByName("TestAccount"), "Account should still exist");
    }

    @Test
    void testCloseAccountCancelledByUser() {
        AccountManager manager = new AccountManager("testuser");
        BankAccount account = new CheckingAccount("TestAccount");
        manager.getCheckingAccounts().add((CheckingAccount) account);

        String input = "no\n"; // simulate user saying "no"
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        boolean result = PromptHandler.handleCloseAccount(manager, account, scanner);

        assertFalse(result, "Account closure should be cancelled by user");
        assertNotNull(manager.getAccountByName("TestAccount"), "Account should still exist");
    }
}