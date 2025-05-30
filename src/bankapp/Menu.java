package bankapp;

import java.util.Scanner;

public class Menu {
    private static Scanner scanner;
    private static LoginManager loginManager;
    
    public static void main(String[] args) {
        initialize();
        User authenticatedUser = attemptAuthentication();
        
        if (authenticatedUser != null) {
            runUserSession(authenticatedUser);
        } else {
            System.out.println("Login/Registration canceled or failed.");
        }
        
        cleanup();
    }
    
    /**
     * Initializes the application components.
     */
    private static void initialize() {
        System.out.println("Welcome to the Banking System");
        System.out.println("Please login or create an account");
        
        scanner = new Scanner(System.in);
        loginManager = new LoginManager();
    }
    
    /**
     * Attempts to authenticate a user.
     * 
     * @return The authenticated user or null if authentication failed
     */
    private static User attemptAuthentication() {
        return loginManager.start();
    }
    
    /**
     * Runs the main user session after successful authentication.
     * 
     * @param user The authenticated user
     */
    private static void runUserSession(User user) {
        System.out.println("\nWelcome, " + user.getUsername() + "!");
        System.out.println("You are now logged in to your account.");
        
        UserProfile userProfile = user.getProfile();
        AccountManager accountManager = user.getAccountManager();
        
        boolean continueSession = true;
        while (continueSession) {
            displayMainMenu();
            String choice = getUserChoice();
            continueSession = processMainMenuChoice(choice, accountManager, userProfile);
        }
    }
    
    /**
     * Displays the main menu options.
     */
    private static void displayMainMenu() {
        System.out.println("\n===== MAIN MENU =====");
        System.out.println("1. Manage Bank Accounts");
        System.out.println("2. Manage Personal Profile");
        System.out.println("0. Logout");
        System.out.println("====================");
    }
    
    /**
     * Gets the user's menu choice.
     * 
     * @return The user's choice
     */
    private static String getUserChoice() {
        System.out.print("Enter your choice: ");
        return scanner.nextLine();
    }
    
    /**
     * Processes the user's main menu choice.
     * 
     * @param choice The user's choice
     * @param accountManager The account manager
     * @param userProfile The user profile
     * @return true to continue the session, false to end it
     */
    private static boolean processMainMenuChoice(String choice, 
                                             AccountManager accountManager, 
                                             UserProfile userProfile) {
        switch (choice) {
            case "1":
                manageAccounts(accountManager);
                return true;
            case "2":
                manageProfile(userProfile);
                return true;
            case "0":
                return false;
            default:
                System.out.println("Invalid option. Please try again.");
                return true;
        }
    }
    
    /**
     * Handles account management.
     * 
     * @param accountManager The account manager
     */
    private static void manageAccounts(AccountManager accountManager) {
        PromptHandler.manageAccounts(accountManager, scanner);
    }
    
    /**
     * Handles profile management.
     * 
     * @param userProfile The user profile
     */
    private static void manageProfile(UserProfile userProfile) {
        ProfileHandler.manageProfile(userProfile, scanner);
    }
    
    /**
     * Performs cleanup before exiting.
     */
    private static void cleanup() {
        System.out.println("\nThank you for using our banking system!");
        loginManager.close();
        if (scanner != null) {
            scanner.close();
        }
    }
}