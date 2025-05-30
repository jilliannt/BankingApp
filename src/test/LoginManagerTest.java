package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;

import bankapp.LoginManager;
import bankapp.User;
import bankapp.UserManager;

class LoginManagerTest {
    
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;
    
    @BeforeEach
    void setUp() {
        // Save original System.out and redirect to our stream for testing output
        originalOut = System.out;
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }
    
    @AfterEach
    void tearDown() {
        // Restore the original System.out
        System.setOut(originalOut);
    }
    
    @Test
    void testExitFromMainMenu() {
        // Set up input with immediate exit
        String input = "0\n";
        
        // Create a LoginManager using redirected input
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        LoginManager loginManager = new LoginManager();
        
        // Execute the process
        User user = loginManager.start();
        
        // Verify no user returned when exiting
        assertNull(user);
        
        // Verify the menu was displayed
        String output = outputStream.toString();
        assertTrue(output.contains("BANKING SYSTEM"));
        assertTrue(output.contains("1. Login"));
        assertTrue(output.contains("2. Register"));
        assertTrue(output.contains("0. Exit"));
    }
    
    @Test
    void testInvalidMenuOption() {
        // Set up input with invalid menu option followed by exit
        String input = "5\n0\n";
        
        // Create a LoginManager using redirected input
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        LoginManager loginManager = new LoginManager();
        
        // Execute the process
        User user = loginManager.start();
        
        // Verify no user returned
        assertNull(user);
        
        // Verify error message was displayed
        String output = outputStream.toString();
        assertTrue(output.contains("Invalid option"));
    }
}