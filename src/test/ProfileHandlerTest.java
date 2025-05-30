package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bankapp.ProfileHandler;
import bankapp.UserProfile;

class ProfileHandlerTest {
    
    private static final String TEST_USERNAME = "profileHandlerTest";
    private static final String PROFILE_DIRECTORY = "data/Profiles/";
    
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final ByteArrayInputStream testIn = new ByteArrayInputStream("".getBytes());
    private final java.io.InputStream originalIn = System.in;
    
    @BeforeEach
    void setUp() throws Exception {
        // Redirect stdout for testing console output
        System.setOut(new PrintStream(outContent));
        
        // Create profiles directory if it doesn't exist
        Files.createDirectories(Paths.get(PROFILE_DIRECTORY));
        
        // Delete any existing test profile
        Files.deleteIfExists(Paths.get(PROFILE_DIRECTORY, TEST_USERNAME + ".txt"));
    }
    
    @AfterEach
    void tearDown() throws Exception {
        // Restore stdout and stdin
        System.setOut(originalOut);
        System.setIn(originalIn);
        
        // Delete test profile file if it exists
        Files.deleteIfExists(Paths.get(PROFILE_DIRECTORY, TEST_USERNAME + ".txt"));
    }
    
    // Helper method to provide simulated user input
    private void provideInput(String data) {
        System.setIn(new ByteArrayInputStream(data.getBytes()));
    }
    
    @Test
    void testViewProfile() {
        // Setup test profile
        UserProfile profile = new UserProfile(TEST_USERNAME);
        profile.setFullName("John Doe");
        profile.setEmail("john@example.com");
        profile.saveProfile();
        
        // Simulate viewing profile then exiting
        // "1" to view profile, enter to continue, "0" to exit
        provideInput("1\n\n0\n");
        Scanner scanner = new Scanner(System.in);
        
        // Run the profile handler
        ProfileHandler.manageProfile(profile, scanner);
        
        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("John Doe"), "Output should display the user's name");
        assertTrue(output.contains("john@example.com"), "Output should display the user's email");
        
