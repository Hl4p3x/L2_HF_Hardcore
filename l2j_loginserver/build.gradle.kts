plugins {
    id("java")
    id("com.github.johnrengelman.shadow").version("5.2.0")
    id("distribution")
}

repositories {
    jcenter()
    mavenCentral()
}

defaultTasks("clean", "build")

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation(project(":mmocore"))
    implementation(project(":common"))

    implementation("com.sun.mail:javax.mail:1.+")
    implementation("com.zaxxer:HikariCP:3.+")
    implementation("org.mariadb.jdbc:mariadb-java-client:2.+")
    implementation("com.google.guava:guava:28.+")
    implementation("org.slf4j:slf4j-api:1.7.+")
    implementation("org.slf4j:slf4j-jdk14:1.7.+")
    implementation("org.apache.httpcomponents:httpclient:4.+")

    implementation(
            files(
                    "dist/libs/mmocore.jar",
                    "dist/libs/L2J_GeoDriver.jar",
                    "dist/libs/jython-engine-2.2.1.jar"
            )
    )

    implementation("org.jdbi:jdbi3-core:3.+")
    implementation("org.jdbi:jdbi3-sqlobject:3.+")

    testImplementation("org.assertj:assertj-core:3.+")
    testImplementation("org.mockito:mockito-core:2.+")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.+")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.+")
}

tasks {
    test {
        useJUnitPlatform()
    }

    jar {
        archiveFileName.set("l2jserver.jar")
        exclude("**/dbinstaller/**")
        exclude("**/com.l2jserver.loginserver/**")
        exclude("**/accountmanager/**")
        exclude("**/gsregistering/**")
        manifest {
            attributes("Main-Class" to "com.l2jserver.gameserver.GameServer")
        }
        from(configurations.compileClasspath)
    }
}
