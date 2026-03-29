// Root build file for Simple-Commons
// Plugins are defined in the main project root build.gradle.kts

tasks.register<Delete>("clean") {
    delete {
        layout.buildDirectory.asFile
    }
}
