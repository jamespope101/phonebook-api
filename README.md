# README #

# Phonebook REST API

This Spring Boot application provides a REST API for managing a phonebook. Frameworks and libraries used include:
- Spring for dependency injection
- Spring security for authorisation using Basic Authentication headers
- Hibernate for ORM
- Jackson for JSON serialisation
- H2 for in-memory database (intended use MySQL in production environment)
- Flyway for database table initialisation
- Jersey for RESTful API services

The application logic is split into three separate layers, and components residing in these layers have unit tests:
- A repository layer for retrieval and persistence of data access objects.
- A service layer to enforce business-defined rules and error-handling.
- A resource layer which defines the REST API paths and requests.

There are also functional tests in FunctionalTests, which spins up a local application with some pre-initialised data and asserts on 
the responses to various requests against the API.

## Getting Started

### Prerequisites
* Java 8 JDK is required. Gradle installation is controlled by the gradle wrapper.
* This service uses Lombok annotations to reduce boilerplate code, there may be a compilation issue in your IDE unless you 'Enable Annotation Processing' in your Preferences.

### Build the Application
`./gradlew clean build`

### Running the App Locally
The app can be run locally by running ApplicationBoot.main() with the following VM options in an IntelliJ run configuration:

`-Dspring.profiles.active=LOCAL`

This will run the application with the default configuration properties in `src/main/resources/phonebook-api.yml`, as well as overrides for the local profile
stored in `src/main/resources/phonebook-api-local.yml`.

## Testing
All unit/functional tests will be run as part of the build task.
