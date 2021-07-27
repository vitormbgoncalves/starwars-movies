val koin_version: String by project
val logback_version: String by project
val ktor_version: String by project
val kmongo_version: String by project
val ktorOpenAPIGenerator_version: String by project
val jackson_version: String by project
val ktorRedis_version: String by project
val lettuce_version: String by project
val embeddedRedis_version: String by project
val healthCheck_version: String by project
val prometeus_version: String by project
val ktorOpentracing_version: String by project
val jaegerClient_version: String by project
val opentracingMongoDB_version: String by project
val kotlinLogging_version: String by project
val opentracingDecorator_version: String by project
val opentracingLettuce_version: String by project

plugins {
    id("com.google.cloud.tools.jib") version "3.1.2"
}

java {
    targetCompatibility = JavaVersion.VERSION_1_8
}

val appMainClassName by extra("io.ktor.server.netty.EngineMain")

val defaultAppJvmArgs = listOf(
    "-server",
    "-Djava.awt.headless=true",
    "-XX:+UseG1GC",
    "-XX:+UseStringDeduplication",
    "-XX:MaxDirectMemorySize=5G"
)

val devJvmArgs = listOf(
    "-Xms128m",
    "-Xmx2g",
    "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5006"
)

application {
    mainClass.set(appMainClassName)
    applicationDefaultJvmArgs = defaultAppJvmArgs + devJvmArgs
}

jib {
    from {
        image = "azul/zulu-openjdk-alpine:16.0.0"
    }
    to {
        image = "ktor-server"
        tags = setOf("${project.version}")
    }
    container {
        ports = listOf("8080")
        mainClass = appMainClassName
        jvmFlags = defaultAppJvmArgs
    }
}

dependencies {
    // Module dependencies
    implementation(project(":core"))
    implementation(project(":interfaces"))
    implementation(project(":database"))
    implementation(project(":common-lib"))

    // Ktor Server
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-auth:$ktor_version")
    implementation("io.ktor:ktor-auth-jwt:$ktor_version")
    implementation("io.ktor:ktor-auth:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")

    // Ktor Client
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-client-apache:$ktor_version")
    implementation("io.ktor:ktor-client-jackson:$ktor_version")

    // Jackson
    implementation("io.ktor:ktor-jackson:$ktor_version")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jackson_version")

    // koin
    implementation("io.insert-koin:koin-ktor:$koin_version")
    implementation("io.insert-koin:koin-logger-slf4j:$koin_version")
    testImplementation("io.insert-koin:koin-test-junit4:$koin_version")

    // KMongo
    implementation("org.litote.kmongo:kmongo-coroutine-serialization:$kmongo_version")

    // Redis
    implementation("com.github.ZenLiuCN:ktor_redis:$ktorRedis_version")
    implementation("io.lettuce:lettuce-core:$lettuce_version")
    testImplementation("com.github.codemonstur:embedded-redis:$embeddedRedis_version")

    // Ktor OpenAPI Generator
    implementation("com.github.papsign:Ktor-OpenAPI-Generator:$ktorOpenAPIGenerator_version")

    // Ktor health-check
    implementation("com.github.zensum:ktor-health-check:$healthCheck_version")

    // Micrometer
    implementation("io.ktor:ktor-metrics-micrometer:$ktor_version")
    implementation("io.micrometer:micrometer-registry-prometheus:$prometeus_version")

    // OpenTracing
    implementation("io.jaegertracing:jaeger-client:$jaegerClient_version")
    implementation("com.zopa:ktor-opentracing:$ktorOpentracing_version")
    implementation("io.opentracing.contrib:opentracing-mongo-common:$opentracingMongoDB_version")
    implementation("io.opentracing.contrib:opentracing-redis-lettuce-5.2:$opentracingLettuce_version")

    // Kotlin Logging
    implementation("io.github.microutils:kotlin-logging-jvm:$kotlinLogging_version")
    implementation("com.github.fstien:kotlin-logging-opentracing-decorator:$opentracingDecorator_version")

    implementation("biz.paluch.logging:logstash-gelf:1.14.1")

    implementation("io.micrometer:micrometer-registry-elastic:1.7.2")

    implementation("com.google.guava:guava:30.1.1-jre")
}
