val jackson_version: String by project
val kmongo_version: String by project

dependencies {
    // Module dependencies
    implementation(project(":core"))

    // Serialization
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jackson_version")

    // KMongo Id type
    implementation("org.litote.kmongo:kmongo-id:$kmongo_version")
}
