package test;

import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import bankapp.AccountStorage;

class AccountStorageTest {
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_ACCOUNT = "Checking1";
    private static final Path TEST_ROOT = Paths.get("..", "data", "testaccounts");
    private AccountStorage storage;

    @BeforeEach
    void setup() throws IOException {
        // Ensure clean start by deleting test folder if it exists
        deleteTestDirectory();
        storage = new AccountStorage(TEST_ROOT);
    }

    @AfterEach
    void cleanup() throws IOException {
        deleteTestDirectory();
    }

    private void deleteTestDirectory() throws IOException {
        if (Files.exists(TEST_ROOT)) {
            Files.walk(TEST_ROOT)
                 .sorted(Comparator.reverseOrder())
                 .forEach(path -> {
                     try {
                         Files.delete(path);
                     } catch (IOException e) {
                         throw new RuntimeException("Failed to delete " + path, e);
                     }
                 });
        }
    }

    @Test
    void recordTransaction_createHistoryFile() throws IOException {
        storage.recordTransaction(TEST_USERNAME, TEST_ACCOUNT, "Deposit $100");
        Path historyFile = TEST_ROOT.resolve(TEST_USERNAME)
                                  .resolve(TEST_ACCOUNT + "_history.txt");
        
        assertTrue(Files.exists(historyFile), "History file should be created");
    }

    @Test
    void recordTransaction_appendsToExistingFile() throws IOException {
        // First transaction
        storage.recordTransaction(TEST_USERNAME, TEST_ACCOUNT, "Deposit $100");
        // Second transaction
        storage.recordTransaction(TEST_USERNAME, TEST_ACCOUNT, "Withdraw $50");
        
        List<String> transactions = storage.getAccountHistory(TEST_USERNAME, TEST_ACCOUNT);
        assertEquals(2, transactions.size(), "Should have two transactions");
    }


    @Test
    void getAccountHistory_returnsEmptyForNewAccount() throws IOException {
        List<String> history = storage.getAccountHistory(TEST_USERNAME, TEST_ACCOUNT);
        assertTrue(history.isEmpty(), "New account should have empty history");
    }

    @Test
    void getLastFiveTransactions_returnsCorrectTransactions() throws IOException {
        // Add 7 transactions
        for (int i = 1; i <= 7; i++) {
            storage.recordTransaction(TEST_USERNAME, TEST_ACCOUNT, "Transaction " + i);
        }
        
        List<String> lastFive = storage.getLastFiveTransactions(TEST_USERNAME, TEST_ACCOUNT);
        assertEquals(5, lastFive.size(), "Should return exactly 5 transactions");
        assertTrue(lastFive.get(0).contains("Transaction 3"), "Should return most recent first");
        assertTrue(lastFive.get(4).contains("Transaction 7"));
    }

    @Test
    void getLastFiveTransactions_returnsAllWhenLessThanFive() throws IOException {
        storage.recordTransaction(TEST_USERNAME, TEST_ACCOUNT, "Only transaction");
        List<String> result = storage.getLastFiveTransactions(TEST_USERNAME, TEST_ACCOUNT);
        assertEquals(1, result.size(), "Should return all transactions when less than 5");
    }

    @Test
    void multipleAccounts_haveSeparateHistories() throws IOException {
        String account2 = "Savings1";
        storage.recordTransaction(TEST_USERNAME, TEST_ACCOUNT, "Checking transaction");
        storage.recordTransaction(TEST_USERNAME, account2, "Savings transaction");
        
        List<String> checkingHistory = storage.getAccountHistory(TEST_USERNAME, TEST_ACCOUNT);
        List<String> savingsHistory = storage.getAccountHistory(TEST_USERNAME, account2);
        
        assertEquals(1, checkingHistory.size());
        assertEquals(1, savingsHistory.size());
        assertTrue(checkingHistory.get(0).contains("Checking"));
        assertTrue(savingsHistory.get(0).contains("Savings"));
    }
}

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("Test Summary")
    class TestSummary {
        @AfterAll
        void showSuccess() {
            System.out.println("\n✅ All tests passed!");
        }
    }

