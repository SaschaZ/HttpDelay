import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    id("com.github.johnrengelman.shadow")
    kotlin("jvm")
}

repositories {
    google()
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation(kotlin("stdlib"))

    val kotlinCoroutinesVersion: String by project
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")

    val ktorVersion: String by project
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-compression:$ktorVersion")
    implementation("io.ktor:ktor-server-default-headers:$ktorVersion")
    implementation("io.ktor:ktor-server-host-common:$ktorVersion")
    implementation("io.ktor:ktor-server-html-builder:$ktorVersion")

    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")

    implementation("org.slf4j:slf4j-log4j12:2.0.3")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveFileName.set("HttpDelay.jar")
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "MainKt"))
        }
        destinationDirectory.set(File(rootProject.projectDir.path))
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "15"
    kotlinOptions.freeCompilerArgs = listOf(
        "-opt-in=kotlin.RequiresOptIn"
    )
}