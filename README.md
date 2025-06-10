# Digital-Locker-System (Console-Based)

Project Overview
The Digital Locker System is a secure, console-based application that allows users to store and retrieve their files with password protection and access control. Built using Java's core file handling capabilities, this system ensures that user files are securely stored in a designated, user-specific directory, and access is restricted to authenticated users only.

Key Features
User Registration & Authentication: Secure user account creation and login with password hashing (SHA-256) for enhanced security.
Secure File Storage: Upload and store files in dedicated directories for each user, preventing unauthorized access.
File Retrieval (Download): Easily download stored files back to your local machine.
File Listing: View a list of all files currently stored in your personal locker.
Robust Error Handling: Comprehensive input validation and error messages to ensure a smooth user experience and prevent unexpected crashes.
Data Persistence: All user credentials and file metadata are saved to text files, ensuring data is retained across application sessions.

Project Structure
The project follows a layered architecture to ensure modularity and maintainability:

digital-locker/ <br>
├── src/     <br>
│   └── main/  <br>
│       └── java/  <br>
│          └── com/ <br>
│               └── digitallocker/ <br>
│                   ├── model/ <br>
│                   │   ├── User.java <br>
│                   │   └── FileMetadata.java <br>
│                   ├── dao/ <br>
│                   │   ├── UserDao.java <br>
│                   │   └── FileDao.java <br>
│                   ├── service/ <br>
│                   │   └── LockerService.java <br>
│                   ├── util/ <br>
│                   │   └── PasswordHasher.java <br>
│                   └── DigitalLockerApp.java (Main application class) <br>
├── data/  <br>
│   ├── users.txt         (Stores user credentials) <br>
│   ├── [username]_files.txt (Stores metadata for each user's files) <br>
│   └── [username]/       (Directory for actual user files) <br>
├── README.md <br>
├── .gitignore <br>
└── pom.xml (if using Maven) / build.gradle (if using Gradle)    <br>


Getting Started
Follow these steps to set up and run the Digital Locker System on your local machine.

Prerequisites
Java Development Kit (JDK) 17 or higher: Ensure JDK 17 (or a compatible version like 11, 21) is installed and configured in your system's PATH.
Maven or Gradle: You can choose either Maven or Gradle as your build tool. This README.md provides instructions for both.
Setup and Running the Project
Option 1: Using Gradle (Recommended)
Clone the repository:Bash 
git clone https://github.com/your-username/digital-locker.git
cd digital-locker

Build the project:Bash 
./gradlew build

(On Windows, use gradlew.bat build)
Run the application:Bash 
./gradlew run

(On Windows, use gradlew.bat run)
Option 2: Using Maven
Clone the repository:Bash 
git clone https://github.com/your-username/digital-locker.git
cd digital-locker

Build the project:Bash 
mvn clean install

Run the application (Fat JAR): After building, a "fat JAR" (with all dependencies included) will be created in the target/ directory.Bash 
java -jar target/DigitalLockerSystem-1.0-SNAPSHOT-jar-with-dependencies.jar


How to Use the Digital Locker
Once the application is running, you'll see a console menu:

Register:

Choose option 2 to register a new account.
Enter your desired username and password.
A new user account will be created, and a dedicated directory for your files will be set up in the data/ folder.
Login:

Choose option 1 to log in with an existing account.
Enter your username and password.
Upon successful login, you'll be presented with the user menu.
User Menu Options:

Upload File: Enter the full path to the file on your computer you wish to store. The system will copy it into your secure locker space.
Download File: First, list your files to get the File ID. Then, provide the File ID and the desired local directory where you want to save the downloaded file.
List Files: View a table of all files you've stored in your locker, including their ID, original filename, upload date, and size.
Logout: Exits your current session and returns to the main login/register menu.

Error Handling and Robustness
The system implements robust error handling for various scenarios:

Input Validation: Ensures that user inputs (like usernames, passwords, file paths) are valid and prevents empty or malformed entries.
File I/O Errors: Gracefully handles exceptions related to file operations, such as files not found, permission issues, or disk errors.
Informative Messages: Provides clear feedback and error messages to the user for better understanding and troubleshooting.

Future Enhancements (Potential Improvements)
1.  File Encryption: Implement stronger encryption (e.g., AES) for files to enhance data security at rest.
2.  Deletion of Files: Add functionality to delete files from the locker.
3.  Renaming/Updating Files: Allow users to rename stored files or update their metadata.
4.  Search Functionality: Enable searching for files within the locker.
5.  GUI Interface: Develop a graphical user interface for a more user-friendly experience.
6.  More Robust Password Hashing: Utilize libraries like Spring Security Crypto or Argon2 for industry-standard password hashing with proper salting.




