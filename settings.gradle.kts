pluginManagement {

    val kotlinPluginVersion: String by settings
    val kotlinterPluginVersion: String by settings
    val detektPluginVersion: String by settings
    val testloggerPluginVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinPluginVersion
        id("org.jmailen.kotlinter") version kotlinterPluginVersion
        id("io.gitlab.arturbosch.detekt") version detektPluginVersion
        id("com.adarshr.test-logger") version testloggerPluginVersion
    }
}

rootProject.name = "starwars-movies"
include("core")
