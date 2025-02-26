# reactive
 
Reactive is an alchemy-themed magic mod based on in-world experimentation. 
If you're here, you're either curious about the source code, or want to know the format for recipes.
Either way, this is a passion project but I tried to put in work to make it understandable at least. 

See the [Wiki](https://github.com/hjake123/reactive/wiki) for all documentation.

If you're looking for the config option or recipe information, please see the [Pack Dev Guide](https://github.com/hjake123/reactive/wiki/Pack-Dev-Guide) on the wiki.

# Releases
[Curseforge Download Page](https://www.curseforge.com/minecraft/mc-mods/reactive)

[Modrinth Download Page](https://modrinth.com/mod/reactive)

# Build Instructions
This mod is built using Gradle -- the exact properties can be found in `gradle/gradle-wrapper.properties`. It's developed using IntelliJ, so importing the project into that IDE should be sufficient for the inbuilt Gradle integration to pick up and prepare the project for building.

Alternatively, `./gradlew.bat init` should be sufficient to set up the build environment.

Once set up, use the `build` gradle task to compile everything and the `jar` gradle task to create an artifact. Make sure you've updated the `gradle.properties` with the current version number before running `jar`.

The `runClient` task is also quite useful for testing the mod, and `runData` will perform data generation (see the `datagen` package). `runServer` also works, but you need to log in using an account, so you cannot log into the debug server using the debug client.

If you have further questions, you can contact me at `hyperlynx` on Discord.
