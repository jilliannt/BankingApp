package bankapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages user accounts for the banking application.
 * Handles user authentication, creation and lookup.
 */
public class UserManager {
    private static UserManager instance;
    private List<User> users;
    
    /**
     * Private constructor for singleton pattern.
     */
    private UserManager() {
        users = new ArrayList<>();
        loadUsers();
    }
    
    /**
     * Gets the singleton instance of UserManager.
     * 
     * @return The UserManager instance
     */
    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }
    
    /**
     * Loads users from the data file.
     */
    private void loadUsers() {
        File file = new File(User.getUserFilePath());
        
        if (!file.exists()) {
            System.out.println("No existing users file found. Starting with empty user list.");
            return;
        }
        
        users.clear();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                User user = parseUserFromLine(line);
                if (user != null) {
                    users.add(user);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }
    
    /**
     * Parses a user from a line in the users file.
     * 
     * @param line The line to parse
     * @return The user object or null if the line is invalid
     */
    private User parseUserFromLine(String line) {
        String[] parts = line.split(",");
        if (parts.length != 2) {
            System.err.println("Invalid user format in file: " + line);
            return null;
        }
            
        String username = parts[0];
        String passwordHash = parts[1];
        return User.loadExistingUser(username, passwordHash);
    }
    
    /**
     * Checks if a username already exists.
     * 
     * @param username The username to check
     * @return true if the username exists, false otherwise
     */
    public boolean usernameExists(String username) {
        return findUserByUsername(username) != null;
    }
    
    /**
     * Finds a user by their username.
     * 
     * @param username The username to search for
     * @return The user with the specified username, or null if not found
     */
    private User findUserByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
    
    /**
     * Authenticates a user with their username and password.
     * 
     * @param username The username
     * @param password The password
     * @return The authenticated user, or null if authentication failed
     */
    public User login(String username, String password) {
        User user = findUserByUsername(username);
        
        if (user == null) {
            return null; // User not found
        }
        
        if (!user.validatePassword(password)) {
            return null; // Invalid password
        }
        
        return user; // Authentication successful
    }
    
    /**
     * Creates a new user account.
     * 
     * @param username The username for the new account
     * @param password The password for the new account
     * @return The newly created user, or null if creation failed
     */
    public User createAccount(String username, String password) {
        if (usernameExists(username)) {
            System.out.println("Username already exists: " + username);
            return null;
        }
        
        User newUser = createNewUser(username, password);
        if (newUser == null) {
            return null;
        }
        
        users.add(newUser);
        return newUser;
    }
    
    /**
     * Creates a new user object and saves it.
     * 
     * @param username The username
     * @param password The password
     * @return The new user or null if saving failed
     */
    private User createNewUser(String username, String password) {
        User newUser = new User(username, password);
        
        if (!newUser.saveUser()) {
            System.out.println("Failed to save user to file.");
            return null;
        }
        
        return newUser;
    }
    
    /**
     * Refreshes the user list from the data file.
     */
    public void refreshUsers() {
        loadUsers();
    }
    
    /**
     * Gets the number of registered users.
     * 
     * @return The number of users
     */
    public int getUserCount() {
        return users.size();
    }
}