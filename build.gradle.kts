/*
 * Copyright 2022, Petr Laštovička as Lasta apps, All rights reserved
 *
 * This file is part of ČVUT Bus.
 *
 * ČVUT Bus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ČVUT Bus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ČVUT Bus.  If not, see <https://www.gnu.org/licenses/>.
 */

import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.aboutLibraries) apply false
    alias(libs.plugins.sqldelight) apply false
    alias(libs.plugins.shadow) apply false

    alias(libs.plugins.benNamesVersions)
    alias(libs.plugins.versionCatalogUpdate)
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

versionCatalogUpdate {
    sortByKey.set(true)
    // pins version - wouldn't be changed
    pin {}
    // keeps entry - wouldn't be deleted when unused
    keep {
        keepUnusedVersions.set(true)
        keepUnusedLibraries.set(true)
        keepUnusedPlugins.set(true)
    }
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("rc", "beta", "release").any { version.toUpperCase().contains(it) }
    val regex = """^[0-9,.v-]+(-r)?$""".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}
