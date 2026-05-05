plugins {
    alias(libs.plugins.android.application)
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.emad.phishingdetector"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.emad.phishingdetector"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildFeatures {
        viewBinding = true
    }

    lint {
        abortOnError = false // Lint will print warnings/errors but the build will succeed
        checkReleaseBuilds = true
        xmlReport = true
        htmlReport = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(project(":domain"))
    implementation(project(":data"))

    // Hilt (Dependency Injection)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // Room (Needed because AppModule builds the database)
    implementation(libs.androidx.room.runtime)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    implementation(libs.androidx.fragment.ktx)

    // Google Sign-In
    implementation(libs.play.services.auth)
    // swipe to refresh
    implementation(libs.androidx.swiperefreshlayout)

    // coil for images
    implementation(libs.coil)
}