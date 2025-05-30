package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import bankapp.BankAccount;
import bankapp.CheckingAccount;
import bankapp.SavingsAccount;

class BankAccountTest {

    private static final String TEST_ACCOUNT_NAME = "Test Account";
    
    @Test
    void testCheckingAccountCreation() {
        BankAccount account = new CheckingAccount(TEST_ACCOUNT_NAME);
        assertEquals(0.0, account.getBalance());
        assertEquals(TEST_ACCOUNT_NAME, account.getAccountName());
        assertEquals("checking", account.getAccountType());
    }
    
    @Test
    void testSavingsAccountCreation() {
        double interestRate = 2.5;
        BankAccount account = new SavingsAccount(TEST_ACCOUNT_NAME, interestRate);
        assertEquals(0.0, account.getBalance());
        assertEquals(TEST_ACCOUNT_NAME, account.getAccountName());
        assertEquals("savings", account.getAccountType());
    }
    
    @Test
    void testDeposit() {
        BankAccount account = new CheckingAccount(TEST_ACCOUNT_NAME);
        account.deposit(100.0);
        assertEquals(100.0, account.getBalance());
        
        account.deposit(50.0);
        assertEquals(150.0, account.getBalance());
    }
    
    @Test
    void testWithdrawalSufficientFunds() {
        BankAccount account = new CheckingAccount(TEST_ACCOUNT_NAME);
        account.deposit(100.0);
        
        boolean result = account.withdraw(50.0);
        
        assertTrue(result);
        assertEquals(50.0, account.getBalance());
    }
    
    @Test
    void testWithdrawalInsufficientFunds() {
        BankAccount account = new CheckingAccount(TEST_ACCOUNT_NAME);
        account.deposit(100.0);
        
        boolean result = account.withdraw(150.0);
        
        assertFalse(result);
        assertEquals(100.0, account.getBalance()); // Balance should remain unchanged
    }
    
    @Test
    void testWithdrawalExceedsLimit() {
        BankAccount account = new CheckingAccount("Test");
        account.deposit(1000.0);
        account.setWithdrawalLimit(200.0); // limit is $200 per withdrawal

        boolean result = account.withdraw(300.0); // exceeds the limit

        assertFalse(result, "Withdrawal should fail because it exceeds the limit");
        assertEquals(1000.0, account.getBalance(), 0.001, "Balance should remain unchanged");
    }

    @Test
    void testWithdrawalWithinLimitSucceeds() {
        BankAccount account = new CheckingAccount("Test");
        account.deposit(500.0);
        account.setWithdrawalLimit(300.0);

        boolean result = account.withdraw(200.0); // within the limit

        assertTrue(result, "Withdrawal should succeed within the limit");
        assertEquals(300.0, account.getBalance(), 0.001, "Balance should be reduced correctly");
    }
    
    @Test
    void testGetAccountName() {
        BankAccount checkingAccount = new CheckingAccount("Personal Checking");
        BankAccount savingsAccount = new SavingsAccount("Vacation Fund", 1.5);
        
        assertEquals("Personal Checking", checkingAccount.getAccountName());
        assertEquals("Vacation Fund", savingsAccount.getAccountName());
    }
    
    @Test
    void testMultipleOperations() {
        BankAccount account = new CheckingAccount(TEST_ACCOUNT_NAME);
        
        account.deposit(100.0);
        assertEquals(100.0, account.getBalance());
        
        account.deposit(50.0);
        assertEquals(150.0, account.getBalance());
        
        boolean result1 = account.withdraw(30.0);
        assertTrue(result1);
        assertEquals(120.0, account.getBalance());
        
        boolean result2 = account.withdraw(200.0);
        assertFalse(result2);
        assertEquals(120.0, account.getBalance());
    }
    
    // Add a test for the overdraft functionality
    @Test
    void testOverdraftWithdrawal() {
        BankAccount account = new CheckingAccount("Overdraft Test");
        account.deposit(100.0);
        account.setOverdraftLimit(50.0);  // Allow $50 of overdraft
        
        // Withdraw more than balance but within overdraft limit
        boolean result = account.withdraw(120.0);
        
        assertTrue(result, "Withdrawal should succeed within overdraft limit");
        assertEquals(-20.0, account.getBalance(), 0.001, "Balance should be negative");
        assertEquals(20.0, account.getOverdraftAmount(), 0.001, "Overdraft amount should be tracked");
    }
    
    // Add a test for the canClose functionality
    @Test
    void testCanClose() {
        BankAccount account = new CheckingAccount("Close Test");
        account.deposit(100.0);
        
        assertTrue(account.canClose(), "Account with positive balance should be closeable");
        
        account.withdraw(100.0); // Balance is now 0
        assertTrue(account.canClose(), "Account with zero balance should be closeable");
        
        // Set up overdraft capability and withdraw
        account.setOverdraftLimit(50.0);
        account.withdraw(20.0); // Balance is now -20
        
        assertFalse(account.canClose(), "Account with negative balance should not be closeable");
    }
    
    // Add a test for the freeze/unfreeze functionality
    @Test
    void testFreezeUnfreeze() {
        BankAccount account = new CheckingAccount("Freeze Test");
        account.deposit(100.0);
        
        assertFalse(account.isFrozen(), "Account should not be frozen by default");
        
        account.freezeAccount();
        assertTrue(account.isFrozen(), "Account should be frozen after freezeAccount call");
        
        // Test that operations are blocked when account is frozen
        account.deposit(50.0);
        assertEquals(100.0, account.getBalance(), "Deposit should be blocked on frozen account");
        
        boolean withdrawResult = account.withdraw(50.0);
        assertFalse(withdrawResult, "Withdrawal should fail on frozen account");
        assertEquals(100.0, account.getBalance(), "Withdrawal should be blocked on frozen account");
        
        account.unfreezeAccount();
        assertFalse(account.isFrozen(), "Account should not be frozen after unfreezeAccount call");
        
        // Now operations should work
        account.deposit(50.0);
        assertEquals(150.0, account.getBalance(), "Deposit should work after unfreezing");
    }
}