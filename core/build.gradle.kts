val kmongo_version: String by project
val serialization_version: String by project

apply(plugin = "kotlinx-serialization")

dependencies {
    // kotlinx Serialization for KMongo
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serialization_version")

    // KMongo Id type
    implementation("org.litote.kmongo:kmongo-id:$kmongo_version")
}
