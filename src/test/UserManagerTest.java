package test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import bankapp.User;
import bankapp.UserManager;

public class UserManagerTest {
    
    private UserManager userManager;
    private final String testDataDir = "data";
    private final String testUserFile = "data/users.txt";
    
    @Before
    public void setUp() {
        // Create test directory if it doesn't exist
        Path dataDir = Paths.get(testDataDir);
        if (!Files.exists(dataDir)) {
            try {
                Files.createDirectory(dataDir);
            } catch (Exception e) {
                fail("Could not create test data directory: " + e.getMessage());
            }
        }
        
        // Create a clean test user file
        File userFile = new File(testUserFile);
        try {
            if (userFile.exists()) {
                userFile.delete();
            }
            userFile.createNewFile();
        } catch (IOException e) {
            fail("Could not create test user file: " + e.getMessage());
        }
        
        // Initialize the user manager
        userManager = UserManager.getInstance();
        userManager.refreshUsers();
    }
    
    @After
    public void tearDown() {
        // Clean up the test file
        File userFile = new File(testUserFile);
        if (userFile.exists()) {
            userFile.delete();
        }
    }
    
    @Test
    public void testCreateAccount() {
        // Create a new account
        User user = userManager.createAccount("testuser", "Password123!");
        
        // Verify account creation
        assertNotNull("User should be created successfully", user);
        assertEquals("Username should match", "testuser", user.getUsername());
        
        // Verify user count increased
        assertEquals("User count should be 1", 1, userManager.getUserCount());
    }
    
    @Test
    public void testCreateAccountWithExistingUsername() {
        // Create an initial account
        userManager.createAccount("testuser", "Password123!");
        
        // Attempt to create an account with the same username
        User duplicateUser = userManager.createAccount("testuser", "DifferentPassword123!");
        
        // Verify account creation failed
        assertNull("Duplicate user creation should fail", duplicateUser);
        
        // Verify user count remains the same
        assertEquals("User count should still be 1", 1, userManager.getUserCount());
    }
    
    @Test
    public void testLoginWithValidCredentials() {
        // Create a test account
        userManager.createAccount("testuser", "Password123!");
        
        // Attempt login with correct credentials
        User loggedInUser = userManager.login("testuser", "Password123!");
        
        // Verify login was successful
        assertNotNull("Login should succeed with correct credentials", loggedInUser);
        assertEquals("Username should match", "testuser", loggedInUser.getUsername());
    }
    
    @Test
    public void testLoginWithInvalidUsername() {
        // Create a test account
        userManager.createAccount("testuser", "Password123!");
        
        // Attempt login with incorrect username
        User loggedInUser = userManager.login("nonexistentuser", "Password123!");
        
        // Verify login failed
        assertNull("Login should fail with incorrect username", loggedInUser);
    }
    
    @Test
    public void testLoginWithInvalidPassword() {
        // Create a test account
        userManager.createAccount("testuser", "Password123!");
        
        // Attempt login with incorrect password
        User loggedInUser = userManager.login("testuser", "WrongPassword123!");
        
        // Verify login failed
        assertNull("Login should fail with incorrect password", loggedInUser);
    }
    
    @Test
    public void testUsernameExists() {
        // Create a test account
        userManager.createAccount("testuser", "Password123!");
        
        // Check if username exists
        boolean exists = userManager.usernameExists("testuser");
        boolean notExists = userManager.usernameExists("nonexistentuser");
        
        // Verify check is correct
        assertTrue("Username should exist", exists);
        assertFalse("Username should not exist", notExists);
    }
    
    @Test
    public void testLoadUsersFromFile() throws IOException {
        // Create a test user file with known content
        try (FileWriter writer = new FileWriter(testUserFile)) {
            writer.write("testuser1,hashedpassword1\n");
            writer.write("testuser2,hashedpassword2\n");
        }
        
        // Refresh users to load from file
        userManager.refreshUsers();
        
        // Verify users were loaded
        assertTrue("testuser1 should exist", userManager.usernameExists("testuser1"));
        assertTrue("testuser2 should exist", userManager.usernameExists("testuser2"));
        assertEquals("User count should be 2", 2, userManager.getUserCount());
    }
    
    @Test
    public void testLoadUsersWithInvalidFormat() throws IOException {
        // Create a test user file with invalid format
        try (FileWriter writer = new FileWriter(testUserFile)) {
            writer.write("testuser1,hashedpassword1\n");
            writer.write("invalidformat\n"); // Missing password hash
            writer.write("testuser2,hashedpassword2\n");
        }
        
        // Refresh users to load from file
        userManager.refreshUsers();
        
        // Verify valid users were loaded and invalid one was skipped
        assertTrue("testuser1 should exist", userManager.usernameExists("testuser1"));
        assertTrue("testuser2 should exist", userManager.usernameExists("testuser2"));
        assertFalse("invalidformat should not exist", userManager.usernameExists("invalidformat"));
        assertEquals("User count should be 2", 2, userManager.getUserCount());
    }
    
    @Test
    public void testGetUserCount() {
        // Initial count should be 0
        assertEquals("Initial user count should be 0", 0, userManager.getUserCount());
        
        // Add a user
        userManager.createAccount("testuser1", "Password123!");
        assertEquals("User count should be 1", 1, userManager.getUserCount());
        
        // Add another user
        userManager.createAccount("testuser2", "Password123!");
        assertEquals("User count should be 2", 2, userManager.getUserCount());
    }
}