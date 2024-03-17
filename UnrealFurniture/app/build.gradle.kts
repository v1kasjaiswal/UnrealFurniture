plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

val tomtomApiKey: String by project

android {
    namespace = "com.vikasjaiswal.unrealfurniture"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.vikasjaiswal.unrealfurniture"
        minSdk = 24
        targetSdk = 34
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

    packagingOptions { resources.excludes.add("META-INF/*") }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.firebase:firebase-firestore:24.10.2")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.google.android.gms:play-services-location:21.1.0")
    implementation("androidx.activity:activity:1.8.0")
    implementation("com.google.firebase:firebase-messaging-ktx:23.4.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //External Implementation
    implementation("com.airbnb.android:lottie:6.2.0")
    implementation("com.github.colourmoon:readmore-textview:v1.0.2")
    implementation("com.github.dhaval2404:imagepicker:2.1")
    implementation("com.squareup.picasso:picasso:2.8")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.faltenreich:skeletonlayout:5.0.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("co.ankurg.expressview:expressview:0.0.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0")
    implementation("io.karn:notify:1.4.0")
}