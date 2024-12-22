import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    java
    checkstyle
    jacoco
    id("org.springframework.boot") version "3.3.1"
    id("io.spring.dependency-management") version "1.1.5"
}

group = "com.ventionteams"
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
    // Spring Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // JWT
    implementation("io.jsonwebtoken:jjwt-jackson:0.12.6")
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    implementation("io.jsonwebtoken:jjwt-impl:0.12.6")

    // Utils
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
    implementation("org.projectlombok:lombok")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    implementation("org.apache.pdfbox:pdfbox:2.0.32")
    checkstyle("com.puppycrawl.tools:checkstyle:10.17.0")
    annotationProcessor("org.projectlombok:lombok")
    implementation("org.passay:passay:1.6.4")

    // DB
    implementation("org.liquibase:liquibase-core:4.28.0")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")

    // Testing
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.testcontainers:junit-jupiter:1.20.1")
    testImplementation("org.testcontainers:postgresql:1.20.1")
    testImplementation("io.rest-assured:rest-assured:5.5.0")
    testImplementation("net.datafaker:datafaker:2.3.1")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testAnnotationProcessor("org.projectlombok:lombok")
}

configurations.all {
    exclude(module = "spring-boot-starter-logging")
}

tasks.withType<Checkstyle> {
    isShowViolations = true
    maxWarnings = 0
    maxErrors = 0
    configFile = rootProject.file("config/checkstyle/checkstyle.xml")
}

tasks.withType<BootRun> {
    dependsOn(tasks.withType(Checkstyle::class))
}

tasks.withType<Test> {
    useJUnitPlatform()
    maxParallelForks = 1
    finalizedBy(tasks.jacocoTestReport)
    finalizedBy(tasks.jacocoTestCoverageVerification)
}

tasks.withType<JacocoReport> {
    dependsOn(tasks.test)

    reports {
        xml.required = true
        xml.outputLocation = layout.buildDirectory.file("reports/jacoco/coverage.xml")
        html.outputLocation = layout.buildDirectory.dir("reports/jacoco/html")
    }

    afterEvaluate {
        classDirectories.setFrom(classDirectories.files.map {
            fileTree(it).matching {
                exclude("com/ventionteams/medfast/dto/**");
                exclude("com/ventionteams/medfast/entity/*");
                exclude("**/config/*")
                exclude("com/ventionteams/medfast/service/GoogleMeetService.class")
            }
        })
    }
}

tasks.withType<JacocoCoverageVerification> {
    dependsOn(tasks.test)

    violationRules {
        rule {
            limit {
                minimum = "0.8".toBigDecimal()
            }
        }
        isFailOnViolation = true
    }
}
