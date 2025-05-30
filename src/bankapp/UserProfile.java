package bankapp;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Manages user profile information including personal details such as
 * full name, date of birth, contact information, and address.
 */
public class UserProfile {
    // Personal information fields
    private String username;
    private String fullName;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String email;
    private String address;
    
    // Storage constants
    private static final String PROFILE_DIRECTORY = "data/Profiles/";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // Validation patterns
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$");
    
    /**
     * Creates a new UserProfile for the specified username.
     * 
     * @param username The username this profile belongs to
     */
    public UserProfile(String username) {
        this.username = username;
        this.fullName = "";
        this.dateOfBirth = null;
        this.phoneNumber = "";
        this.email = "";
        this.address = "";
        
        createProfileDirectory();
    }
    
    /**
     * Creates a directory for storing profile data if it doesn't exist.
     */
    private void createProfileDirectory() {
        try {
            Files.createDirectories(Paths.get(PROFILE_DIRECTORY));
        } catch (IOException exception) {
            System.err.println("Failed to create profile directory: " + exception.getMessage());
        }
    }
    
    /**
     * Saves the user profile to the file system.
     * 
     * @return true if the save was successful, false otherwise
     */
    public boolean saveProfile() {
        createProfileDirectory();
        
        Path profilePath = getProfilePath();
        
        try {
            Map<String, String> profileData = convertToMap();
            saveMapToFile(profilePath, profileData);
            return true;
        } catch (IOException exception) {
            System.err.println("Error saving profile for " + username + ": " + exception.getMessage());
            return false;
        }
    }
    
    /**
     * Converts the profile fields to a map for storage.
     * 
     * @return A map of field names to values
     */
    private Map<String, String> convertToMap() {
        Map<String, String> profileData = new HashMap<>();
        profileData.put("fullName", fullName);
        profileData.put("dateOfBirth", dateOfBirth != null ? dateOfBirth.format(DATE_FORMAT) : "");
        profileData.put("phoneNumber", phoneNumber);
        profileData.put("email", email);
        profileData.put("address", address);
        
        return profileData;
    }
    
