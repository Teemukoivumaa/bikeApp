plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    //id("com.google.devtools.ksp") version "2.0.21-1.0.25"

    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")

    id("kotlin-kapt")
    id("com.google.devtools.ksp")
    id("dagger.hilt.android.plugin")
}

secrets {
    propertiesFileName = "secrets.properties"

    defaultPropertiesFileName = "local.defaults.properties"
}

android {
    namespace = "com.example.bikeapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.bikeapp"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = file("schemas").absolutePath
            }
        }
    }

    buildTypes {
        debug {
            buildConfigField(
                "String",
                "STRAVA_CLIENT_ID",
                "\"${project.properties["STRAVA_CLIENT_ID"]}\""
            )
            buildConfigField(
                "String",
                "STRAVA_REDIRECT_URI",
                "\"${project.properties["STRAVA_REDIRECT_URI"]}\""
            )
            buildConfigField(
                "String",
                "STRAVA_CLIENT_SECRET",
                "\"${project.properties["STRAVA_CLIENT_SECRET"]}\""
            )
            buildConfigField(
                "String",
                "STRAVA_REFRESH_TOKEN",
                "\"${project.properties["STRAVA_REFRESH_TOKEN"]}\""
            )
            buildConfigField(
                "String",
                "MAPS_API_KEY",
                "\"${project.properties["MAPS_API_KEY"]}\""
            )
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField(
                "String",
                "STRAVA_CLIENT_ID",
                "\"${project.properties["STRAVA_CLIENT_ID"]}\""
            )
            buildConfigField(
                "String",
                "STRAVA_REDIRECT_URI",
                "\"${project.properties["STRAVA_REDIRECT_URI"]}\""
            )
            buildConfigField(
                "String",
                "STRAVA_CLIENT_SECRET",
                "\"${project.properties["STRAVA_CLIENT_SECRET"]}\""
            )
            buildConfigField(
                "String",
                "STRAVA_REFRESH_TOKEN",
                "\"${project.properties["STRAVA_REFRESH_TOKEN"]}\""
            )
            buildConfigField(
                "String",
                "MAPS_API_KEY",
                "\"${project.properties["MAPS_API_KEY"]}\""
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    sourceSets {
        getByName("main") {
            java {
                srcDirs("src\\main\\java", "src\\main\\java\\2")
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.room.runtime.v261)
    implementation(libs.androidx.room.ktx.v261)
    implementation(libs.androidx.navigation.compose)
    ksp(libs.androidx.room.compiler)

    implementation("com.google.maps.android:maps-compose:6.5.3")
    implementation("androidx.compose.ui:ui-text-google-fonts:1.7.8")
    implementation("com.google.code.gson:gson:2.10.1")

    implementation ("com.google.dagger:hilt-android:2.56.1")
    kapt("com.google.dagger:hilt-compiler:2.56.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}