
val kotlinVersion: String by project
val logbackVersion: String by project
val ktorVersion: String by project
val mockkVersion: String by project
val jaxbApiVersion: String by project
val jaxbRuntimeVersion: String by project
val gsonVersion: String by project
val apacheCommonsLangVersion: String by project
val apacheCommonsCollectionsVersion: String by project
val kotlinCsvVersion: String by project
val micrometerVersion: String by project
val prometheusVersion: String by project

plugins {
    kotlin("jvm") version "2.0.0"
    id("io.ktor.plugin") version "2.3.12"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0"
    id("org.sonarqube") version "6.2.0.5505"
    id("jacoco")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

sonar {
    properties {
        property("sonar.projectKey", "gusthawojunkes_financial-file-parser")
        property("sonar.organization", "gusthawojunkes")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.coverage.jacoco.xmlReportPath", "${layout.buildDirectory.get()}/reports/jacoco/test/jacocoTestReport.xml")
        property("sonar.exclusions", "src/main/kotlin/dev/wo/domain/models/ofx/**")
    }
}

group = "dev.wo"
version = "1.2.0"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-serialization-gson:$ktorVersion")
    implementation("io.ktor:ktor-server-swagger:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-config-yaml")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("javax.xml.bind:jaxb-api:$jaxbApiVersion")
    implementation("org.glassfish.jaxb:jaxb-runtime:$jaxbRuntimeVersion")
    implementation("com.google.code.gson:gson:$gsonVersion")
    implementation("org.apache.commons:commons-lang3:$apacheCommonsLangVersion")
    implementation("org.apache.commons:commons-collections4:$apacheCommonsCollectionsVersion")
    implementation("com.jsoizo:kotlin-csv-jvm:$kotlinCsvVersion")
    implementation("io.ktor:ktor-server-cors:$ktorVersion")

    implementation("io.ktor:ktor-server-metrics-micrometer:$ktorVersion")
    implementation("io.micrometer:micrometer-registry-prometheus:$prometheusVersion")
    implementation("io.micrometer:micrometer-core:$micrometerVersion")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
}

tasks.register("stage") {
    description = "Assembles the application and prepares it for deployment."
    group = "FIX"
    dependsOn("build")
}

tasks.named("sonar") {
    dependsOn("jacocoTestReport")
}

tasks.named<JacocoReport>("jacocoTestReport") {
    reports {
        xml.required = true
    }
}


tasks.named("sonar").configure {
    dependsOn("jacocoTestReport")
}
