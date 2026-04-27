plugins {
	java
	id("org.springframework.boot") version "4.0.6"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.distributed"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	compileOnly("org.projectlombok:lombok")
	annotationProcessor ("org.projectlombok:lombok")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.springframework.boot:spring-boot-starter-webservices")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webservices-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("net.objecthunter:exp4j:0.4.8")
	implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.15.2")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
