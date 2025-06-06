plugins {
    id("java")
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

tasks {
    runServer {
        downloadPlugins {
            url("https://ci.pyr.lol/job/ZNPCsPlus/lastSuccessfulBuild/artifact/plugin/build/libs/ZNPCsPlus-2.1.0-SNAPSHOT.jar")
        }
        minecraftVersion("1.21.4")
    }
}

group = "com.nxweb"
version = "1.0-SNAPSHOT"

repositories {
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "pyrSnapshots"
        url = uri("https://repo.pyr.lol/snapshots")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("lol.pyr:znpcsplus-api:2.1.0-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.jar {
    manifest {
        attributes["paperweight-mappings-namespace"] = "mojang"
    }
}