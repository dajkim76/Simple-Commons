plugins {
    alias(libs.plugins.android)
    alias(libs.plugins.compose.compiler)
}



android {
    compileSdk = libs.versions.app.build.compileSDKVersion.get().toInt()

    defaultConfig {
        applicationId = libs.versions.app.version.appId.get()
        minSdk = libs.versions.app.build.minimumSDK.get().toInt()
        targetSdk = libs.versions.app.build.targetSDK.get().toInt()
        versionName = libs.versions.app.version.versionName.get()
        versionCode = libs.versions.app.version.versionCode.get().toInt()
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        val currentJavaVersionFromLibs = JavaVersion.valueOf(libs.versions.app.build.javaVersion.get().toString())
        sourceCompatibility = currentJavaVersionFromLibs
        targetCompatibility = currentJavaVersionFromLibs
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
        compose = true
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget(project.libs.versions.app.build.kotlinJVMTarget.get()))
            freeCompilerArgs.addAll(
                "-opt-in=kotlin.RequiresOptIn",
                "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                "-opt-in=androidx.compose.material.ExperimentalMaterialApi",
                "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
                "-Xcontext-parameters"
            )
        }
    }

    sourceSets {
        getByName("main").java.srcDirs("src/main/kotlin")
    }
    namespace = "com.simplemobiletools.commons.samples"

    lint {
        disable.add("Instantiatable")
    }
}

dependencies {
    implementation(projects.commons)
    implementation(libs.material)
    implementation(libs.androidx.swiperefreshlayout)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.lifecycle)
    implementation(libs.bundles.compose)
    debugImplementation(libs.bundles.composePreview)
}
