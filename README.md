# Hi there!!! USERS_API is welcoming you!

# USERS API README

## Introduction

Welcome to USERS API made using Spring Boot.
[See the example of requests to some endpoints by watching this video](https://www.loom.com/share/cbd5d2ebafda45a38d57762ea12b0d41?sid=2173edcc-05f7-40aa-94e0-0dc06b8091f9)

## Properties in .env file are mocked, use yours (or you can use these pointed, but put your Google and GitHub properties for OAuth2).

## Technologies used

- **Spring Boot (v3.2.2):** A super-powerful framework for creating Java-based applications (just like this one).
- **Spring Security:** Ensures application security with features such as authentication and authorization.
- **JWT (JSON Web Token):** Ensures secure user authentication.
- **Spring Data JPA:** Simplifies the data access layer and interactions with the database.
- **Swagger (springdoc-openapi):** Eases understanding and interaction with endpoints for other developers.
- **MapStruct (v1.5.5.Final):** Simplifies the implementation of mappings between Java bean types.
- **Liquibase:** A powerful way to ensure database-independence for project and database schema changes and control.
- **Docker:** A powerful tool for letting other developers use this application.

## Project structure

This Spring Boot application follows the most common structure with such **main layers** as:
- repository (for working with database).
- service (for business logic implementation).
- controller (for accepting clients' requests and getting responses to them).

Also it has other **important layers** such as:
- mapper (for converting models for different purposes).
- security (for letting user authorize and be secured while interacting with application).
- exception (CustomGlobalExceptionHandler for getting proper messages about errors).
- dto (for managing sensitive info about models and better representation of it).
- config (config for mappers and OpenApi config).

## Key features

- **User authentication:** Secure user authentication using JWT for enhanced security.
- **User authorization:** Limited access to some endpoints of the application.
- **User login:** Login by email and password for generating JWT token.
- **API Documentation:** Using Swagger to generate clear and interactive API documentation.

## Setup Instructions

To set up and run the project locally, follow these steps:

1. Clone the repository.
2. Ensure you have Java 21 installed.
3. Ensure you have Maven installed.
4. Ensure you have Docker installed.
5. Create the database configuration in the `.env` file. (put your cliend.id and client-secret for Oauth2)
6. Build the project using Maven: `mvn clean package` (it will create required jar-archive).
7. Build the image using Docker: `docker-compose build`.
8. Run the application using Docker: `docker-compose up` (to test, send requests to port pointed in your .env file as SPRING_LOCAL_PORT).

## Roles explanation

- There are only 2 roles of users available: **USER and ADMIN roles**.
- User has access to such endpoints as getting or updating their info.
- But user doesn't get to update roles of other users (and similar actions that require some permission). **(Remember it)**

## Users managing

- There is 1 user added to a database with help of liquibase.
- This user is already an admin.
- Credentials: email: admin@example.com, password: 1234567890.
- Using this admin credentials you can update roles of other users.
