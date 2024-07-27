plugins {
    alias(libs.plugins.android.application)
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "sg.edu.np.mad.p04_team4"
    compileSdk = 34

    defaultConfig {
        applicationId = "sg.edu.np.mad.p04_team4"
        minSdk = 21
        targetSdk = 34
        versionCode = 10 //update version code
        versionName = "1.9" //update version name

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.recyclerview)
    implementation(libs.picasso)
    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:28.4.0"))

    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-auth-ktx:21.0.1")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-database:20.0.5")
    implementation("com.google.firebase:firebase-auth:21.0.1")
    // Add the dependencies for any other desired Firebase products

    // Add MPAndroidChart dependency
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation("com.github.google:flexbox-layout:3.0.0")
    implementation ("com.github.bumptech.glide:glide:4.13.2")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.13.2")
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    implementation("androidx.room:room-runtime:2.3.0")
    annotationProcessor("androidx.room:room-compiler:2.3.0")
    implementation("androidx.room:room-ktx:2.3.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.0")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.room:room-runtime:2.3.0")
    annotationProcessor("androidx.room:room-compiler:2.3.0")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.room:room-runtime:2.3.0")
    annotationProcessor("androidx.room:room-compiler:2.3.0")
    implementation("androidx.room:room-rxjava2:2.3.0")
    implementation("androidx.room:room-guava:2.3.0")
    testImplementation("androidx.room:room-testing:2.3.0")
    implementation ("androidx.recyclerview:recyclerview:1.2.1")
    implementation ("com.squareup.okhttp3:okhttp:4.10.0")

}