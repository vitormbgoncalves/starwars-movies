pluginManagement {

    val kotlinPluginVersion: String by settings
    val ktlintPluginVersion: String by settings
    val detektPluginVersion: String by settings
    val testloggerPluginVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinPluginVersion
        id("org.jlleitschuh.gradle.ktlint") version ktlintPluginVersion
        id("io.gitlab.arturbosch.detekt") version detektPluginVersion
        id("com.adarshr.test-logger") version testloggerPluginVersion
    }
}

rootProject.name = "starwars-movies"
include("core")
include("interfaces")
