# Smart Buddy

This folder contains the Spring Boot backend for the Smart Buddy travel application.

# Folder Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── hkust/
│   │           └── smart_buddy/
│   │               ├── SmartBuddyApplication.java
│   │               ├── common/
│   │               │   └── domain/
│   │               │       └── ...
│   │               │   └── dto/
│   │               │       └── ...
│   │               │   └── util/
│   │               │       └── ...
│   │               └── auth/
│   │                   ├── controller/
│   │                   │   └── ...
│   │                   ├── domain/
│   │                   │   └── ...
│   │                   ├── dto/
│   │                   │   └── ...
│   │                   ├── exception/
│   │                   │   └── ...
│   │                   ├── repository/
│   │                   │   └── ...
│   │                   └── service/
│   │                       └── ...
│   │               └── ...
│   └── resources/
│       ├── application.properties
│       ├── data.sql
│       └── schema.sql
│       └── ...
├── test/
│   └── java/
│       └── com/
│           └── hkust/
│               └── smart_buddy/
│                   └── ...
```

# Getting Started

1. Set up the MySQL database and create a database named `smart_buddy`.
2. Run the schema.sql file to create the necessary tables.
3. Run the data.sql file to insert the initial data.
4. Provide the password for the database in the `application.properties` file.
5. Install JDK 21.
   - Ensure that the JAVA_HOME environment variable is set correctly.
6. install the plugin for development:
   - Lombok
   - JPA Buddy
   - SonarQube
7. Run the SmartBuddyApplication.java file to start the application.