val ktor_version: String by project
val jackson_version: String by project

dependencies {
    // Jackson Serialization
    implementation("io.ktor:ktor-jackson:$ktor_version")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jackson_version")
}