        scanner.close();
    }
    
    @Test
    void testEditFullName() {
        // Setup test profile
        UserProfile profile = new UserProfile(TEST_USERNAME);
        
        // Simulate editing name then exiting
        // "2" to edit name, "Jane Smith" as new name, "0" to exit
        provideInput("2\nJane Smith\n0\n");
        Scanner scanner = new Scanner(System.in);
        
        // Run the profile handler
        ProfileHandler.manageProfile(profile, scanner);
        
        // Check result
        assertEquals("Jane Smith", profile.getFullName(), "Full name should be updated");
        
        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("Full name updated successfully"), "Output should confirm successful update");
        
        scanner.close();
    }
    
    @Test
    void testEditInvalidEmail() {
        // Setup test profile
        UserProfile profile = new UserProfile(TEST_USERNAME);
        
        // Simulate editing email with invalid format then exiting
        // "5" to edit email, "invalid-email" as invalid email, "0" to exit
        provideInput("5\ninvalid-email\n0\n");
        Scanner scanner = new Scanner(System.in);
        
        // Run the profile handler
        ProfileHandler.manageProfile(profile, scanner);
        
        // Check result
        assertEquals("", profile.getEmail(), "Email should not be updated with invalid format");
        
        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("Invalid email format"), "Output should indicate email format error");
        
        scanner.close();
    }
    
    @Test
    void testEditDateOfBirth() {
        // Setup test profile
        UserProfile profile = new UserProfile(TEST_USERNAME);
        
        // Simulate editing date of birth then exiting
        // "3" to edit DOB, "1990-05-15" as DOB, "0" to exit
        provideInput("3\n1990-05-15\n0\n");
        Scanner scanner = new Scanner(System.in);
        
        // Run the profile handler
        ProfileHandler.manageProfile(profile, scanner);
        
        // Check result
        assertEquals(LocalDate.of(1990, 5, 15), profile.getDateOfBirth(), "Date of birth should be updated");
        
        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("Date of birth updated successfully"), "Output should confirm successful update");
        
        scanner.close();
    }
    
    @Test
    void testEditInvalidDateOfBirth() {
        // Setup test profile
        UserProfile profile = new UserProfile(TEST_USERNAME);
        
        // Simulate editing date of birth with invalid format then exiting
        // "3" to edit DOB, "05/15/1990" as invalid DOB format, "0" to exit
        provideInput("3\n05/15/1990\n0\n");
        Scanner scanner = new Scanner(System.in);
        
        // Run the profile handler
        ProfileHandler.manageProfile(profile, scanner);
        
        // Check result
        assertNull(profile.getDateOfBirth(), "Date of birth should not be updated with invalid format");
        
        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("Invalid date format"), "Output should indicate date format error");
        
        scanner.close();
    }
    
    @Test
    void testEditMultilineAddress() {
        // Setup test profile
        UserProfile profile = new UserProfile(TEST_USERNAME);
        
        // Simulate editing address then exiting
        // "6" to edit address, two-line address with blank line to finish, "0" to exit
        provideInput("6\n123 Main St\nAnytown, NY 12345\n\n0\n");
        Scanner scanner = new Scanner(System.in);
        
        // Run the profile handler
        ProfileHandler.manageProfile(profile, scanner);
        
        // Check result
        assertTrue(profile.getAddress().contains("123 Main St"), "Address should contain first line");
        assertTrue(profile.getAddress().contains("Anytown, NY 12345"), "Address should contain second line");
        
        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("Address updated successfully"), "Output should confirm successful update");
        
        scanner.close();
    }
    
    @Test
    void testInvalidMenuOption() {
        // Setup test profile
        UserProfile profile = new UserProfile(TEST_USERNAME);
        
        // Simulate entering invalid option then exiting
        // "9" as invalid option, "0" to exit
        provideInput("9\n0\n");
        Scanner scanner = new Scanner(System.in);
        
        // Run the profile handler
        ProfileHandler.manageProfile(profile, scanner);
        
        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("Invalid option"), "Output should indicate invalid option");
        
        scanner.close();
    }
    
    @Test
    void testEditAllFields() {
        // Setup test profile
        UserProfile profile = new UserProfile(TEST_USERNAME);
        
        // Simulate editing all fields then exiting
        // This is a comprehensive test of the entire flow
        String input = "2\nJohn Doe\n" +            // Edit name
                       "3\n1985-03-20\n" +          // Edit DOB
                       "4\n555-987-6543\n" +        // Edit phone
                       "5\njohn.doe@example.com\n" + // Edit email
                       "6\n123 Oak St\nSuite 4B\nBoston, MA 02108\n\n" + // Edit address (multiline)
                       "1\n\n" +                    // View profile and continue
                       "0\n";                       // Exit
        
        provideInput(input);
        Scanner scanner = new Scanner(System.in);
        
        // Run the profile handler
        ProfileHandler.manageProfile(profile, scanner);
        
        // Check results
        assertEquals("John Doe", profile.getFullName());
        assertEquals(LocalDate.of(1985, 3, 20), profile.getDateOfBirth());
        assertEquals("555-987-6543", profile.getPhoneNumber());
        assertEquals("john.doe@example.com", profile.getEmail());
        assertTrue(profile.getAddress().contains("123 Oak St"));
        
        // Check output from view profile
        String output = outContent.toString();
        assertTrue(output.contains("John Doe"), "Output should display updated name");
        assertTrue(output.contains("1985-03-20"), "Output should display updated DOB");
        assertTrue(output.contains("555-987-6543"), "Output should display updated phone");
        assertTrue(output.contains("john.doe@example.com"), "Output should display updated email");
        assertTrue(output.contains("123 Oak St"), "Output should display updated address");
        
        scanner.close();
    }
}