# RESTful API from the Star Wars movies list

![Action workflow](https://github.com/vitormbgoncalves/starwars-movies/actions/workflows/codecov.yml/badge.svg)
[![codecov](https://codecov.io/gh/vitormbgoncalves/starwars-movies/branch/master/graph/badge.svg?token=ZnPk9pv1ed)](https://codecov.io/gh/vitormbgoncalves/starwars-movies)
[![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/KotlinBy/awesome-kotlin)

Backend API service built using clean architecture, gradle multi-module and implemented with Kotlin's Ktor framework. This project was created to demonstrate some tools and technologies that facilitate the development of
applications in Kotlin and also to be used as a foundation for future projects.

# Overview

## What is Star Wars Movies List?

RESTful API that provides the Star Wars movies catalog, with a summary of each movie and other information such as dates, direction and production.

## Why Clean Architecture?

One of the most important features of the Clean Architecture is its ability to provide developers with a way to organize code in a way that encapsulates business logic, but keeps it separate from the delivery mechanism, ie, the layer responsible for business rules becomes independent infrastructure layer, responsible for delivering the application.
This project uses the Clean Architecture to separate the functionality into 5 modules - **core**, **interfaces**, **common-lib**, **database**, **infrastructure**, with only few modules having dependencies on other modules.

So here is a brief overview of each module:

- **core** - contains business logic (services that implement use cases);
- **interfaces** - provides the controller for user input, converts it into the request model defined by the use case interact and passes this to the same. The request object are simple data transfer objects (DTO);
- **common-lib** - application common libraries (serialization configuration);
- **database** - repository implementation for data persistence (MongoDB repository implementation);
- **infrastructure** - platform/framework specific functionality (e.g. REST API, security, monitoring, caching).

## What tools/frameworks do we use?

- **Gradle** - our build system of choice (using Kotlin DSL)
- **Kotlin 1.5** - our language of choice
- **ktor** for creating web application: https://github.com/ktorio/ktor
- **MongoDB** for database: https://www.mongodb.com/
- **KMongo** for access database: //https://github.com/Litote/kmongo
- **Jackson** for JSON serialization/deserialization: https://github.com/FasterXML/jackson
- **Koin** for dependency injection: https://github.com/InsertKoinIO/koin
- **HOCON** for application configuration: https://github.com/lightbend/config/
- **Redis** for application caching: https://redis.com/
- **ktor-redis** ktor redis client base on lettuce for caching: https://github.com/ZenLiuCN/ktor-redis
- **Ktor-OpenAPI-Generator** for OpenAPI/Swagger 3 documentation generation: https://github.com/papsign/Ktor-OpenAPI-Generator
- **ktor-health-check** for health and readiness checks: https://github.com/zensum/ktor-health-check
- **kotlinx.serialization** for multi-format data serialization: https://github.com/Kotlin/kotlinx.serialization
- **Keycloak** for application Oauth2 authentication/authorization: https://www.keycloak.org/

and for testing:
- **Spek2** framework for application testing: https://github.com/spekframework/spek
- **Kluent** provides fluent assertions: https://github.com/MarkusAmshove/Kluent
- **Mockk** provides mocking tool: https://github.com/mockk/mockk
- **JaCoCo** for code coverage metrics: https://github.com/jacoco/jacoco
- **Codecov** for code coverage reports and dashboard: https://github.com/marketplace/codecov
- **Embedded MongoDB** for mongo database integration and unit test: https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo
- **embedded-redis** for redis caching integration and unit test: https://github.com/kstyrc/embedded-redis

and for monitoring:
- **Logback** for application events logging: https://github.com/qos-ch/logback
- **Micrometer** for application instrumentation and metrics facade: https://github.com/micrometer-metrics/micrometer
- **ktor-opentracing** for OpenTracing instrumentation and Jaeger client: https://github.com/zopaUK/ktor-opentracing
- **kotlin-logging-opentracing-decorator** for writes OpenTracing logs in addition to regular logs: https://github.com/fstien/kotlin-logging-opentracing-decorator
- **logstash-gelf** provides logging to logstash from logback, using the Graylog Extended Logging Format (GELF): https://github.com/mp911de/logstash-gelf
- **Prometheus** for application monitoring and metrics database: https://github.com/prometheus/prometheus
- **Grafana** for observability and data visualization from prometheus data: https://github.com/grafana/grafana
- **Jaeger** for application tracing: https://github.com/jaegertracing/jaeger
- **ELK Stack (Elasticsearch, Logstash, kibana)** for logs ingestion, processing and visualization: https://www.elastic.co/
 
and some other misc stuff:
- **Ktlint** for code checkstyle, linting and formatter: https://github.com/pinterest/ktlint
- **Detekt** for static code smell analysis: https://github.com/detekt/detekt
- **Jib** for build containers images: https://github.com/GoogleContainerTools/jib

# Setup

## Prerequisites

- **[Required]** [JDK 16](https://www.oracle.com/java/technologies/javase-jdk16-downloads.html): To run gradle tasks.
- **[Required]** [Gradle](https://gradle.org/): How the project is built.
- **[Required]** [Docker](https://www.docker.com/): As this project dependencies is dockerized.
- **[Required]** [Docker-Compose](https://docs.docker.com/compose/): To run project dependencies.

# Running application

First, clone the project:

```shell
git https://github.com/vitormbgoncalves/starwars-movies.git
cd starwars-movies
```

Then execute the following commands to run docker compose and install project dependencies (database, monitoring, security):

```shell
docker build -f Dockerfile.prometheus -t prometheus-star-wars . && docker-compose up -d
```

Then configure Keycloak:

You can access the Keycloak UI by this url: http://localhost:8180/

* **Username**: admin
* **Password**: admin

After that, in the UI interface you need to create a new user and write down the credential to use later. It is also necessary to note the client credential **KtorApp**.

Then build the application with the command below:

```shell
./gradlew build
```

Now you are ready to lunch it:

```shell
./gradlew run
```

After executing the project, access the following address in the browser: http://localhost:8080/
Authorize the user with the Keycloak data annotated in the previous step.

# Testing application

To run the unit and integration tests on all modules, run the following command:

```
./gradlew test
```

# Quick note

This project was developed and runs on Linux.