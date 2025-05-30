package test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import bankapp.CheckingAccount;
import bankapp.BankAccount;

public class OverDraftTest {
    
    private CheckingAccount account;
    
    @Before
    public void setUp() {
        account = new CheckingAccount("Test Checking");
        account.deposit(100.0); // Starting balance of $100
    }
    
    @Test
    public void testWithdrawalWithoutOverdraft() {
        // With no overdraft limit set, withdrawal beyond balance should fail
        assertFalse(account.withdraw(150.0));
        assertEquals(100.0, account.getBalance(), 0.001);
    }
    
    @Test
    public void testWithdrawalWithOverdraft() {
        // Set overdraft limit
        account.setOverdraftLimit(200.0);
        
        // Withdraw more than balance but within overdraft limit
        assertTrue(account.withdraw(150.0));
        assertEquals(-50.0, account.getBalance(), 0.001);
        
        // Withdraw more than balance + overdraft limit
        assertFalse(account.withdraw(160.0));
        assertEquals(-50.0, account.getBalance(), 0.001);
    }
    
    @Test
    public void testOverdraftInterest() {
        // Set up account with overdraft
        account.setOverdraftLimit(100.0);
        account.setOverdraftInterestRate(10.0); // 10% interest
        
        // Put account into overdraft
        account.withdraw(150.0);
        assertEquals(-50.0, account.getBalance(), 0.001);
        
        // Apply overdraft interest
        double interestCharged = account.applyOverdraftInterest();
        assertEquals(5.0, interestCharged, 0.001); // 10% of $50 = $5
        assertEquals(-55.0, account.getBalance(), 0.001);
    }
    
    @Test
    public void testFreezeAccount() {
        account.freezeAccount();
        assertTrue(account.isFrozen());
        
        // Deposits and withdrawals should fail on frozen account
        assertFalse(account.withdraw(50.0));
        assertEquals(100.0, account.getBalance(), 0.001);
        
        account.deposit(50.0);
        assertEquals(100.0, account.getBalance(), 0.001);
        
        // Unfreeze and try operations again
        account.unfreezeAccount();
        assertFalse(account.isFrozen());
        
        assertTrue(account.withdraw(50.0));
        assertEquals(50.0, account.getBalance(), 0.001);
    }
    
    @Test
    public void testCanClose() {
        // Account with positive balance can be closed
        assertTrue(account.canClose());
        
        // Put account into overdraft
        account.setOverdraftLimit(100.0);
        account.withdraw(150.0);
        assertEquals(-50.0, account.getBalance(), 0.001);
        
        // Account with negative balance cannot be closed
        assertFalse(account.canClose());
        
        // Bring account back to positive
        account.deposit(100.0);
        assertEquals(50.0, account.getBalance(), 0.001);
        
        // Now it can be closed again
        assertTrue(account.canClose());
    }
    
    @Test
    public void testWithdrawalLimits() {
        // Set withdrawal limit to $50
        account.setWithdrawalLimit(50.0);
        
        // Should fail even though balance covers it
        assertFalse(account.withdraw(60.0));
        assertEquals(100.0, account.getBalance(), 0.001);
        
        // Should succeed
        assertTrue(account.withdraw(50.0));
        assertEquals(50.0, account.getBalance(), 0.001);
    }
}