    /**
     * Writes a map of profile data to a file.
     * 
     * @param profilePath The path to the profile file
     * @param profileData The map of profile data to save
     * @throws IOException If there's an error writing to the file
     */
    private void saveMapToFile(Path profilePath, Map<String, String> profileData) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(profilePath)) {
            for (Map.Entry<String, String> entry : profileData.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                
                // Special handling for multiline values
                if (value != null && value.contains("\n")) {
                    // Start with the key and an opening marker
                    writer.write(key + "=<<MULTILINE>>");
                    writer.newLine();
                    // Write the content
                    writer.write(value);
                    writer.newLine();
                    // End with a closing marker
                    writer.write("<<END>>");
                    writer.newLine();
                } else {
                    // Normal single-line entry
                    writer.write(key + "=" + (value != null ? value : ""));
                    writer.newLine();
                }
            }
        }
    }
    
    /**
     * Loads a user profile from the file system.
     * 
     * @param username The username whose profile to load
     * @return The loaded UserProfile, or a new empty profile if none exists
     */
    public static UserProfile loadProfile(String username) {
        UserProfile profile = new UserProfile(username);
        Path profilePath = profile.getProfilePath();
        
        if (!Files.exists(profilePath)) {
            return profile;
        }
        
        try {
            Map<String, String> profileData = loadMapFromFile(profilePath);
            populateFromMap(profile, profileData);
            return profile;
        } catch (IOException exception) {
            System.err.println("Error loading profile for " + username + ": " + exception.getMessage());
            return profile;
        }
    }
    
    /**
     * Reads a map of profile data from a file.
     * 
     * @param profilePath The path to the profile file
     * @return A map of profile data loaded from the file
     * @throws IOException If there's an error reading from the file
     */
    private static Map<String, String> loadMapFromFile(Path profilePath) throws IOException {
        Map<String, String> profileData = new HashMap<>();
        
        try (BufferedReader reader = Files.newBufferedReader(profilePath)) {
            String line;
            String currentKey = null;
            StringBuilder multilineValue = null;
            boolean inMultiline = false;
            
            while ((line = reader.readLine()) != null) {
                if (inMultiline) {
                    // Check if this is the end marker
                	if (line.equals("<<END>>")) {
                	    // End of multiline value
                	    String value = multilineValue.toString();
                	    // Trim any trailing newline
                	    if (value.endsWith("\n")) {
                	        value = value.substring(0, value.length() - 1);
                	    }
                	    profileData.put(currentKey, value);
                	    inMultiline = false;
                    } else {
                        // Add this line to the multiline value
                        multilineValue.append(line).append("\n");
                    }
                } else {
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        if (parts[1].equals("<<MULTILINE>>")) {
                            // Start of a multiline value
                            currentKey = parts[0];
                            multilineValue = new StringBuilder();
                            inMultiline = true;
                        } else {
                            // Normal single-line entry
                            profileData.put(parts[0], parts[1]);
                        }
                    }
                }
            }
        }
        
        return profileData;
    }
    
    /**
     * Populates a profile object from a map of profile data.
     * 
     * @param profile The profile to populate
     * @param profileData The map of profile data
     */
    private static void populateFromMap(UserProfile profile, Map<String, String> profileData) {
        profile.setFullName(profileData.getOrDefault("fullName", ""));
        
        String birthDateStr = profileData.getOrDefault("dateOfBirth", "");
        if (!birthDateStr.isEmpty()) {
            try {
                profile.setDateOfBirth(LocalDate.parse(birthDateStr, DATE_FORMAT));
            } catch (DateTimeParseException exception) {
                System.err.println("Invalid date format in profile: " + exception.getMessage());
            }
        }
        
        profile.setPhoneNumber(profileData.getOrDefault("phoneNumber", ""));
        profile.setEmail(profileData.getOrDefault("email", ""));
        profile.setAddress(profileData.getOrDefault("address", ""));
    }
    
    /**
     * Gets the file path for this user's profile.
     * 
     * @return The path to the profile file
     */
    private Path getProfilePath() {
        return Paths.get(PROFILE_DIRECTORY, username + ".txt");
    }
    
    /**
     * Sets the user's full name.
     * 
     * @param fullName The full name to set
     * @return true if the name was valid and set, false otherwise
     */
    public boolean setFullName(String fullName) {
        if (isValidName(fullName)) {
            this.fullName = fullName;
            return true;
        }
        return false;
    }
    
    /**
     * Validates a user's full name.
     * 
     * @param fullName The name to validate
     * @return true if the name is valid, false otherwise
     */
    private boolean isValidName(String fullName) {
        return fullName != null && !fullName.trim().isEmpty();
    }
    
    /**
     * Sets the user's date of birth.
     * 
     * @param dateOfBirth The date of birth to set
     * @return true if the date was valid and set, false otherwise
     */
    public boolean setDateOfBirth(LocalDate dateOfBirth) {
        if (isValidDateOfBirth(dateOfBirth)) {
            this.dateOfBirth = dateOfBirth;
            return true;
        }
        return false;
    }
    
    /**
     * Validates a date of birth.
     * 
     * @param dateOfBirth The date to validate
     * @return true if the date is valid, false otherwise
     */
    private boolean isValidDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return false;
        }
        
        LocalDate currentDate = LocalDate.now();
        return !dateOfBirth.isAfter(currentDate) && 
               !dateOfBirth.isBefore(currentDate.minusYears(120));
    }
    
    /**
     * Sets the user's phone number.
     * 
     * @param phoneNumber The phone number to set
     * @return true if the phone number was valid and set, false otherwise
     */
    public boolean setPhoneNumber(String phoneNumber) {
        if (isValidPhoneNumber(phoneNumber)) {
            this.phoneNumber = phoneNumber;
            return true;
        }
        return false;
    }
    
    /**
     * Validates a phone number.
     * 
     * @param phoneNumber The phone number to validate
     * @return true if the phone number is valid, false otherwise
     */
    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && PHONE_PATTERN.matcher(phoneNumber).matches();
    }
    
    /**
     * Sets the user's email address.
     * 
     * @param email The email to set
     * @return true if the email was valid and set, false otherwise
     */
    public boolean setEmail(String email) {
        if (isValidEmail(email)) {
            this.email = email;
            return true;
        }
        return false;
    }
    
    /**
     * Validates an email address.
     * 
     * @param email The email to validate
     * @return true if the email is valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Sets the user's address.
     * 
     * @param address The address to set
     * @return true if the address was valid and set, false otherwise
     */
    public boolean setAddress(String address) {
        if (isValidAddress(address)) {
            this.address = address;
            return true;
        }
        return false;
    }
    
    /**
     * Validates an address.
     * 
     * @param address The address to validate
     * @return true if the address is valid, false otherwise
     */
    private boolean isValidAddress(String address) {
        return address != null && !address.trim().isEmpty();
    }
    
    /**
     * Gets the profile owner's username.
     * 
     * @return The username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Gets the user's full name.
     * 
     * @return The full name
     */
    public String getFullName() {
        return fullName;
    }
    
    /**
     * Gets the user's date of birth.
     * 
     * @return The date of birth
     */
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    
    /**
     * Gets the user's phone number.
     * 
     * @return The phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    /**
     * Gets the user's email address.
     * 
     * @return The email address
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Gets the user's address.
     * 
     * @return The address
     */
    public String getAddress() {
        return address;
    }
    
    /**
     * Formats the profile information for display.
     * 
     * @return A formatted string representation of the profile
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        
        builder.append("Profile for ").append(username).append(":\n");
        builder.append("Full Name: ").append(fullName.isEmpty() ? "[Not Set]" : fullName).append("\n");
        
        String birthDateStr = dateOfBirth != null 
            ? dateOfBirth.format(DATE_FORMAT) 
            : "[Not Set]";
        builder.append("Date of Birth: ").append(birthDateStr).append("\n");
        
        builder.append("Phone Number: ").append(phoneNumber.isEmpty() ? "[Not Set]" : phoneNumber).append("\n");
        builder.append("Email: ").append(email.isEmpty() ? "[Not Set]" : email).append("\n");
        
        // Special handling for multiline address
        builder.append("Address: ");
        if (address.isEmpty()) {
            builder.append("[Not Set]");
        } else {
            // Replace newlines with ", " for display purposes
            String displayAddress = address.replace("\n", ", ");
            builder.append(displayAddress);
        }
        
        return builder.toString();
    }
}