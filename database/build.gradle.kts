val kmongo_version: String by project
val embedMongo_version: String by project

dependencies {
    // Module dependencies
    implementation(project(":core"))

    // KMongo
    implementation("org.litote.kmongo:kmongo-coroutine-serialization:$kmongo_version")

    // Embedded MongoDB for integration testing
    testImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo:$embedMongo_version")
}
