import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform.getCurrentOperatingSystem

plugins {
    application
    id("java")
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.beryx.runtime").version("1.12.7")
}

val platform = when {
    getCurrentOperatingSystem().isWindows -> "win"
    getCurrentOperatingSystem().isLinux -> "linux"
    getCurrentOperatingSystem().isMacOsX -> "mac"
    else -> throw UnsupportedOperationException("Operating system ${getCurrentOperatingSystem()} not supported yet")
}

repositories {
    mavenCentral()
}

runtime {
    jpackage {
        imageName = rootProject.name
        skipInstaller = true
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")

    runtimeOnly("com.h2database:h2")

    compileOnly("org.projectlombok:lombok:1.18.36")

    annotationProcessor("org.projectlombok:lombok:1.18.36")

    testCompileOnly("org.projectlombok:lombok:1.18.36")

    testAnnotationProcessor("org.projectlombok:lombok:1.18.36")

    testImplementation("org.hibernate.validator:hibernate-validator:7.0.2.Final")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa")
    testImplementation("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.assertj:assertj-core:3.27.2")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}

application {
    mainClass = "pl.kul.taskmanager.TaskManagerServerApplication"
}

tasks.test {
    useJUnitPlatform()
}