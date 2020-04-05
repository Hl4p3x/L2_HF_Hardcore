import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
	sourceSets {
		main {
			java {
				srcDir("dist/game/data/scripts")
			}
		}
	}
}

dependencies {
	implementation(files("../l2j_server/dist/libs/mmocore.jar"))
	implementation(project(":l2j_server"))

	implementation("org.slf4j:slf4j-api:1.7.+")
	implementation("org.jdbi:jdbi3-core:3.+")
	implementation("org.jdbi:jdbi3-sqlobject:3.+")
	implementation("org.apache.httpcomponents:httpclient:4.+")

	implementation("com.fasterxml.jackson.core:jackson-core:2.10.+")
	implementation("com.fasterxml.jackson.core:jackson-databind:2.10.+")
	implementation("com.fasterxml.jackson.core:jackson-annotations:2.10.+")
	implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.10.+")

	implementation("com.google.guava:guava:28.+")

	testImplementation("org.assertj:assertj-core:3.+")
	testImplementation("org.mockito:mockito-core:2.+")
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.+")
	testImplementation("org.junit.jupiter:junit-jupiter-engine:5.+")
}

task("zip", Zip::class) {
	val filename = "L2J_DataPack_" + DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDateTime.now()) + ".zip"
	archiveFileName.set(filename)
	from("dist")
	exclude("**/geodata/**")
	exclude("**/pathnode/**")
	println("Build in build/distributions/$filename")
}

tasks {
	test {
		useJUnitPlatform()
	}
}

tasks {
	build {
		finalizedBy(getByName("zip"))
	}
}