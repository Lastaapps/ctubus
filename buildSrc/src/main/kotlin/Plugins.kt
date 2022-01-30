/*
 * Copyright 2022, Petr Laštovička as Lasta apps, All rights reserved
 *
 * This file is part of ČVUT Bus.
 *
 * Menza is free software: you can redistribute it and/or modify
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

object Plugins {

    const val APPLICATION = "com.android.application"
    const val LIBRARY = "com.android.library"
    const val KOTLIN = "kotlin-android"

    object Java {
        const val LIBRARY = "java-library"
        const val KOTLIN = "kotlin"
    }

    const val KSP = "com.google.devtools.ksp"
    const val KAPT = "kotlin-kapt"
    const val SQLDELIGHT = "com.squareup.sqldelight"
    const val DAGGER_HILT_CLASSPATH =
        "com.google.dagger:hilt-android-gradle-plugin:${Versions.DAGGER_HILT}"
    const val DAGGER_HILT = "dagger.hilt.android.plugin"

    const val PARCELIZE = "kotlin-parcelize"

    const val OSS_LICENSE = "com.google.android.gms.oss-licenses-plugin"

    const val PLAY_SERVICES = "com.google.gms.google-services"
    const val FIREBASE_CRASHLYTICS = "com.google.firebase.crashlytics"

}