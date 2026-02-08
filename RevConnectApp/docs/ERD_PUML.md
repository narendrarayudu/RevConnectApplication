## Module 1: User Management Module
### Purpose: Handles user authentication, registration, profile management, and account operations.

```text
UserManagement/
├── UserService.java        // User authentication and profile logic
├── UserDAO.java           // CRUD operations for User entity
├── User.java              // User entity model
├── ProfileService.java    // Profile management logic
├── ProfileDAO.java        // CRUD operations for Profile entity
└── Profile.java           // Profile entity model
```

## Module 2: Content Management Module
### Purpose: Manages posts, comments, likes, and content interactions.

```text
ContentManagement/
├── PostService.java       // Business logic for post operations
├── PostDAO.java          // DAO for CRUD operations on posts
├── Post.java             // Post entity model
├── CommentService.java   // Comment management logic
├── CommentDAO.java       // DAO for comment operations
├── Comment.java          // Comment entity model
├── LikeService.java      // Like/unlike logic
└── LikeDAO.java          // Like operations
```

## Module 3: Social Network Module
### Purpose: Handles social connections, follows, and relationship management.

```text
SocialNetwork/
├── ConnectionService.java  // Connection request/accept logic
├── ConnectionDAO.java     // Connection CRUD operations
├── Connection.java        // Connection entity model
├── FollowService.java     // Follow/unfollow logic
├── FollowDAO.java        // Follow operations
└── Follow.java           // Follow entity model
```

## Module 4: Notification Module
### Purpose: Manages system notifications and alerts.

```text
Notification/
├── NotificationService.java  // Notification generation logic
├── NotificationDAO.java     // Notification CRUD operations
└── Notification.java        // Notification entity model
```

## Module 5: Utility Module
### Purpose: Provides helper functionalities and shared utilities across the application.

```text
Utils/
├── DBConnection.java      // Database connection management
├── InputValidator.java    // Validates input formats and constraints
├── LoggerUtil.java        // Logging wrapper (Log4J)
├── DateUtil.java          // Date/time utilities
└── HashUtil.java          // Hashing and security utilities
```

## 📐 CLASS DIAGRAM (Simplified)
The class structure follows a layered and dependency-driven design, where controllers delegate to services, services interact with DAOs, and DAOs communicate with the database.

Dependency Flow:

```bash
    MainApplication → MainMenu → Services → DAOs → DatabaseUtil → MySQL
```

##  Interaction Flow:
```text
┌──────────────────────────────────────────────────────┐
│               RevConnect Application                 │
├──────────────────────────────────────────────────────┤
│  ┌──────────────┐    ┌──────────────┐                │
│  │ InputHandler │───▶│ OutputHandler│                │
│  └──────────────┘    └──────────────┘                │
│           │                    │                     │
│  ┌────────▼────────────────────▼──────────────────┐  │
│  │            Business Logic Layer                │  │
│  │  ┌──────────────┐  ┌──────────────┐            │  │
│  │  │ Authentication│  │ Content      │            │  │
│  │  │ Module        │  │ Management   │            │  │
│  │  └──────────────┘  └──────────────┘            │  │ 
│  │  ┌──────────────┐  ┌──────────────┐            │  │
│  │  │ Social       │  │ Notification │            │  │
│  │  │ Network      │  │ Module       │            │  │
│  │  └──────────────┘  └──────────────┘            │  │
│  └───────────────────────┬────────────────────────┘  │
│                          │                           │
│  ┌───────────────────────▼────────────────────────┐  │
│  │               Data Access Layer                │  │
│  │  ┌──────────────────────────────────────────┐  │  │
│  │  │ UserDAO | PostDAO | ConnectionDAO | ...  │  │  │
│  │  └──────────────────────────────────────────┘  │  │
│  └───────────────────────┬────────────────────────┘  │
│                          │                           │
└──────────────────────────│───────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────┐
│                 External Systems                    │
├─────────────────────────────────────────────────────┤
│  ┌──────────────┐        ┌──────────────────────┐   │
│  │  MySQL DB    │        │ File System (Logs)   │   │
│  └──────────────┘        └──────────────────────┘   │
└─────────────────────────────────────────────────────┘
```

