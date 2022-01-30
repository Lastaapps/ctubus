buildscript {
}
plugins {
    id("com.android.application") version "7.2.0-alpha05" apply false
    id("com.android.library") version "7.2.0-alpha05" apply false
    id("org.jetbrains.kotlin.android") version "1.5.31" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}