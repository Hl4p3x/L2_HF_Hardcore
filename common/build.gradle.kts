plugins {
    java
}

version = "1.0.0"

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(project(":mmocore"))

    implementation("at.favre.lib:bcrypt:0.+")
    implementation("com.zaxxer:HikariCP:3.+")

    implementation("com.google.guava:guava:28.+")

    implementation("org.slf4j:slf4j-api:1.7.+")

    implementation("org.liquibase:liquibase-core:3.8.9")

    implementation("com.fasterxml.jackson.core:jackson-core:2.10.+")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.10.+")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.10.+")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.10.+")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}