val jackson_version: String by project
val kmongo_version: String by project
val ktorOpenAPIGenerator_version: String by project
val typesafe_version: String by project

dependencies {
    // Module dependencies
    implementation(project(":core"))
    implementation(project(":common-lib"))

    // Serialization
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jackson_version")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jackson_version")

    // KMongo Id type
    implementation("org.litote.kmongo:kmongo-id:$kmongo_version")

    // Ktor OpenAPI Generator
    implementation("com.github.papsign:Ktor-OpenAPI-Generator:$ktorOpenAPIGenerator_version")

    // HOCON configuration library
    implementation("com.typesafe:config:$typesafe_version")
}
