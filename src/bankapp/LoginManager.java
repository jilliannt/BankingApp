package bankapp;
import java.util.Scanner;

/**
 * Manages user login and registration functionality.
 * This class provides an interface for users to login or create a new account.
 */
public class LoginManager {
    private Scanner scanner;
    private User currentUser;
    private UserManager userManager;
    
    /**
     * Creates a new LoginManager.
     */
    public LoginManager() {
        scanner = new Scanner(System.in);
        currentUser = null;
        userManager = UserManager.getInstance();
    }
    
    /**
     * Starts the login/registration process.
     * 
     * @return The authenticated user if login/registration is successful, null otherwise
     */
    public User start() {
        boolean running = true;
        
        while (running) {
            displayMenu();
            int choice = getIntInput("Enter your choice: ");
            running = processMenuChoice(choice);
            
            if (currentUser != null) {
                return currentUser;
            }
        }
        
        return null;
    }
    
    /**
     * Processes the user's menu choice.
     * 
     * @param choice The user's menu choice
     * @return true to continue running, false to exit
     */
    private boolean processMenuChoice(int choice) {
        switch (choice) {
            case 1:
                currentUser = loginUser();
                return currentUser == null;
            case 2:
                currentUser = registerUser();
                return currentUser == null;
            case 0:
                return false;
            default:
                System.out.println("Invalid option. Please try again.");
                return true;
        }
    }
    
    /**
     * Displays the login/registration menu.
     */
    private void displayMenu() {
        System.out.println("\n===== BANKING SYSTEM =====");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("0. Exit");
        System.out.println("=========================");
    }
    
    /**
     * Handles the user login process.
     * 
     * @return The authenticated user if login is successful, null otherwise
     */
    private User loginUser() {
        System.out.println("\n----- LOGIN -----");
        String usernameText = getStringInput("Enter your username: ");
        String passwordText = getStringInput("Enter your password: ");
        
        User loggedInUser = userManager.login(usernameText, passwordText);
        displayLoginResult(loggedInUser != null);
        return loggedInUser;
    }
    
    /**
     * Displays the result of a login attempt.
     * 
     * @param successful Whether the login was successful
     */
    private void displayLoginResult(boolean successful) {
        if (successful) {
            System.out.println("Login successful!");
        } else {
            System.out.println("Invalid username or password.");
        }
    }
    
    /**
     * Handles the user registration process.
     * 
     * @return The newly created user if registration is successful, null otherwise
     */
    private User registerUser() {
        System.out.println("\n----- REGISTER -----");
        
        String usernameText = getValidUsername();
        if (usernameText == null) {
            return null;
        }
        
        String passwordText = getValidPassword();
        if (passwordText == null) {
            return null;
        }
        
        if (!confirmPassword(passwordText)) {
            System.out.println("Passwords do not match. Registration failed.");
            return null;
        }
        
        return createUserAccount(usernameText, passwordText);
    }
    
    /**
     * Gets a valid username from the user.
     * 
     * @return A valid username, or null if the process was canceled
     */
    private String getValidUsername() {
        System.out.println(Username.instance().getRequirements());
        
        String usernameText;
        boolean validUsername = false;
        
        while (!validUsername) {
            usernameText = getStringInput("Choose a username: ");
            
            if (userManager.usernameExists(usernameText)) {
                System.out.println("Username already exists. Please choose another one.");
                continue;
            }
            
            if (Username.instance().isValid(usernameText)) {
                return usernameText;
            } else {
                System.out.println("Invalid username. " + Username.instance().getRequirements());
            }
        }
        
        return null;
    }
    
    /**
     * Gets a valid password from the user.
     * 
     * @return A valid password, or null if the process was canceled
     */
    private String getValidPassword() {
        System.out.println(Password.instance().getRequirements());
        
        String passwordText;
        boolean validPassword = false;
        
        while (!validPassword) {
            passwordText = getStringInput("Choose a password: ");
            if (Password.instance().isValid(passwordText)) {
                return passwordText;
            } else {
                System.out.println("Invalid password.");
                System.out.println(Password.instance().getValidationErrors(passwordText));
            }
        }
        
        return null;
    }
    
    /**
     * Confirms the password with the user.
     * 
     * @param passwordText The password to confirm
     * @return true if the confirmation matches, false otherwise
     */
    private boolean confirmPassword(String passwordText) {
        String confirmPassword = getStringInput("Confirm your password: ");
        return passwordText.equals(confirmPassword);
    }
    
    /**
     * Creates a new user account.
     * 
     * @param usernameText The username for the new account
     * @param passwordText The password for the new account
     * @return The new user, or null if account creation failed
     */
    private User createUserAccount(String usernameText, String passwordText) {
        User newUser = userManager.createAccount(usernameText, passwordText);
        if (newUser != null) {
            System.out.println("Registration successful!");
        } else {
            System.out.println("Error registering user. Please try again.");
        }
        return newUser;
    }
    
    /**
     * Gets string input from the user.
     * 
     * @param prompt The prompt to display
     * @return The user's input
     */
    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
    
    /**
     * Gets integer input from the user.
     * 
     * @param prompt The prompt to display
     * @return The user's input as an integer
     */
    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = Integer.parseInt(scanner.nextLine());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
    
    /**
     * Closes the scanner.
     */
    public void close() {
        scanner.close();
    }
}