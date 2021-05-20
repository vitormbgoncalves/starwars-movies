val kmongo_version: String by project

dependencies {
    // Module dependencies
    implementation(project(":core"))

    // KMongo dependencies
    implementation("org.litote.kmongo:kmongo-coroutine-serialization:$kmongo_version")
}
