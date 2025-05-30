package test;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.util.Scanner;
import bankapp.AccountManager;
import bankapp.BankAccount;
import bankapp.CheckingAccount;
import bankapp.PromptHandler;
import static org.junit.jupiter.api.Assertions.*;

class PromptHandlerTest {
    
    // Original tests
	@Test
	void testOpenCheckingAccount() {
	    // Include additional input for overdraft configuration
	    // Format: account type, account name, overdraft choice (no)
	    String input = String.join("\n",
	            "1",           // Choose checking account
	            "MyChecking",  // Account name
	            "no"           // No overdraft protection
	        ) + "\n";
	    Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
	    BankAccount account = PromptHandler.openAccount(scanner);
	    assertNotNull(account);
	    assertEquals("checking", account.getAccountType());
	    assertEquals(0.0, account.getBalance(), 0.001);
	}
    
	@Test
	void testOpenAccountWithInvalidInputCreatesDefault() {
	    // Include response to overdraft question
	    String input = "no\nno\n"; // Invalid account type + no overdraft
	    Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
	    BankAccount account = PromptHandler.openAccount(scanner);
	    assertNotNull(account);
	    assertTrue(account instanceof CheckingAccount);
	    assertEquals("Default Checking", account.getAccountName());
	}
    
    @Test
    void testCommandLoopDepositWithdrawDone() {
        // Simulates: deposit 100 → withdraw 30 → done
        String input = String.join("\n",
            "2", "100",
            "3", "30",
            "0"
        ) + "\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        bankapp.BankAccount account = new CheckingAccount("Checking");
        bankapp.AccountManager manager = new AccountManager("mike");
        PromptHandler.commandLoop(manager, account, scanner);
        assertEquals(70.0, account.getBalance(), 0.001);
    }
    
    // New tests for handleDeposit and handleWithdrawal
    @Test
    void testHandleDepositAddsMoneyToAccount() {
        // Setup
        AccountManager accountManager = new AccountManager("testuser");
        BankAccount account = new CheckingAccount("TestAccount");
        
        // Initial balance should be zero
        assertEquals(0.0, account.getBalance(), 0.001);
        
        // Simulate deposit input (100.50)
        String input = "100.50\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        // Call the method
        PromptHandler.handleDeposit(accountManager, account, scanner);
        
        // Verify deposit was processed
        assertEquals(100.50, account.getBalance(), 0.001);
    }
    
    @Test
    void testHandleWithdrawalRemovesMoneyFromAccount() {
        // Setup
        AccountManager accountManager = new AccountManager("testuser");
        BankAccount account = new CheckingAccount("TestAccount");
        
        // Add initial balance
        account.deposit(200.00);
        assertEquals(200.00, account.getBalance(), 0.001);
        
        // Simulate withdrawal input (75.25)
        String input = "75.25\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        // Call the method
        PromptHandler.handleWithdrawal(accountManager, account, scanner);
        
        // Verify withdrawal was processed
        assertEquals(124.75, account.getBalance(), 0.001);
    }
    
    @Test
    void testHandleDepositWithInvalidAmount() {
        // Setup
        AccountManager accountManager = new AccountManager("testuser");
        BankAccount account = new CheckingAccount("TestAccount");
        
        // Initial balance
        account.deposit(100.00);
        
        // Simulate invalid deposit input (abc)
        String input = "abc\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        // Call the method
        PromptHandler.handleDeposit(accountManager, account, scanner);
        
        // Verify balance unchanged
        assertEquals(100.00, account.getBalance(), 0.001);
    }
    
    @Test
    void testHandleDepositWithNegativeAmount() {
        // Setup
        AccountManager accountManager = new AccountManager("testuser");
        BankAccount account = new CheckingAccount("TestAccount");
        
        // Initial balance
        account.deposit(100.00);
        
        // Simulate negative deposit input (-50.00)
        String input = "-50.00\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        // Call the method
        PromptHandler.handleDeposit(accountManager, account, scanner);
        
        // Verify balance unchanged
        assertEquals(100.00, account.getBalance(), 0.001);
    }
    
    @Test
    void testHandleWithdrawalWithInsufficientFunds() {
        // Setup
        AccountManager accountManager = new AccountManager("testuser");
        BankAccount account = new CheckingAccount("TestAccount");
        
        // Initial balance
        account.deposit(50.00);
        
        // Simulate withdrawal more than balance (100.00)
        String input = "100.00\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        // Call the method
        PromptHandler.handleWithdrawal(accountManager, account, scanner);
        
        // Verify balance unchanged (withdrawal should fail)
        assertEquals(50.00, account.getBalance(), 0.001);
    }
    
    @Test
    void testHandleWithdrawalWithOverdraft() {
        // Setup
        AccountManager accountManager = new AccountManager("testuser");
        BankAccount account = new CheckingAccount("TestAccount");
        account.setOverdraftLimit(100.00);
        
        // Initial balance
        account.deposit(50.00);
        
        // Simulate withdrawal with overdraft (125.00)
        String input = "125.00\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        // Call the method
        PromptHandler.handleWithdrawal(accountManager, account, scanner);
        
        // Verify overdraft processed correctly
        assertEquals(-75.00, account.getBalance(), 0.001);
    }
    
    @Test
    void testHandleWithdrawalExceedingWithdrawalLimit() {
        // Setup
        AccountManager accountManager = new AccountManager("testuser");
        BankAccount account = new CheckingAccount("TestAccount");
        account.setWithdrawalLimit(1000.00);
        
        // Initial balance (large amount)
        account.deposit(5000.00);
        
        // Simulate withdrawal exceeding limit (2000.00)
        String input = "2000.00\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        // Call the method
        PromptHandler.handleWithdrawal(accountManager, account, scanner);
        
        // Verify balance unchanged (withdrawal should fail due to limit)
        assertEquals(5000.00, account.getBalance(), 0.001);
    }
    
    @Test
    void testFrozenAccountCannotDeposit() {
        // Setup
        AccountManager accountManager = new AccountManager("testuser");
        BankAccount account = new CheckingAccount("TestAccount");
        account.freezeAccount();
        
        // Initial balance
        account.deposit(100.00); // This won't work because account is frozen
        
        // Simulate deposit
        String input = "50.00\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        // Call the method
        PromptHandler.handleDeposit(accountManager, account, scanner);
        
        // Verify balance unchanged (account is frozen)
        assertEquals(0.0, account.getBalance(), 0.001);
    }
    
    @Test
    void testFrozenAccountCannotWithdraw() {
        // Setup
        AccountManager accountManager = new AccountManager("testuser");
        BankAccount account = new CheckingAccount("TestAccount");
        
        // Add initial balance
        account.deposit(100.00);
        
        // Freeze account
        account.freezeAccount();
        
        // Simulate withdrawal
        String input = "50.00\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        // Call the method
        PromptHandler.handleWithdrawal(accountManager, account, scanner);
        
        // Verify balance unchanged (account is frozen)
        assertEquals(100.00, account.getBalance(), 0.001);
    }
}