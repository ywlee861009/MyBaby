plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.sqldelight)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "core-database"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)
            implementation(project(":core:model"))
        }
        androidMain.dependencies {
            implementation(libs.sqldelight.androidDriver)
        }
        iosMain.dependencies {
            implementation(libs.sqldelight.nativeDriver)
        }
    }
}

android {
    namespace = "com.mybaby.app.core.database"
    compileSdk = 35
    defaultConfig {
        minSdk = 26
    }
}

sqldelight {
    databases {
        create("PumDatabase") {
            packageName.set("com.mybaby.app.db")
        }
    }
}
