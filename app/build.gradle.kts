plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id ("kotlin-android")
    id ("kotlin-parcelize")
    id ("kotlin-kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.hr.navigator.customer"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.hr.navigator.customer"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures{
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    implementation("com.google.android.material:material:1.4.0")

    implementation(platform("com.google.firebase:firebase-bom:32.2.3"))
    implementation("com.google.firebase:firebase-crashlytics:18.6.0")
    implementation("com.google.firebase:firebase-analytics:21.5.0")
    implementation("com.google.firebase:firebase-database")


    implementation("com.jakewharton.rxbinding3:rxbinding:3.1.0")

    implementation("com.intuit.sdp:sdp-android:1.0.6")
    implementation("com.intuit.ssp:ssp-android:1.0.6")
    implementation("com.google.code.gson:gson:2.10")

    //==============Android architecture component==============
    implementation ("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation ("androidx.lifecycle:lifecycle-common-java8:2.2.0")

    //==============RxJava==============
    implementation ("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation ("io.reactivex.rxjava2:rxjava:2.2.20")
    implementation ("com.airbnb.android:lottie:6.3.0")
    implementation("com.github.aabhasr1:OtpView:v1.1.2")

    implementation("org.greenrobot:eventbus:3.3.1")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("com.jakewharton.timber:timber:4.7.1")

    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation("com.guolindev.permissionx:permissionx:1.7.1")

    implementation("androidx.preference:preference:1.1.0")
    implementation("com.google.firebase:firebase-auth-ktx:22.3.1")
}