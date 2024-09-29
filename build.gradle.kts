plugins {
	java
	id("org.springframework.boot") version "3.3.4"
	id("io.spring.dependency-management") version "1.1.6"

}

group = "tdelivery.mr_irmag"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

extra["springCloudVersion"] = "2023.0.3"

dependencies {
	// Spring Boot Starter
	implementation("org.springframework.boot:spring-boot-starter-web")

	// Spring Cloud
//	implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")

	// Database
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	runtimeOnly("com.h2database:h2")

	// Docker
//	developmentOnly("org.springframework.boot:spring-boot-docker-compose")

	// Utilities
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.apache.commons:commons-lang3:3.12.0")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	// Security
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("io.jsonwebtoken:jjwt-api:0.12.3")
	implementation("io.jsonwebtoken:jjwt-impl:0.12.3")
	implementation("io.jsonwebtoken:jjwt-jackson:0.12.3")

	// Testing
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	// Lombok
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
