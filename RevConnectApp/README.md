## 📝 Application Overview
### RevConnect is a feature-rich social networking console application built with Java and MySQL. It enables users to connect, share content, and interact in a secure environment. The application implements modern social media features including user profiles, posts, comments, likes, follows, connection requests, and real-time notifications.

### ⚙️ Core Functional Features
### 👤 User Management
Create an account with secure authentication

User login/logout with session management

Profile creation and management

Privacy settings (Public/Private profiles)

User types: Personal, Creator, Business

### 📱 Social Features
Post Creation: Create text posts with hashtags and scheduling

Content Interaction: Like and comment on posts

Social Connections: Send/accept/reject connection requests

Follow System: Follow/unfollow other users

Timeline View: Personalized content feed

### 🔔 Notification System
Real-time notifications for social interactions

Notification types: Likes, Comments, Follows, Connection requests

Read/unread status tracking

### 🔍 Search & Discovery
Search users by username

View user profiles and posts

Explore connections and followers

### ✅ Standard Functional Scope
Registered users can:
Create and manage their profile

Post content with hashtags and scheduling

Interact with other users' content

Manage social connections

Send and respond to connection requests

Follow/unfollow other users

Receive and manage notifications

Control privacy settings

💻 Environment / Technologies
Programming Language: Java 11+

Database: MySQL 8.0+

Database Connectivity: JDBC

Build Tool: Maven

Logging: Log4J 2.x

Testing: JUnit 5

Version Control: Git

### 🚀 Getting Started
Prerequisites
Java Development Kit (JDK) 11 or higher

MySQL Server 8.0 or higher

Maven 3.6 or higher

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
│   │   │       ├── App.java                    # Main application entry point
│   │   │       ├── DBTest.java                 # Database connection testing
│   │   │       └── NotificationTest.java       # Notification system testing
│   │   │
│   │   │       ├── dao/                        # Data Access Objects
│   │   │       │   ├── CommentDAO.java         # Comment data operations
│   │   │       │   ├── ConnectionDAO.java      # Connection management
│   │   │       │   ├── FollowDAO.java          # Follow relationships
│   │   │       │   ├── LikeDAO.java            # Like operations
│   │   │       │   ├── NotificationDAO.java    # Notification handling
│   │   │       │   ├── PostDAO.java            # Post CRUD operations
│   │   │       │   ├── ProfileDAO.java         # Profile management
│   │   │       │   └── UserDAO.java            # User authentication and data
│   │   │
│   │   │       ├── model/                      # Entity Models
│   │   │       │   ├── Comment.java            # Comment entity
│   │   │       │   ├── Connection.java         # Connection entity
│   │   │       │   ├── Follow.java             # Follow relationship
│   │   │       │   ├── Like.java               # Like entity (composite key)
│   │   │       │   ├── Notification.java       # Notification entity
│   │   │       │   ├── Post.java               # Post entity
│   │   │       │   ├── Profile.java            # Profile entity
│   │   │       │   └── User.java               # User entity
│   │   │
│   │   │       ├── repository/                 # Repository interfaces (optional)
│   │   │
│   │   │       ├── service/                    # Business Logic Layer
│   │   │       │   ├── UserService.java        # User management logic
│   │   │       │   ├── PostService.java        # Post operations logic
│   │   │       │   ├── ConnectionService.java  # Connection management logic
│   │   │       │   ├── NotificationService.java # Notification logic
│   │   │       │   └── FeedService.java        # Timeline and feed logic
│   │   │
│   │   │       ├── ui/                         # User Interface Layer
│   │   │       │   ├── MainMenu.java           # Main navigation menu
│   │   │       │   ├── NotificationMenu.java   # Notification management
│   │   │       │   └── ProfileMenu.java        # Profile management
│   │   │
│   │   │       └── util/                       # Utility Classes
│   │   │           ├── DBConnection.java       # Database connection pool
│   │   │           ├── DateUtil.java           # Date/time utilities
│   │   │           ├── HashUtil.java           # Password hashing
│   │   │           ├── InputValidator.java     # Input validation
│   │   │           └── LoggerUtil.java         # Logging wrapper
│   │   │
│   │   └── resources/                          # Configuration Files
│   │       ├── application.properties          # Application configuration
│   │       ├── log4j2.properties               # Logging configuration
│   │       └── schema.sql                      # Database schema
│   │
│   └── test/                                   # Test Classes
│       └── java/com/revconnectapp/
│           ├── UserServiceTest.java
│           ├── PostServiceTest.java
│           ├── ConnectionServiceTest.java
│           └── NotificationServiceTest.java
│
├── target/                                     # Compiled output
├── pom.xml                                     # Maven configuration
├── README.md                                   # This file
├── REVCONNECT_ARCHITECTURE.md                  # Architecture documentation
├── REVCONNECT_ERD.md                           # Entity Relationship Diagram
└── .gitignore                                  # Git ignore file
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

Key Relationships:
User 1:1 Profile - Each user has one profile

User 1:N Posts - Users create multiple posts

Post 1:N Comments - Posts receive multiple comments

User M:N Follows - Users follow multiple users

User M:N Connections - Users connect with multiple users

Post M:N Likes - Posts liked by multiple users

### 🔐 Authentication Security
- Password hashing using SHA-256 with salt

- Secure session management

Account lockout protection

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