### Deployment Setup:
```text
┌──────────────────────────────────────────────────┐
│           Development Environment                │
├──────────────────────────────────────────────────┤
│  ┌─────────┐    ┌─────────┐    ┌─────────┐       │
│  │ IntelliJ│───▶│  Maven  │───▶│   JVM   │       │ 
│  │  IDEA   │    │  Build  │    │ Runtime │       │
│  └─────────┘    └─────────┘    └─────────┘       │
│                      │                           │
│  ┌───────────────────▼────────────────────────┐  │
│  │         RevConnect Application (JAR)       │  │
│  └───────────────────┬────────────────────────┘  │
│                      │                           │
│  ┌───────────────────▼────────────────────────┐  │
│  │           MySQL Database (Local)           │  │
│  │     ┌───────────────────────────────────┐  │  │
│  │     │       revconnect_db               │  │  │
│  │     │   • users                         │  │  │
│  │     │   • posts                         │  │  │
│  │     │   • connections                   │  │  │
│  │     │   • notifications                 │  │  │
│  │     │   • profiles                      │  │  │
│  │     │   • comments                      │  │  │
│  │     │   • follows                       │  │  │
│  │     │   • likes                         │  │  │
│  │     └───────────────────────────────────┘  │  │
│  └────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────┘
```

### Security Layers Implementation:

```text
┌─────────────────────────────────────────────────┐
│              Security Architecture              │
├─────────────────────────────────────────────────┤
│                                                 │
│ 🔒 Layer 1: Input Validation                   │
│ • Email format validation                       │
│ • Strong password rules (min 8 chars, mix)      │
│ • Length & character checks                     │
│ • SQL injection prevention                      │
│ • XSS prevention                                │
│                                                 │
│ 🔑 Layer 2: Authentication                     │
│ • Password hashing (SHA-256 + Salt)             │
│ • Secure login flow with session tracking       │
│ • Account lockout after failed attempts         │
│ • Secure password reset with tokens             │
│ • Two-factor authentication (future)            │
│                                                 │
│ 🛡️ Layer 3: Authorization                      │
│ • User-specific data access control             │
│ • Ownership validation before CRUD operations   │
│ • Role-based permissions (PERSONAL/CREATOR/     │
│   BUSINESS user types)                          │
│ • Sensitive action verification                 │
│ • Privacy settings enforcement                  │
│                                                 │
│ 🔐 Layer 4: Data Protection                    │
│ • Database-level encryption                     │
│ • Sensitive data masking in logs                │
│ • Secure data transmission                      │
│ • Regular encrypted backups                     │
│ • Data retention and deletion policies          │
│                                                 │
│ 🧾 Layer 5: Auditing & Logging                 │
│ • Login attempt logging with IP tracking        │
│ • Error & exception tracking (Log4J)            │
│ • Security event monitoring                     │
│ • User activity audit trails                    │
│ • Compliance and reporting                      │
│                                                 │
└─────────────────────────────────────────────────┘
```

 ## 📦 PROJECT STRUCTURE
