# Digital-Locker-System
digital-locker/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── digitallocker/
│   │   │           ├── model/
│   │   │           │   ├── User.java
│   │   │           │   └── FileMetadata.java
│   │   │           ├── dao/
│   │   │           │   ├── UserDao.java
│   │   │           │   └── FileDao.java
│   │   │           ├── service/
│   │   │           │   └── LockerService.java
│   │   │           ├── util/
│   │   │           │   └── PasswordHasher.java
│   │   │           └── DigitalLockerApp.java (Main application class)
│   └── test/
│       └── java/
│           └── com/
│               └── digitallocker/
│                   └── ... (Unit tests - optional for initial phase)
├── data/
│   ├── users.txt         (Stores user credentials)
│   ├── [username]_files.txt (Stores metadata for each user's files)
│   └── [username]/       (Directory for actual user files)
├── README.md
├── .gitignore
└── pom.xml (if using Maven) / build.gradle (if using Gradle)
