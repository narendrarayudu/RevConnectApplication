## RevConnect - Social Networking Console Application
### 📝 Application Overview

RevConnect is a feature-rich social networking console application built with Java and MySQL. It enables users to connect, share content, and interact in a secure environment. The application implements modern social media features including user profiles, posts, comments, likes, follows, connection requests, and real-time notifications.

### ⚙️ Core Functional Features
### 👤 User Management
- Create an account with secure authentication

- User login/logout with session management

- Profile creation and management

- Privacy settings (Public/Private profiles)

- User types: Personal, Creator, Business

### 📱 Social Features
- Post Creation: Create text posts with hashtags and scheduling

- Content Interaction: Like and comment on posts

- Social Connections: Send/accept/reject connection requests

- Follow System: Follow/unfollow other users

- Timeline View: Personalized content feed

### 🔔 Notification System
- Real-time notifications for social interactions

- Notification types: Likes, Comments, Follows, Connection requests

- Read/unread status tracking

### 🔍 Search & Discovery
- Search users by username

- View user profiles and posts

- Explore connections and followers

### ✅ Standard Functional Scope
#### Registered users can:
- Create and manage their profile

- Post content with hashtags and scheduling

- Interact with other users' content

- Manage social connections

- Send and respond to connection requests

- Follow/unfollow other users

- Receive and manage notifications

- Control privacy settings

### 💻 Environment / Technologies
- Programming Language: Java 11+

- Database: MySQL 8.0+

- Database Connectivity: JDBC

- Build Tool: Maven

- Logging: Log4J 2.x

- Testing: JUnit 5

- Version Control: Git

### 🚀 Getting Started
#### Prerequisites
- Java Development Kit (JDK) 11 or higher

- MySQL Server 8.0 or higher

- Maven 3.6 or higher

```Git

Installation Steps
Clone the repository:
```

```bash
git clone https://github.com/yourusername/revconnect.git
cd revconnect
```
### Set up the database:

```bash
mysql -u root -p
```

```sql
CREATE DATABASE revconnect_db;
USE revconnect_db;
-- Run the schema.sql file from the resources folder
SOURCE src/main/resources/schema.sql;
```
### Configure database connection:
```text Edit src/main/resources/application.properties:
```

```properties
# Database Configuration
db.url=jdbc:mysql://localhost:3306/revconnect_db
db.username=your_username
db.password=your_password
db.driver=com.mysql.cj.jdbc.Driver

# Application Settings
app.name=RevConnect
app.version=1.0.0
```

Build the project:

```bash
mvn clean install
```

### Run the application:

```bash
mvn exec:java -Dexec.mainClass="com.revconnectapp.App"
```

### Run tests:
```bash
mvn test
```

### 📂 Project Structure
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

### 🗄️ Database Schema
Core Tables:
- users - User authentication and basic info

- profiles - Extended user profile information

- posts - User-generated content

- comments - Comments on posts

- likes - Post likes (composite key)

- follows - Follow relationships

- connections - Connection requests with status

- notifications - System notifications

### Key Relationships:
- User 1:1 Profile - Each user has one profile

- User 1:N Posts - Users create multiple posts

- Post 1:N Comments - Posts receive multiple comments

- User M:N Follows - Users follow multiple users

- User M:N Connections - Users connect with multiple users

- Post M:N Likes - Posts liked by multiple users

### 🔐 Authentication Security
- Password hashing using SHA-256 with salt

- Secure session management

- Account lockout protection

- Input validation and sanitization

### 🛡️ Data Protection
- SQL injection prevention

- XSS protection

- Secure database connections

- Encrypted sensitive data

### 👁️ Privacy Controls
- User privacy settings (Public/Private)

- Profile visibility controls

- Connection approval system

- Data access authorization

### 🧪 Testing

#Running Tests
```bash
# Run all tests
mvn test
```
###  Run specific test class
```bash
mvn test -Dtest=UserServiceTest
```

###  Generate test coverage report
```bash
mvn jacoco:report
```

### Test Coverage
- Unit tests for all service classes

- Integration tests for DAO layer

- Database transaction tests

- Boundary condition tests

### 📊 Performance Considerations
### Database Optimization
- Proper indexing on frequently queried columns

- Connection pooling for database access

- Efficient query design

- Caching strategies for frequently accessed data

### Application Performance
- Efficient algorithm design

- Memory management best practices

- Thread-safe operations

- Resource cleanup and management

### 🐛 Troubleshooting
#### Common Issues:
- Database Connection Failed

- Verify MySQL service is running

- Check database credentials in application.properties

- Ensure database schema is created

### Build Failures

- Clean Maven cache: mvn clean

- Update dependencies: mvn clean install -U

- Check Java version compatibility

- Application Crashes

- Check log files in logs/ directory

- Verify database connectivity

- Check memory allocation

### Logs:
- Application logs: logs/revconnect-app.log

- Error logs: logs/revconnect-error.log

- Database logs: logs/revconnect-db.log

### 📈 Future Enhancements
#### Planned Features:
- Direct Messaging - Private chat between users

- Groups - User groups and communities

- Media Uploads - Image and file sharing

- Advanced Search - Enhanced search capabilities

- Analytics Dashboard - User activity analytics

- Mobile App - Cross-platform mobile application

#### Technical Improvements:
- Microservices Architecture - Scalable service decomposition

- Redis Caching - Performance optimization

Elasticsearch Integration - Advanced search capabilities

- Docker Containerization - Easy deployment

- CI/CD Pipeline - Automated testing and deployment

### 🤝 Contributing
- We welcome contributions! Please follow these steps:

- Fork the repository

- Create a feature branch: git checkout -b feature/YourFeature

- Commit your changes: git commit -m 'Add YourFeature'

- Push to the branch: git push origin feature/YourFeature

- Open a Pull Request
