package test;

import bankapp.AccountManager;
import bankapp.CheckingAccount;
import bankapp.SavingsAccount;
import bankapp.BankAccount;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountCreationTest {

    @Test
    void testCreateCheckingAccount() {
        AccountManager manager = new AccountManager("testuser");
        boolean created = manager.addCheckingAccount("MyChecking");

        assertTrue(created, "Checking account should be created successfully.");
        BankAccount account = manager.getAccountByName("MyChecking");
        assertNotNull(account, "Checking account should exist.");
        assertTrue(account instanceof CheckingAccount, "Account should be a CheckingAccount.");
        assertEquals("MyChecking", account.getAccountName());
        assertEquals(0.0, account.getBalance(), 0.001);
    }

    @Test
    void testCreateSavingsAccount() {
        AccountManager manager = new AccountManager("testuser");
        boolean created = manager.addSavingsAccount("MySavings", 2.5);

        assertTrue(created, "Savings account should be created successfully.");
        BankAccount account = manager.getAccountByName("MySavings");
        assertNotNull(account, "Savings account should exist.");
        assertTrue(account instanceof SavingsAccount, "Account should be a SavingsAccount.");
        assertEquals("MySavings", account.getAccountName());
        assertEquals(0.0, account.getBalance(), 0.001);
        assertEquals(2.5, ((SavingsAccount) account).getInterestRate(), 0.001);
    }
}
