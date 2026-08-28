plugins {
	java
	id("jacoco")
	id("org.springframework.boot") version "4.0.5"
	id("io.spring.dependency-management") version "1.1.7"
}

version = "1.0.0"
group = "br.com.fiap"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

tasks.named<Jar>("jar") {
	enabled = false
}

configurations.configureEach {
	exclude(group = "ch.qos.logback", module = "logback-classic")
	exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
}

dependencies {

	// Spring Boot
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.springframework.boot:spring-boot-starter-graphql")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.1.0")

	// Lombok
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	// Logging
	implementation("org.slf4j:slf4j-api")
	implementation("org.apache.logging.log4j:log4j-slf4j-impl")
	implementation("org.springframework.boot:spring-boot-starter-log4j2")

	// PostgreSQL
	runtimeOnly("org.postgresql:postgresql")

	// Tests
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testImplementation("org.springframework.boot:spring-boot-starter-graphql-test")
	testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
	testImplementation("org.springframework.boot:spring-boot-starter-validation-test")

}

tasks.withType<Test> {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport)
}

tasks.named<JacocoReport>("jacocoTestReport") {
	dependsOn(tasks.test)

	reports {
		html.required.set(true)
	}

	classDirectories.setFrom(
		files(
			classDirectories.files.map {
				fileTree(it) {
					exclude(
						"**/config/**",
						"**/enums/**",
						"**/exceptions/**",
						"**/model/**",
						"**/HistoricoAPIApplication.class"
					)
				}
			}
		)
	)
}