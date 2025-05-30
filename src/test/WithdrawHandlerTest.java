package test;

import static org.junit.Assert.*;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import bankapp.BankAccount;
import bankapp.CheckingAccount;
import bankapp.WithdrawHandler;

public class WithdrawHandlerTest {

    @Test
    public void testSuccessfulWithdrawal() {
        // Redirect System.out to capture output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        
        // Simulate input: withdraw 50.0
        String input = "50.0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        BankAccount account = new CheckingAccount("Checking");
        account.deposit(100.0); // Preload funds

        WithdrawHandler.handle(account, scanner);

        // Verify balance was updated correctly
        assertEquals(50.0, account.getBalance(), 0.001);
        
        // Verify output message 
        String output = outContent.toString();
        assertTrue(output.contains("Withdrawal successful"));
        
        // Reset System.out
        System.setOut(originalOut);
    }

    @Test
    public void testFailedWithdrawalDueToInsufficientFunds() {
        // Redirect System.out to capture output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        
        // Simulate input: withdraw 200.0
        String input = "200.0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        BankAccount account = new CheckingAccount("Checking");
        account.deposit(100.0); // Not enough funds
        
        WithdrawHandler.handle(account, scanner);
        
        // Balance should remain unchanged
        assertEquals(100.0, account.getBalance(), 0.001);
        
        // Verify output message
        String output = outContent.toString();
        assertTrue(output.contains("Insufficient funds"));
        
        // Reset System.out
        System.setOut(originalOut);
    }
    
    @Test
    public void testWithdrawalWithOverdraft() {
        // Redirect System.out
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        
        // Simulate withdrawal amount input
        String input = "150.0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        // Create account with overdraft protection
        CheckingAccount account = new CheckingAccount("Overdraft Checking");
        account.deposit(100.0);
        account.setOverdraftLimit(100.0);
        
        // Process withdrawal
        WithdrawHandler.handle(account, scanner);
        
        // Verify balance went negative
        assertEquals(-50.0, account.getBalance(), 0.001);
        
        // Verify success message
        String output = outContent.toString();
        assertTrue(output.contains("Withdrawal successful"));
        
        // Reset System.out
        System.setOut(originalOut);
    }
    
    @Test
    public void testFailedWithdrawalWithFrozenAccount() {
        // Redirect System.out
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        
        // Simulate withdrawal amount input
        String input = "50.0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        // Create and freeze account
        BankAccount account = new CheckingAccount("Frozen Account");
        account.deposit(100.0);
        account.freezeAccount();
        
        // Process withdrawal attempt
        WithdrawHandler.handle(account, scanner);
        
        // Verify balance remains unchanged
        assertEquals(100.0, account.getBalance(), 0.001);
        
        // Reset System.out
        System.setOut(originalOut);
    }
}