```text
RevConnectApp/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/revconnectapp/
│   │   │       ├── App.java                          # Main application entry point
│   │   │       ├── DBTest.java                       # Database connection testing
│   │   │       ├── NotificationTest.java             # Notification system testing
│   │   │       └── TestLogging.java                  # Logging system testing
│   │   │
│   │   │       ├── dao/                              # Data Access Objects Layer
│   │   │       │   ├── CommentDAO.java              # CRUD operations for comments
│   │   │       │   ├── ConnectionDAO.java           # Connection request management
│   │   │       │   ├── FollowDAO.java               # Follow relationship operations
│   │   │       │   ├── LikeDAO.java                 # Like/unlike operations
│   │   │       │   ├── NotificationDAO.java         # Notification storage/retrieval
│   │   │       │   ├── PostDAO.java                 # Post CRUD operations
│   │   │       │   ├── ProfileDAO.java              # Profile management operations
│   │   │       │   └── UserDAO.java                 # User authentication and data
│   │   │
│   │   │       ├── model/                            # Entity Models Layer
│   │   │       │   ├── Comment.java                 # Comment entity class
│   │   │       │   ├── Connection.java              # Connection entity with status
│   │   │       │   ├── Like.java                    # Like entity (composite key)
│   │   │       │   ├── Notification.java            # Notification entity
│   │   │       │   ├── Post.java                    # Post entity with hashtags
│   │   │       │   ├── Profile.java                 # Profile entity
│   │   │       │   └── User.java                    # User entity
│   │   │
│   │   │       ├── service/                          # Business Logic Layer
│   │   │       │   ├── CommentService.java          # Comment business logic
│   │   │       │   ├── ConnectionService.java       # Connection management logic
│   │   │       │   ├── FollowService.java           # Follow relationship logic
│   │   │       │   ├── LikeService.java             # Like/unlike business logic
│   │   │       │   ├── NotificationService.java     # Notification generation logic
│   │   │       │   ├── PostService.java             # Post business logic
│   │   │       │   ├── ProfileService.java          # Profile management logic
│   │   │       │   └── UserService.java             # User authentication logic
│   │   │
│   │   │       ├── ui/                               # User Interface Layer
│   │   │       │   ├── MainMenu.java                # Main navigation controller
│   │   │       │   ├── NotificationMenu.java        # Notification management UI
│   │   │       │   └── ProfileMenu.java             # Profile management UI
│   │   │
│   │   │       └── util/                             # Utility Classes
│   │   │           ├── ConnectionUtil.java          # Database connection utilities
│   │   │           ├── InputUtil.java               # Input validation and parsing
│   │   │           ├── LoggerUtil.java              # Logging wrapper
│   │   │           └── LogUtil.java                 # Additional logging utilities
│   │   │
│   │   └── resources/                                # Configuration Resources
│   │       └── log4j2.xml                           # Log4J2 configuration
│   │
│   └── test/                                         # Test Directory
│       ├── java/
│       │   └── com/revconnectapp/
│       │       ├── DatabaseConnectionTest.java      # Database connectivity tests
│       │       ├── LikeIntegrationTest.java         # Like system integration tests
│       │       ├── NotificationValidationTest.java  # Notification validation tests
│       │       ├── PostAndCommentTest.java          # Post-comment integration tests
│       │       ├── ProfileIntegrationTest.java      # Profile system integration tests
│       │       ├── ProfileValidationTest.java       # Profile validation tests
│       │       └── UserRegistrationLoginTest.java   # User auth integration tests
│       │
│       │       └── util/                            # Utility Tests
│       │           ├── FileLoggingTest.java         # File logging tests
│       │           └── LogTest.java                 # Logging system tests
│       │
│       └── resources/                               # Test Resources
│           └── log4j2-test.xml                      # Test logging configuration
│
├── logs/                                            # Application Logs Directory
│   ├── app.log                                      # General application logs
│   ├── error.log                                    # Error logs
│   ├── revconnect-app.log                          # RevConnect application logs
│   ├── revconnect-db.log                           # Database operation logs
│   ├── revconnect-error.log                        # RevConnect error logs
│   └── test-results.log                            # Test execution logs
│
├── target/                                          # Maven Build Output
│   ├── classes/                                     # Compiled Java classes
│   ├── test-classes/                                # Compiled test classes
│   ├── maven-status/                               # Maven build status
│   ├── surefire-reports/                           # Test reports
│   └── revconnectapp-1.0-SNAPSHOT.jar             # Executable JAR
│
├── pom.xml                                         # Maven Configuration File
│   ├── Project Metadata
│   ├── Dependencies
│   │   ├── mysql-connector-j:8.0.33
│   │   ├── log4j-core:2.23.1
│   │   ├── log4j-api:2.20.0
│   │   ├── junit:4.13.2
│   │   └── junit-jupiter:5.10.2
│   ├── Build Configuration
│   └── Plugin Management
│
├── README.md                                       # Main project documentation
├── REVCONNECT_ARCHITECTURE.md                     # Architecture documentation
├── REVCONNECT_ERD.md                              # Entity Relationship Diagram
└── .gitignore                                     # Git ignore file
```
