# Medfast Backend

Backend repository for MedFast project

<!-- TOC -->
* [Medfast Backend](#medfast-backend)
  * [Technological Stack](#technological-stack)
  * [Getting Started Locally](#getting-started-locally)
    * [Installation](#installation)
    * [Running the project](#running-the-project)
      * [Via Docker](#via-docker)
        * [Prerequisites](#prerequisites)
        * [Steps](#steps)
      * [With Docker Compose for PostgreSQL and ELK stack only](#with-docker-compose-for-postgresql-and-elk-stack-only)
        * [Prerequisites](#prerequisites-1)
        * [Steps](#steps-1)
    * [Coding Style](#coding-style)
    * [Code coverage with tests](#code-coverage-with-tests)
      * [How to generate reports](#how-to-generate-reports)
      * [Coverage indicators](#coverage-indicators)
      * [JaCoCo configuration](#jacoco-configuration)
    * [Logging](#logging)
<!-- TOC -->

## Technological Stack

- Java 17
- Gradle
- PostgreSQL
- Spring Boot
- Spring Security (JWT)
- Spring Data JPA
- Spring Cloud Stream for RabbitMQ
- Spring Testing
- Liquibase
- Docker / Docker Compose
- Swagger
- Thymeleaf

## Getting Started Locally

### Installation

Clone the repository

```bash
git clone https://github.com/medfastdev/medfast-be.git
```

### Running the project

We can run either of `prod` (will pull the latest `main` image from docker hub) or `dev` (will build
image from the local version) docker compose files.

#### Via Docker

##### Prerequisites

- Docker / Docker Compose

##### Steps

- `prod`

```bash
docker compose up
```

- `dev`

```bash
docker compose up --build
```

> [!IMPORTANT]
> Execute these commands in the root directory of the project

> [!IMPORTANT]
> If you are running the `prod` version, please make sure that you have logged in to the docker hub
> beforehand
> ```bash
> docker login docker.io -u healflowdev
> ```
> Shortly after executing the command above, you will be prompted to enter the password, it can be
> found
> in [the Medfast docs](https://docs.google.com/document/d/16I_MUle7IBE3wN9GDAVzZhlh00B0DSXia3hpzNTSyT8/edit#heading=h.9l37u1xea78s)

#### With Docker Compose for PostgreSQL and ELK stack only

##### Prerequisites

- Java 17
- Make sure you have PostgreSQL and ELK stack up and running using docker compose file:
    ```bash
    docker compose up postgresql elasticsearch logstash kibana 
    ```
- Environment variable:
    - `MAIL_PASSWORD=medfast_email_password`

> [!IMPORTANT]
> The `medfast_email_password` can be found
> in [the Medfast docs](https://docs.google.com/document/d/16I_MUle7IBE3wN9GDAVzZhlh00B0DSXia3hpzNTSyT8/edit#heading=h.9l37u1xea78s)

> [!IMPORTANT]
> How to set environment
> variables: [Windows](https://www.architectryan.com/2018/08/31/how-to-change-environment-variables-on-windows-10/),
> [macOS and Linux](https://phoenixnap.com/kb/set-environment-variable-mac)

##### Steps

Run the following command in the root directory of the project

```bash
./gradlew bootRun
```

### Coding Style

We are following the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) for
this project. Please make sure to follow the guidelines. They are enforced by the `checkstyle`
plugin.

It's recommended to install
the [CheckStyle-IDEA](https://plugins.jetbrains.com/plugin/1065-checkstyle-idea) plugin for IntelliJ
IDEA to see the style violations in real-time. To run the checkstyle task, open the CheckStyle tab
and run the `Check Project` task.

Another checkstyle-related recommendation would be to turn on real-time checking and set the
inspection severity to `Error` in
the `Preferences` -> `Editor` -> `Inspections` -> `Checkstyle` -> `Checkstyle real-time scan` ->
`Severity` -> `Error`.

### Code coverage with tests

For code coverage checks we will
use
JaCoCo. [JaCoCo - Java Code Coverage Library](https://www.jacoco.org/jacoco/trunk/index.html#:~:text=JaCoCo%20is%20a%20free%20Java,under%20the%20Eclipse%20Public%20License.)
is a widely used tool that provides detailed reports on code coverage, helping us identify untested
parts of our codebase.

#### How to generate reports

1. Build the project:

    ```bash
    ./gradlew build
    ```

2. Move to Jacoco folder and open `build/reports/jacoco/html/index.html`
3. `index.html` will list the coverage for the complete Project

#### Coverage indicators

- Red : Not Covered
- Yellow : Partially Covered
- Green : Completely Covered

#### JaCoCo configuration

Jacoco plugin is used for getting Code coverage Report. Here we can add folders to exclude from the
check.

**build.gradle.kts**

```kotlin
afterEvaluate {
    classDirectories.setFrom(classDirectories.files.map {
        fileTree(it).matching {
            exclude("com/ventionteams/medfast/dto/**");
            exclude("com/ventionteams/medfast/entity/*");
            exclude("**/config/*")
        }
    })
}
```

Here we can add new rules for code coverage verification.

**build.gradle.kts**

```kotlin
violationRules {
    rule {
        limit {
            minimum = "0.8".toBigDecimal()
        }
    }
    // new rule
    isFailOnViolation = false
}
```

### Logging

We are using the ELK stack to log the application. The logs are sent to the logstash server via the
`log4j2` appender. The logstash server then sends the logs to the elasticsearch server. The logs can
be viewed in the kibana server. Follow the steps below to view the logs:

1. Open the kibana server in the browser: [http://localhost:5601](http://localhost:5601)
2. Go to the `Discover` tab and use the `spring-boot-*` index pattern or the `Dashboard` tab
   and `Visual Data` dashboard
    