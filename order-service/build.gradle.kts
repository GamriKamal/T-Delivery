plugins {
	java
	id("org.springframework.boot") version "3.3.4"
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
	// Spring Boot
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-cache")
	implementation("org.springframework.boot:spring-boot-starter-websocket")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	// Spring Cloud
	implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")

	// JSON and data processing
	implementation("com.google.code.gson:gson")
	implementation("org.springframework.hateoas:spring-hateoas")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

	// Kafka
	implementation("org.springframework.kafka:spring-kafka")

	// General utility libraries
	implementation("com.google.guava:guava:31.1-jre")
	implementation("org.hibernate:hibernate-spatial:6.6.1.Final")

	// Swagger
	implementation("org.springdoc:springdoc-openapi-ui:2.1.0")

	// Lombok
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	// PostgreSQL
	runtimeOnly("org.postgresql:postgresql")

	// Test
	testImplementation("junit:junit:4.12")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.kafka:spring-kafka-test")
	testImplementation("org.testcontainers:junit-jupiter:1.18.3")
	testImplementation("org.testcontainers:kafka:1.17.6")
	testImplementation("org.testcontainers:postgresql:1.20.0")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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
			exclude("tdelivery/mr_irmag/order_service/domain/**")
			exclude("tdelivery/mr_irmag/order_service/config/**")
			exclude("tdelivery/mr_irmag/order_service/kafka/**")
			exclude("tdelivery/mr_irmag/order_service/service/WebSocketDeliveryStatusService.class")
		}


	}))
}