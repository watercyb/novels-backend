# Cat Novels Backend

A backend service for Cat Novels, a platform for publishing, discovering, and sharing novels. Built with Java and Spring Boot, it provides REST APIs for content management, user authentication, search, and other application features.

## Features

- **User Authentication:** User authentication with access and refresh token management.
- **Role-Based Access Control:** Manage permissions and access to application resources based on user roles.
- **Novel Management:** Create, publish, update, and manage novel content.
- **Content Management:** Manage chapters and other novel-related content.
- **Search:** Search and retrieve novels with database indexing and query optimization.
- **Comments:** Support user comments and interactions.
- **Notifications:** Manage application notifications.
- **Caching:** Improve application performance using Redis caching.
- **REST APIs:** Provide backend services for the Cat Novels frontend.

## Technologies

- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- MySQL
- Redis
- REST APIs
- Git
- GitHub Actions
- CI/CD

## Technical Highlights

- Designed and implemented RESTful APIs using Spring Boot.
- Implemented authentication and access control with access and refresh tokens.
- Developed role-based authorization for protecting application resources.
- Designed relational database structures and optimized MySQL queries using indexing.
- Integrated Redis caching to improve data retrieval performance.
- Implemented backend services for novel publishing, content management, search, and notifications.
- Configured automated build and deployment workflows using GitHub Actions.

## Architecture

Cat Novels uses a frontend-backend architecture:

- **Frontend:** Web application for browsing, publishing, and managing novels.
- **Backend:** Spring Boot application providing REST APIs and business logic.
- **Database:** MySQL for persistent application data.
- **Cache:** Redis for caching and performance optimization.

## Requirements

- Java 21 or later.
- MySQL.
- Redis.
- Gradle.

## Related Projects

- [Cat Novels Frontend](https://github.com/watercyb/novels-frontend)

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.