import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

val logback_version: String by project
val ktor_version: String by project
val kotlin_version: String by project
val kotlinLanguage_version: String by project
val spek_version: String by project
val kluent_version: String by project
val mockk_version: String by project
val serialization_version: String by project

val jacocoExcludes = listOf(
    "**/core/**",
    "**/commonlib/**",
    "**/openAPIGeneratorConfig/**",
    "**/module"
)

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    jacoco
    id("org.jlleitschuh.gradle.ktlint")
    id("io.gitlab.arturbosch.detekt")
    id("com.adarshr.test-logger")
}

apply {
    from("./jacoco.gradle.kts")
}

allprojects {
    group = "com.github.vitormbgoncalves.starwarsmovies"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenLocal()
        mavenCentral()
        jcenter()
        maven(url = "https://jitpack.io")
    }

    apply(plugin = "kotlin")
    apply(plugin = "kotlinx-serialization")
    apply(plugin = "jacoco")

    jacoco {
        toolVersion = "0.8.7"
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

subprojects {
    tasks {
        withType<KotlinCompile<*>> {
            kotlinOptions {
                languageVersion = kotlinLanguage_version
                apiVersion = kotlinLanguage_version
                (this as KotlinJvmOptions).jvmTarget =
                    JavaVersion.VERSION_1_8.toString()
                freeCompilerArgs = listOfNotNull(
                    "-Xopt-in=kotlin.RequiresOptIn"
                )
            }
        }

        withType<Test> {
            useJUnitPlatform {
                includeEngines("spek2")
            }
        }

        withType<Detekt>().configureEach {
            jvmTarget = "1.8"
        }

        test {
            finalizedBy(jacocoTestReport)
        }

        jacocoTestReport {
            dependsOn(test)
        }

        apply(plugin = "org.jlleitschuh.gradle.ktlint")
        apply(plugin = "io.gitlab.arturbosch.detekt")
        apply(plugin = "com.adarshr.test-logger")

        dependencies {
            // Kotlin
            implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
            implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlin_version")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serialization_version")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlin_version")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$kotlin_version")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$kotlin_version")

            // Test
            testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spek_version")
            testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spek_version")
            testImplementation("org.amshove.kluent:kluent:$kluent_version")
            testImplementation("io.mockk:mockk:$mockk_version")
        }

        configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
            debug.set(true)
            outputToConsole.set(true)
            outputColorName.set("RED")
        }

        detekt {
            config = files("config/detekt/detekt.yml")
            buildUponDefaultConfig = true

            reports {
                html.enabled = true
                xml.enabled = false
                txt.enabled = false
                sarif.enabled = false
            }
        }

        testlogger {
            theme = com.adarshr.gradle.testlogger.theme.ThemeType.MOCHA
            showExceptions = true
            showStackTraces = true
            showFullStackTraces = false
            showCauses = true
            slowThreshold = 0
            showSummary = true
            showSimpleNames = false
            showPassed = true
            showSkipped = true
            showFailed = true
            showStandardStreams = false
            showPassedStandardStreams = true
            showSkippedStandardStreams = true
            showFailedStandardStreams = true
            logLevel = LogLevel.LIFECYCLE
        }
    }
}
