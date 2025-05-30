package test;

import bankapp.*;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class TransferHandlerTest {

    @Test
    void testTransferBetweenAccounts() {
        // Setup accounts
        AccountManager manager = new AccountManager("testuser");
        manager.addSavingsAccount("FromAccount", 1.0);
        manager.addSavingsAccount("ToAccount", 1.0);

        BankAccount fromAccount = manager.getAccountByName("FromAccount");
        BankAccount toAccount = manager.getAccountByName("ToAccount");

        fromAccount.deposit(200.00);

        // Simulate user input:
        // 1) Account to transfer to: "ToAccount"
        // 2) Amount to transfer: "75.50"
        String input = "ToAccount\n75.50\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(in);

        // Call the transfer handler
        TransferHandler.handleTransfer(manager, fromAccount, scanner);

        // Assert balances
        assertEquals(124.50, fromAccount.getBalance(), 0.001);  // 200 - 75.5
        assertEquals(75.50, toAccount.getBalance(), 0.001);
    }
}
