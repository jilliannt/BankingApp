package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bankapp.AccountManager;
import bankapp.BankAccount;
import bankapp.SavingsAccount;


class AccountManagerTest {
    
    private AccountManager accountManager;
    private final String TEST_USERNAME = "testuser";
    
    @BeforeEach
    void setUp() {
        accountManager = new AccountManager(TEST_USERNAME);
    }
    
    @Test
    void testAddCheckingAccount() {
        assertTrue(accountManager.addCheckingAccount("Primary Checking"));
        assertEquals(1, accountManager.getCheckingAccounts().size());
        
        // Check if we can get the account by name
        BankAccount account = accountManager.getAccountByName("Primary Checking");
        assertNotNull(account);
        assertEquals("Primary Checking", account.getAccountName());
        assertEquals("checking", account.getAccountType());
    }
    
    @Test
    void testAddSavingsAccount() {
        assertTrue(accountManager.addSavingsAccount("Vacation Savings", 2.5));
        assertEquals(1, accountManager.getSavingsAccounts().size());
        
        // Check if we can get the account by name
        BankAccount account = accountManager.getAccountByName("Vacation Savings");
        assertNotNull(account);
        assertEquals("Vacation Savings", account.getAccountName());
        assertEquals("savings", account.getAccountType());
        assertEquals(2.5, ((SavingsAccount)account).getInterestRate());
    }
    
    @Test
    void testAddDuplicateAccountName() {
        assertTrue(accountManager.addCheckingAccount("Primary Account"));
        assertFalse(accountManager.addSavingsAccount("Primary Account", 1.5));
        assertEquals(1, accountManager.getCheckingAccounts().size());
        assertEquals(0, accountManager.getSavingsAccounts().size());
    }
    
    @Test
    void testAccountLimits() {
        // Add max checking accounts
        assertTrue(accountManager.addCheckingAccount("Checking 1"));
        assertTrue(accountManager.addCheckingAccount("Checking 2"));
        assertFalse(accountManager.addCheckingAccount("Checking 3")); // Should fail - limit reached
        
        // Add max savings accounts
        assertTrue(accountManager.addSavingsAccount("Savings 1", 1.0));
        assertTrue(accountManager.addSavingsAccount("Savings 2", 2.0));
        assertTrue(accountManager.addSavingsAccount("Savings 3", 3.0));
        assertFalse(accountManager.addSavingsAccount("Savings 4", 4.0)); // Should fail - limit reached
        
        assertEquals(2, accountManager.getCheckingAccounts().size());
        assertEquals(3, accountManager.getSavingsAccounts().size());
    }
    
    @Test
    void testGetAccountByName() {
        accountManager.addCheckingAccount("Daily Checking");
        accountManager.addSavingsAccount("Emergency Fund", 1.75);
        
        BankAccount checking = accountManager.getAccountByName("Daily Checking");
        BankAccount savings = accountManager.getAccountByName("Emergency Fund");
        BankAccount nonExistent = accountManager.getAccountByName("Non-Existent Account");
        
        assertNotNull(checking);
        assertNotNull(savings);
        assertNull(nonExistent);
        
        assertEquals("Daily Checking", checking.getAccountName());
        assertEquals("Emergency Fund", savings.getAccountName());
    }
    
    @Test
    void testSaveAndLoadAccounts() {
        // Create and modify accounts
        accountManager.addCheckingAccount("Test Checking");
        accountManager.addSavingsAccount("Test Savings", 2.0);
        
        BankAccount checking = accountManager.getAccountByName("Test Checking");
        BankAccount savings = accountManager.getAccountByName("Test Savings");
        
        checking.deposit(100.0);
        savings.deposit(200.0);
        
        // Save accounts
        assertTrue(accountManager.saveAccounts());
        
        // Create a new account manager instance to load from files
        AccountManager loadedManager = new AccountManager(TEST_USERNAME);
        assertTrue(loadedManager.loadAccounts());
        
        // Verify loaded accounts
        assertEquals(1, loadedManager.getCheckingAccounts().size());
        assertEquals(1, loadedManager.getSavingsAccounts().size());
        
        BankAccount loadedChecking = loadedManager.getAccountByName("Test Checking");
        BankAccount loadedSavings = loadedManager.getAccountByName("Test Savings");
        
        assertNotNull(loadedChecking);
        assertNotNull(loadedSavings);
        
        assertEquals(100.0, loadedChecking.getBalance());
        assertEquals(200.0, loadedSavings.getBalance());
        assertEquals(2.0, ((SavingsAccount)loadedSavings).getInterestRate());
    }
}