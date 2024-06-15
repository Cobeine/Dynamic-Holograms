plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version("7.1.0")
}

group = "me.cobeine"
version = "1.0-SNAPSHOT"

tasks.compileJava {
    options.encoding = "UTF-8"
    sourceCompatibility = "17"
    targetCompatibility = "17"
}

repositories {
    mavenCentral()
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven ("https://repo.aikar.co/content/groups/aikar/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/sonatype-oss-snapshots")
    maven("https://jitpack.io")
}


dependencies {
    compileOnly ("me.clip:placeholderapi:2.11.5")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")
    compileOnly("org.projectlombok:lombok:1.18.26")
    compileOnly("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")

    annotationProcessor("org.projectlombok:lombok:1.18.26")

    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("com.github.C0-1:SQLava:1.5.4-SNAPSHOT")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("redis.clients:jedis:5.1.0")
    implementation ("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    implementation("mysql:mysql-connector-java:8.0.33")

}


tasks.withType<ProcessResources> {
    from(sourceSets.main.get().resources) {
        include("plugin.yml")
        filter { line -> line.replace("%project_version%", project.version.toString()) }
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
}
tasks.shadowJar {
    archiveFileName.set("Holograms-${project.version}.jar")
    relocate ("co.aikar.commands", "me.cobeine.holograms.api.acf")
    relocate ("co.aikar.locales", "me.cobeine.holograms.api.locales")

    exclude("org/checkerframework/")
    exclude("META-INF/**")
}
apply(plugin = "java")
apply(plugin = "com.github.johnrengelman.shadow")


