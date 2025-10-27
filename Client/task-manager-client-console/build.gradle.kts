import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform.getCurrentOperatingSystem

plugins {
    application
    id("java")
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
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("com.fasterxml.jackson.core:jackson-core:2.15.2")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.15.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")

    compileOnly("org.projectlombok:lombok:1.18.36")

    annotationProcessor("org.projectlombok:lombok:1.18.36")

    testCompileOnly("org.projectlombok:lombok:1.18.36")

    testAnnotationProcessor("org.projectlombok:lombok:1.18.36")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}


application {
    mainClass = "pl.kul.taskmanagerclient.ConsoleApplication"
}


tasks.test {
    useJUnitPlatform()
}