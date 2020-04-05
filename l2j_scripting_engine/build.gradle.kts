plugins {
    id("java")
}

repositories {
    jcenter()
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    sourceCompatibility = JavaVersion.VERSION_11
}

version = "1.0.0"

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.+")
}

tasks.withType<JavaCompile>().configureEach {
    options.apply {
        isFork = true
        compilerArgs = listOf(
                "--add-exports", "jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED",
                "--add-exports", "jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED"
        )
    }
}