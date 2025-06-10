// Main application class: DigitalLockerApp.java
package com.digitallocker;

import com.digitallocker.model.FileMetadata;
import com.digitallocker.model.User;
import com.digitallocker.service.LockerService;
import com.digitallocker.util.PasswordHasher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

/**
 * Main application class for the Digital Locker System.
 * Provides a console-based user interface for interacting with the locker.
 */
public class DigitalLockerApp {

    private static final Scanner scanner = new Scanner(System.in);
    private static LockerService lockerService;
    private static User currentUser = null;

    // Defines the base directory for storing all application data.
    // This will contain user credentials and subdirectories for user files.
    private static final String DATA_DIR = "data";

    public static void main(String[] args) {
        // Initialize the LockerService with the data directory.
        // This ensures all file operations are relative to this base directory.
        lockerService = new LockerService(DATA_DIR);

        // Ensure the base data directory exists.
        // This is crucial for the application to store its data correctly.
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            System.err.println("Error creating data directory: " + e.getMessage());
            return; // Exit if the data directory cannot be created.
        }

        // Display the main menu and handle user interactions.
        while (true) {
            if (currentUser == null) {
                showLoginRegisterMenu();
            } else {
                showUserMenu();
            }
        }
    }

    /**
     * Displays the login and registration menu for unauthenticated users.
     * Allows users to log in or create a new account.
     */
    private static void showLoginRegisterMenu() {
        System.out.println("\n--- Digital Locker System ---");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Enter your choice: ");

        int choice = getIntegerInput();

        switch (choice) {
            case 1:
                loginUser();
                break;
            case 2:
                registerUser();
                break;
            case 3:
                System.out.println("Exiting Digital Locker. Goodbye!");
                scanner.close();
                System.exit(0);
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    /**
     * Handles the user login process.
     * Prompts for username and password, then attempts to authenticate.
     */
    private static void loginUser() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            // Attempt to authenticate the user using the LockerService.
            currentUser = lockerService.authenticateUser(username, password);
            if (currentUser != null) {
                System.out.println("Login successful! Welcome, " + currentUser.getUsername() + ".");
            } else {
                System.out.println("Invalid username or password.");
            }
        } catch (IOException e) {
            System.err.println("Error during login: " + e.getMessage());
            System.out.println("Please try again later.");
        }
    }

    /**
     * Handles the user registration process.
     * Prompts for a new username and password, then attempts to register.
     */
    private static void registerUser() {
        System.out.print("Enter desired username: ");
        String username = scanner.nextLine();
        System.out.print("Enter desired password: ");
        String password = scanner.nextLine();

        // Basic input validation for username and password.
        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            System.out.println("Username and password cannot be empty.");
            return;
        }

        try {
            // Attempt to register the new user.
            boolean registered = lockerService.registerUser(username, password);
            if (registered) {
                System.out.println("Registration successful! You can now log in.");
            } else {
                System.out.println("Username already exists. Please choose a different username.");
            }
        } catch (IOException e) {
            System.err.println("Error during registration: " + e.getMessage());
            System.out.println("Please try again later.");
        }
    }

    /**
     * Displays the menu for authenticated users.
     * Provides options for file operations and logging out.
     */
    private static void showUserMenu() {
        System.out.println("\n--- Welcome, " + currentUser.getUsername() + " ---");
        System.out.println("1. Upload File");
        System.out.println("2. Download File");
        System.out.println("3. List Files");
        System.out.println("4. Logout");
        System.out.print("Enter your choice: ");

        int choice = getIntegerInput();

        switch (choice) {
            case 1:
                uploadFile();
                break;
            case 2:
                downloadFile();
                break;
            case 3:
                listFiles();
                break;
            case 4:
                currentUser = null; // Log out the current user.
                System.out.println("Logged out successfully.");
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    /**
     * Handles the file upload process.
     * Prompts for the source file path and attempts to upload it to the locker.
     */
    private static void uploadFile() {
        System.out.print("Enter the full path of the file to upload: ");
        String sourceFilePath = scanner.nextLine();
        Path sourcePath = Paths.get(sourceFilePath);

        // Validate if the source file exists.
        if (!Files.exists(sourcePath) || !Files.isRegularFile(sourcePath)) {
            System.out.println("Error: Source file does not exist or is not a regular file.");
            return;
        }

        try {
            // Attempt to upload the file using the LockerService.
            lockerService.uploadFile(currentUser, sourcePath);
            System.out.println("File uploaded successfully!");
        } catch (IOException e) {
            System.err.println("Error uploading file: " + e.getMessage());
            System.out.println("Please ensure the file path is correct and you have read permissions.");
        }
    }

    /**
     * Handles the file download process.
     * Prompts for the file ID to download and the destination directory.
     */
    private static void downloadFile() {
        listFiles(); // Show available files first.
        System.out.print("Enter the File ID to download: ");
        String fileId = scanner.nextLine();

        System.out.print("Enter the destination directory for download: ");
        String destinationDir = scanner.nextLine();
        Path destinationPath = Paths.get(destinationDir);

        // Validate if the destination directory exists and is a directory.
        if (!Files.exists(destinationPath) || !Files.isDirectory(destinationPath)) {
            System.out.println("Error: Destination directory does not exist or is not a directory.");
            return;
        }

        try {
            // Attempt to download the file using the LockerService.
            lockerService.downloadFile(currentUser, fileId, destinationPath);
            System.out.println("File downloaded successfully!");
        } catch (IOException e) {
            System.err.println("Error downloading file: " + e.getMessage());
            System.out.println("Please check the File ID and ensure you have write permissions to the destination.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage()); // Specific error for file not found or unauthorized access.
        }
    }

    /**
     * Displays a list of files stored in the current user's locker.
     */
    private static void listFiles() {
        try {
            // Retrieve the list of files for the current user.
            List<FileMetadata> files = lockerService.listFiles(currentUser);
            if (files.isEmpty()) {
                System.out.println("Your locker is empty. No files to display.");
            } else {
                System.out.println("\n--- Your Stored Files ---");
                System.out.printf("%-5s %-30s %-20s %-15s%n", "ID", "Original Filename", "Upload Date", "Size (bytes)");
                System.out.println("-------------------------------------------------------------------------");
                for (FileMetadata file : files) {
                    System.out.printf("%-5s %-30s %-20s %-15d%n",
                            file.getId(), file.getOriginalFilename(), file.getUploadDate(), file.getFileSize());
                }
                System.out.println("-------------------------------------------------------------------------");
            }
        } catch (IOException e) {
            System.err.println("Error listing files: " + e.getMessage());
            System.out.println("Unable to retrieve file list at this time.");
        }
    }

    /**
     * Helper method to get integer input from the user, with error handling.
     *
     * @return The integer input, or -1 if the input is invalid.
     */
    private static int getIntegerInput() {
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next(); // Consume the invalid input.
            System.out.print("Enter your choice: ");
        }
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character left after nextInt().
        return choice;
    }
}
