import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

val logback_version: String by project
val ktor_version: String by project
val kotlin_version: String by project
val spek_version: String by project
val kluent_version: String by project
val mockk_version: String by project

plugins {
    kotlin("jvm")
    jacoco
    id("org.jmailen.kotlinter")
    id("io.gitlab.arturbosch.detekt")
    id("com.adarshr.test-logger")
}

apply {
    from("./jacoco.gradle.kts")
}

allprojects {
    group = "com.github.vitormbgoncalves"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenLocal()
        mavenCentral()
        jcenter()
    }

    apply(plugin = "kotlin")
    apply(plugin = "jacoco")

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

subprojects {
    tasks {
        withType<KotlinCompile<*>> {
            kotlinOptions {
                languageVersion = kotlin_version
                apiVersion = kotlin_version
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
            reports {
                xml.isEnabled = true
                csv.isEnabled = false
            }
        }

        apply(plugin = "org.jmailen.kotlinter")
        apply(plugin = "io.gitlab.arturbosch.detekt")
        apply(plugin = "com.adarshr.test-logger")

        dependencies {
            implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
            implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlin_version")

            testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spek_version")
            testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spek_version")
            testImplementation("org.amshove.kluent:kluent:$kluent_version")
            testImplementation("io.mockk:mockk:$mockk_version")
        }

        kotlinter {
            ignoreFailures = true
            indentSize = 4
            reporters = arrayOf("checkstyle", "plain")
            experimentalRules = false
            disabledRules = emptyArray()
        }

        detekt {
            buildUponDefaultConfig = true
            allRules = false
            config = files("$projectDir/detekt/config.yml")
            baseline = file("$projectDir/detekt/baseline.xml")

            reports {
                html.enabled = true
                xml.enabled = true
                txt.enabled = true
                sarif.enabled = true
            }
        }

        testlogger {
            showStackTraces = false
            showCauses = false
            showSimpleNames = true
            showExceptions = true
            showPassed = true
            showStandardStreams = false
        }
    }
}
