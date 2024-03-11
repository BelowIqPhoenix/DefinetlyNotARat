import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("gg.essential.loom") version "0.10.0.+"
    id("dev.architectury.architectury-pack200") version "0.1.3"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("java")
    kotlin("jvm") version "1.9.0"
}

version = "1.0"
group = "com.example"
base.archivesName = "examplemod"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

loom {
    silentMojangMappingsLicense()
    launchConfigs {
        getByName("client") {
            property("mixin.debug.verbose", "true")
            arg("--tweakClass", "org.spongepowered.asm.launch.MixinTweaker")
        }
    }
    runConfigs {
        remove(getByName("server"))
    }
    forge {
        pack200Provider.set(dev.architectury.pack200.java.Pack200Adapter())
        mixinConfig("mixins.${base.archivesName.get()}.json")
    }
    mixin {
        defaultRefmapName.set("mixins.${base.archivesName.get()}.refmap.json")
    }
}

val include: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

repositories {
    maven("https://repo.spongepowered.org/maven/")
}

dependencies {
    minecraft("com.mojang:minecraft:1.8.9")
    mappings("de.oceanlabs.mcp:mcp_stable:22-1.8.9")
    forge("net.minecraftforge:forge:1.8.9-11.15.1.2318-1.8.9")

    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
    include("org.spongepowered:mixin:0.7.11-SNAPSHOT") {
        isTransitive = false
    }
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            languageVersion = "1.7"

            freeCompilerArgs =
                listOf(
                    //"-Xuse-k2"
                )
        }
        kotlinDaemonJvmArguments.set(
            listOf(
                "-Xmx4G",
                "-Dkotlin.enableCacheBuilding=true",
                "-Dkotlin.useParallelTasks=true",
                "-Dkotlin.enableFastIncremental=true"
            )
        )
    }
    processResources {
        inputs.property("version", project.version)
        inputs.property("mcversion", "1.8.9")
        inputs.property("modid", base.archivesName.get())
        inputs.property("mixinGroup", "${project.group}.mixin")

        filesMatching(listOf("mcmod.info", "mixins.${base.archivesName.get()}.json")) {
            expand(inputs.properties)
        }
        dependsOn(compileJava)
    }
    jar {
        archiveBaseName.set(base.archivesName.get())
        manifest.attributes(
            mapOf(
                "FMLCorePluginContainsFMLMod" to true,
                "ForceLoadAsMod" to true,
                "TweakClass" to "org.spongepowered.asm.launch.MixinTweaker",
                "MixinConfigs" to "mixins.${base.archivesName.get()}.json"
            )
        )
        dependsOn(shadowJar)
        enabled = false
    }
    remapJar {
        val file = shadowJar.get().archiveFile
        input.set(file)
    }
    shadowJar {
        archiveClassifier.set("dev")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        configurations = listOf(include)

        exclude(
            "**/LICENSE.md",
            "**/LICENSE.txt",
            "**/LICENSE",
            "**/NOTICE",
            "**/NOTICE.txt",
            "pack.mcmeta",
            "dummyThing",
            "**/module-info.class",
            "META-INF/proguard/**",
            "META-INF/maven/**",
            "META-INF/versions/**",
            "META-INF/com.android.tools/**",
            "fabric.mod.json"
        )
        mergeServiceFiles()
    }
}
