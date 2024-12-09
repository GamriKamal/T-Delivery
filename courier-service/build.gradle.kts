plugins {
	java
	id("org.springframework.boot") version "3.3.5"
	id("io.spring.dependency-management") version "1.1.6"
	jacoco
}

group = "tdelivery.mr_irmag"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

extra["springCloudVersion"] = "2023.0.3"

dependencies {
	// Spring Boot Core Dependencies
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-cache")
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	// Spring Cloud Dependencies
	implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")

	// Spring Kafka Dependencies
	implementation("org.springframework.kafka:spring-kafka")
	testImplementation("org.springframework.kafka:spring-kafka-test")

	// Gson and Guava (Utility Libraries)
	implementation("com.google.code.gson:gson")
	implementation("com.google.guava:guava:31.1-jre")

	// Lombok (Code Generation)
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	// OpenAPI Documentation
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

	// Testing Dependencies
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
	testImplementation("org.junit.jupiter:junit-jupiter-engine")
	testImplementation("org.junit.platform:junit-platform-launcher")
	testImplementation("org.mockito:mockito-junit-jupiter")
	testImplementation("org.testcontainers:testcontainers")
	testImplementation("io.github.hakky54:logcaptor:2.7.10")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}


tasks.test {
	finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
}

jacoco {
	toolVersion = "0.8.12"
	reportsDirectory = layout.buildDirectory.dir("reports/jacoco")
}

tasks.jacocoTestReport {
	reports {
		xml.required = false
		csv.required = false
		html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
	}

	classDirectories.setFrom(files(classDirectories.files.map {
		fileTree(it).apply {
			exclude("tdelivery/mr_irmag/courier_service/domain/**")
			exclude("tdelivery/mr_irmag/courier_service/config/**")
		}
	}))
}
