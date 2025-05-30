package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bankapp.UserProfile;

class UserProfileTest {
    
    private static final String TEST_USERNAME = "testUser";
    private static final String TEST_FULLNAME = "Test User";
    private static final LocalDate TEST_DOB = LocalDate.of(1990, 1, 15);
    private static final String TEST_PHONE = "555-123-4567";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_ADDRESS = "123 Test St\nTest City, TS 12345";
    private static final String PROFILE_DIRECTORY = "data/Profiles/";
    
    @BeforeEach
    void setUp() throws Exception {
        // Create profiles directory if it doesn't exist
        Files.createDirectories(Paths.get(PROFILE_DIRECTORY));
        
        // Delete any existing test profile
        Path testProfilePath = Paths.get(PROFILE_DIRECTORY, TEST_USERNAME + ".txt");
        if (Files.exists(testProfilePath)) {
            Files.delete(testProfilePath);
        }
    }
    
    @AfterEach
    void tearDown() throws Exception {
        // Delete test profile file if it exists
        Path testProfilePath = Paths.get(PROFILE_DIRECTORY, TEST_USERNAME + ".txt");
        if (Files.exists(testProfilePath)) {
            Files.delete(testProfilePath);
        }
    }
    
    @Test
    void testProfileCreation() {
        // Create a new profile
        UserProfile profile = new UserProfile(TEST_USERNAME);
        
        // Check username was set correctly
        assertEquals(TEST_USERNAME, profile.getUsername(), "Username should be set correctly");
        
        // Profile should have empty default values
        assertEquals("", profile.getFullName(), "Default full name should be empty");
        assertNull(profile.getDateOfBirth(), "Default date of birth should be null");
        assertEquals("", profile.getPhoneNumber(), "Default phone number should be empty");
        assertEquals("", profile.getEmail(), "Default email should be empty");
        assertEquals("", profile.getAddress(), "Default address should be empty");
    }
    
    @Test
    void testSaveAndLoadProfile() {
        // Create a new profile with test data
        UserProfile profile = new UserProfile(TEST_USERNAME);
        profile.setFullName(TEST_FULLNAME);
        profile.setDateOfBirth(TEST_DOB);
        profile.setPhoneNumber(TEST_PHONE);
        profile.setEmail(TEST_EMAIL);
        profile.setAddress(TEST_ADDRESS);
        
        // Save the profile
        boolean saveResult = profile.saveProfile();
        assertTrue(saveResult, "Profile should save successfully");
        
        // Check that the file was created
        Path profilePath = Paths.get(PROFILE_DIRECTORY, TEST_USERNAME + ".txt");
        assertTrue(Files.exists(profilePath), "Profile file should exist after saving");
        
        // Load the profile for a different instance
        UserProfile loadedProfile = UserProfile.loadProfile(TEST_USERNAME);
        
        // Verify loaded data matches original
        assertEquals(TEST_USERNAME, loadedProfile.getUsername(), "Loaded username should match");
        assertEquals(TEST_FULLNAME, loadedProfile.getFullName(), "Loaded full name should match");
        assertEquals(TEST_DOB, loadedProfile.getDateOfBirth(), "Loaded date of birth should match");
        assertEquals(TEST_PHONE, loadedProfile.getPhoneNumber(), "Loaded phone number should match");
        assertEquals(TEST_EMAIL, loadedProfile.getEmail(), "Loaded email should match");
        assertEquals(TEST_ADDRESS, loadedProfile.getAddress(), "Loaded address should match");
    }
    
    @Test
    void testLoadNonExistentProfile() {
        // Ensure test profile doesn't exist
        Path profilePath = Paths.get(PROFILE_DIRECTORY, "nonexistent.txt");
        assertFalse(Files.exists(profilePath), "Test requires nonexistent profile");
        
        // Load a profile that doesn't exist
        UserProfile profile = UserProfile.loadProfile("nonexistent");
        
        // Should get a new empty profile
        assertNotNull(profile, "Should get a valid profile object");
        assertEquals("nonexistent", profile.getUsername(), "Username should be set correctly");
        assertEquals("", profile.getFullName(), "Full name should be empty");
        assertNull(profile.getDateOfBirth(), "Date of birth should be null");
        assertEquals("", profile.getPhoneNumber(), "Phone number should be empty");
        assertEquals("", profile.getEmail(), "Email should be empty");
        assertEquals("", profile.getAddress(), "Address should be empty");
    }
    
    @Test
    void testFullNameValidation() {
        UserProfile profile = new UserProfile(TEST_USERNAME);
        
        // Valid names
        assertTrue(profile.setFullName("John Doe"), "Valid full name should be accepted");
        assertTrue(profile.setFullName("Maria García-López"), "Name with hyphen should be accepted");
        assertTrue(profile.setFullName("José María"), "Name with accents should be accepted");
        
        // Invalid names
        assertFalse(profile.setFullName(""), "Empty name should be rejected");
        assertFalse(profile.setFullName(null), "Null name should be rejected");
        assertFalse(profile.setFullName("   "), "Whitespace-only name should be rejected");
    }
    
