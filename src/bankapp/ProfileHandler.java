package bankapp;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Handles user interaction for managing profile information.
 */
public class ProfileHandler {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * Manages the profile information flow, allowing users to view and edit their profile.
     * 
     * @param profile The user's profile to manage
     * @param scanner The scanner for user input
     */
    public static void manageProfile(UserProfile profile, Scanner scanner) {
        boolean continueManaging = true;
        
        while (continueManaging) {
            displayProfileMenu(profile);
            String choice = promptForInput(scanner, "Enter your choice: ");
            
            switch (choice) {
                case "1":
                    displayProfile(profile);
                    pauseForUser(scanner);
                    break;
                case "2":
                    editFullName(profile, scanner);
                    break;
                case "3":
                    editDateOfBirth(profile, scanner);
                    break;
                case "4":
                    editPhoneNumber(profile, scanner);
                    break;
                case "5":
                    editEmail(profile, scanner);
                    break;
                case "6":
                    editAddress(profile, scanner);
                    break;
                case "0":
                    continueManaging = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
    
    /**
     * Displays the profile management menu.
     * 
     * @param profile The user's profile
     */
    private static void displayProfileMenu(UserProfile profile) {
        System.out.println("\n===== PROFILE MANAGEMENT =====");
        System.out.println("1. View Profile");
        System.out.println("2. Edit Full Name");
        System.out.println("3. Edit Date of Birth");
        System.out.println("4. Edit Phone Number");
        System.out.println("5. Edit Email");
        System.out.println("6. Edit Address");
        System.out.println("0. Back to Main Menu");
        System.out.println("=============================");
    }
    
    /**
     * Displays the user's profile information.
     * 
     * @param profile The user's profile to display
     */
    private static void displayProfile(UserProfile profile) {
        System.out.println("\n" + profile.toString());
    }
    
    /**
     * Prompts the user to edit their full name.
     * 
     * @param profile The user's profile
     * @param scanner The scanner for user input
     */
    private static void editFullName(UserProfile profile, Scanner scanner) {
        System.out.println("\n----- Edit Full Name -----");
        System.out.println("Current Full Name: " + 
            (profile.getFullName().isEmpty() ? "[Not Set]" : profile.getFullName()));
        
        String fullName = promptForInput(scanner, "Enter your full name: ");
        
        if (profile.setFullName(fullName)) {
            profile.saveProfile();
            System.out.println("Full name updated successfully.");
        } else {
            System.out.println("Invalid name format. Name must not be empty.");
        }
    }
    
    /**
     * Prompts the user to edit their date of birth.
     * 
     * @param profile The user's profile
     * @param scanner The scanner for user input
     */
    private static void editDateOfBirth(UserProfile profile, Scanner scanner) {
        System.out.println("\n----- Edit Date of Birth -----");
        
        LocalDate currentDob = profile.getDateOfBirth();
        String currentDobStr = currentDob != null ? 
            currentDob.format(DATE_FORMAT) : "[Not Set]";
            
        System.out.println("Current Date of Birth: " + currentDobStr);
        System.out.println("Enter date in format YYYY-MM-DD (e.g., 1990-01-15)");
        
        String dobString = promptForInput(scanner, "Enter your date of birth: ");
        
        if (dobString.trim().isEmpty()) {
            System.out.println("No changes made to date of birth.");
            return;
        }
        
        try {
            LocalDate dob = LocalDate.parse(dobString, DATE_FORMAT);
            
            if (profile.setDateOfBirth(dob)) {
                profile.saveProfile();
                System.out.println("Date of birth updated successfully.");
            } else {
                System.out.println("Invalid date. Must be a valid date not in the future and not more than 120 years ago.");
            }
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD format.");
        }
    }
    
    /**
     * Prompts the user to edit their phone number.
     * 
     * @param profile The user's profile
     * @param scanner The scanner for user input
     */
    private static void editPhoneNumber(UserProfile profile, Scanner scanner) {
        System.out.println("\n----- Edit Phone Number -----");
        System.out.println("Current Phone Number: " + 
            (profile.getPhoneNumber().isEmpty() ? "[Not Set]" : profile.getPhoneNumber()));
        System.out.println("Enter phone number (e.g., 555-123-4567 or +1 555-123-4567)");
        
        String phoneNumber = promptForInput(scanner, "Enter your phone number: ");
        
        if (profile.setPhoneNumber(phoneNumber)) {
            profile.saveProfile();
            System.out.println("Phone number updated successfully.");
        } else {
            System.out.println("Invalid phone number format. Please try again.");
        }
    }
    
    /**
     * Prompts the user to edit their email address.
     * 
     * @param profile The user's profile
     * @param scanner The scanner for user input
     */
    private static void editEmail(UserProfile profile, Scanner scanner) {
        System.out.println("\n----- Edit Email -----");
        System.out.println("Current Email: " + 
            (profile.getEmail().isEmpty() ? "[Not Set]" : profile.getEmail()));
        
        String email = promptForInput(scanner, "Enter your email address: ");
        
        if (profile.setEmail(email)) {
            profile.saveProfile();
            System.out.println("Email updated successfully.");
        } else {
            System.out.println("Invalid email format. Please enter a valid email address.");
        }
    }
    
    /**
     * Prompts the user to edit their address.
     * 
     * @param profile The user's profile
     * @param scanner The scanner for user input
     */
    private static void editAddress(UserProfile profile, Scanner scanner) {
        System.out.println("\n----- Edit Address -----");
        System.out.println("Current Address: " + 
            (profile.getAddress().isEmpty() ? "[Not Set]" : profile.getAddress()));
        
        String address = promptForMultilineInput(scanner, "Enter your address (press Enter twice when done):");
        
        if (profile.setAddress(address)) {
            profile.saveProfile();
            System.out.println("Address updated successfully.");
        } else {
            System.out.println("Invalid address. Address must not be empty.");
        }
    }
    
    /**
     * Prompts the user for input with a given prompt message.
     * 
     * @param scanner The scanner for user input
     * @param prompt The prompt message to display
     * @return The user's input
     */
    private static String promptForInput(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    
    /**
     * Prompts the user for multiline input, useful for addresses.
     * 
     * @param scanner The scanner for user input
     * @param prompt The prompt message to display
     * @return The user's multiline input as a single string
     */
    private static String promptForMultilineInput(Scanner scanner, String prompt) {
        System.out.println(prompt);
        StringBuilder input = new StringBuilder();
        String line;
        
        while (true) {
            line = scanner.nextLine();
            if (line.trim().isEmpty() && input.length() > 0) {
                break;
            } else if (!line.trim().isEmpty()) {
                if (input.length() > 0) {
                    input.append("\n");
                }
                input.append(line);
            }
        }
        
        return input.toString();
    }
    
    /**
     * Pauses execution until the user presses Enter.
     * 
     * @param scanner The scanner for user input
     */
    private static void pauseForUser(Scanner scanner) {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
}