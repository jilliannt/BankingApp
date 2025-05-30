package test;

import static org.junit.Assert.*;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import bankapp.BankAccount;
import bankapp.CheckingAccount;
import bankapp.DepositHandler;

public class DepositHandlerTest {

    @Test
    public void testHandleDeposit() {
        // Redirect System.out to capture output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        
        // Simulate user input of "150.75"
        String input = "150.75\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(inContent);

        // Create account and handle deposit
        BankAccount account = new CheckingAccount("Test Account");
        DepositHandler.handle(account, scanner);

        // Verify balance was updated correctly
        assertEquals(150.75, account.getBalance(), 0.001);
        
        // Optionally verify output message contains expected text
        String output = outContent.toString();
        assertTrue(output.contains("Deposit successful"));
        
        // Reset System.out
        System.setOut(originalOut);
    }
    
    @Test
    public void testHandleDepositWithFrozenAccount() {
        // This test only applies if you've implemented the account freezing feature
        
        // Redirect System.out to capture output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        
        // Simulate user input
        String input = "100.00\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(inContent);

        // Create account, freeze it, and attempt deposit
        BankAccount account = new CheckingAccount("Frozen Account");
        account.freezeAccount();
        DepositHandler.handle(account, scanner);

        // Verify balance remains unchanged
        assertEquals(0.0, account.getBalance(), 0.001);
        
        // Reset System.out
        System.setOut(originalOut);
    }
}