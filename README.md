# Digital-Locker-System
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
│   
├── data/
│   ├── users.txt         (Stores user credentials) <br>
│   ├── [username]_files.txt (Stores metadata for each user's files) <br>
│   └── [username]/       (Directory for actual user files) <br>
├── README.md <br>
├── .gitignore <br>
└── pom.xml (if using Maven) / build.gradle (if using Gradle)    <br>
