package bankapp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class AccountStorage {
    private final Path accountsRoot;
    private static final String HISTORY_FILE = "_history.txt";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

    public AccountStorage() {
        this(Paths.get("..", "data", "Accounts"));
    }

    public AccountStorage(Path accountsRoot) {
        this.accountsRoot = accountsRoot;
        try {
            Files.createDirectories(accountsRoot);
        } catch (IOException e) {
            System.err.println("Failed to create accounts directory: " + e.getMessage());
        }
    }

    private Path getUserPath(String username) throws IOException {
        Path userPath = accountsRoot.resolve(username);
        if (!Files.exists(userPath)) {
            Files.createDirectories(userPath);
        }
        return userPath;
    }

    public void recordTransaction(String username, String accountName, String transaction) throws IOException {
        Path historyPath = getUserPath(username).resolve(accountName + HISTORY_FILE);
        String timestampedTransaction = transaction + ", " + DATE_FORMAT.format(new Date()) + System.lineSeparator();
        Files.write(historyPath, 
                   timestampedTransaction.getBytes(), 
                   StandardOpenOption.CREATE, 
                   StandardOpenOption.APPEND);
    }

    public List<String> getAccountHistory(String username, String accountName) throws IOException {
        Path historyPath = getUserPath(username).resolve(accountName + HISTORY_FILE);
        if (!Files.exists(historyPath)) {
            return Collections.emptyList();
        }
        return Files.readAllLines(historyPath);
    }

    public List<String> getLastFiveTransactions(String username, String accountName) throws IOException {
        List<String> allTransactions = getAccountHistory(username, accountName);
        int startIdx = Math.max(0, allTransactions.size() - 5);
        return allTransactions.subList(startIdx, allTransactions.size());
    }

}