    @Test
    void testDateOfBirthValidation() {
        UserProfile profile = new UserProfile(TEST_USERNAME);
        
        // Valid dates
        assertTrue(profile.setDateOfBirth(LocalDate.of(1990, 1, 1)), "Valid past date should be accepted");
        assertTrue(profile.setDateOfBirth(LocalDate.now().minusDays(1)), "Yesterday should be accepted");
        assertTrue(profile.setDateOfBirth(LocalDate.now()), "Today should be accepted");
        
        // Invalid dates
        assertFalse(profile.setDateOfBirth(null), "Null date should be rejected");
        assertFalse(profile.setDateOfBirth(LocalDate.now().plusDays(1)), "Future date should be rejected");
        assertFalse(profile.setDateOfBirth(LocalDate.now().minusYears(121)), "Date over 120 years ago should be rejected, boy you are not that old be fr");
    }
    
    @Test
    void testPhoneNumberValidation() {
        UserProfile profile = new UserProfile(TEST_USERNAME);
        
        // Valid phone numbers
        assertTrue(profile.setPhoneNumber("555-123-4567"), "Standard format should be accepted");
        assertTrue(profile.setPhoneNumber("(555) 123-4567"), "Parentheses format should be accepted");
        assertTrue(profile.setPhoneNumber("5551234567"), "No separators should be accepted");
        assertTrue(profile.setPhoneNumber("+1 555-123-4567"), "International format should be accepted");
        
        // Invalid phone numbers
        assertFalse(profile.setPhoneNumber(""), "Empty phone number should be rejected");
        assertFalse(profile.setPhoneNumber(null), "Null phone number should be rejected");
        assertFalse(profile.setPhoneNumber("123-45-678"), "Incorrect digit grouping should be rejected");
        assertFalse(profile.setPhoneNumber("abcdefghij"), "Letters should be rejected");
        assertFalse(profile.setPhoneNumber("555-123-456"), "Too few digits should be rejected");
    }
    
    @Test
    void testEmailValidation() {
        UserProfile profile = new UserProfile(TEST_USERNAME);
        
        // Valid emails
        assertTrue(profile.setEmail("user@example.com"), "Standard email should be accepted");
        assertTrue(profile.setEmail("user.name+tag@example.co.uk"), "Complex email should be accepted");
        assertTrue(profile.setEmail("user_name@example-domain.com"), "Email with underscore should be accepted");
        
        // Invalid emails
        assertFalse(profile.setEmail(""), "Empty email should be rejected");
        assertFalse(profile.setEmail(null), "Null email should be rejected");
        assertFalse(profile.setEmail("user@"), "Incomplete email should be rejected");
        assertFalse(profile.setEmail("user@.com"), "Missing domain part should be rejected");
        assertFalse(profile.setEmail("@example.com"), "Missing username part should be rejected");
        assertFalse(profile.setEmail("user@example"), "Missing TLD should be rejected");
        assertFalse(profile.setEmail("user name@example.com"), "Space in email should be rejected");
    }
    
    @Test
    void testAddressValidation() {
        UserProfile profile = new UserProfile(TEST_USERNAME);
        
        // Valid addresses
        assertTrue(profile.setAddress("123 Main St"), "Single line address should be accepted");
        assertTrue(profile.setAddress("123 Main St\nAnytown, ST 12345"), "Multi-line address should be accepted");
        assertTrue(profile.setAddress("Apt 4B, 123 Main St\nAnytown, ST 12345\nUnited States"), "Complex address should be accepted");
        
        // Invalid addresses
        assertFalse(profile.setAddress(""), "Empty address should be rejected");
        assertFalse(profile.setAddress(null), "Null address should be rejected");
        assertFalse(profile.setAddress("   "), "Whitespace-only address should be rejected");
    }
    
    @Test
    void testToString() {
        // Create profile with test data
        UserProfile profile = new UserProfile(TEST_USERNAME);
        profile.setFullName(TEST_FULLNAME);
        profile.setDateOfBirth(TEST_DOB);
        profile.setPhoneNumber(TEST_PHONE);
        profile.setEmail(TEST_EMAIL);
        profile.setAddress(TEST_ADDRESS);
        
        // Get string representation
        String profileString = profile.toString();
        
        // Check that all fields are included
        assertTrue(profileString.contains(TEST_USERNAME), "toString should include username");
        assertTrue(profileString.contains(TEST_FULLNAME), "toString should include full name");
        assertTrue(profileString.contains(TEST_DOB.toString().substring(0, 10)), "toString should include date of birth");
        assertTrue(profileString.contains(TEST_PHONE), "toString should include phone number");
        assertTrue(profileString.contains(TEST_EMAIL), "toString should include email");
        assertTrue(profileString.contains(TEST_ADDRESS.replace("\n", ", ")), "toString should include address");    }
    
    @Test
    void testEmptyProfileToString() {
        // Create empty profile
        UserProfile profile = new UserProfile(TEST_USERNAME);
        
        // Get string representation
        String profileString = profile.toString();
        
        // Check that placeholders are included for empty fields
        assertTrue(profileString.contains("[Not Set]"), "toString should include [Not Set] for empty fields");
    }
    
    @Test
    void testProfileDirectoryCreation() {
        // Instead of deleting, create a unique test directory
        String testDirPath = PROFILE_DIRECTORY + "test_" + System.currentTimeMillis() + "/";
        
        // Create a profile that will use this test directory
        UserProfile profile = new UserProfile(TEST_USERNAME);
        
        // Check if the main profiles directory exists
        assertTrue(Files.exists(Paths.get(PROFILE_DIRECTORY)), "Profiles directory should be created");
        
        // Clean up any created directories after test
        try {
            Files.deleteIfExists(Paths.get(PROFILE_DIRECTORY, TEST_USERNAME + ".txt"));
        } catch (IOException e) {
            // Ignore cleanup errors
        }
    }
}