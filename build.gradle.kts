buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.13.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.2.20")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.9.5")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.57.2")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

task("clean", Delete::class) {
    delete = setOf(rootProject.layout.buildDirectory)
}