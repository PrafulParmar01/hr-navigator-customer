import com.android.build.gradle.internal.tasks.FinalizeBundleTask
import org.jetbrains.kotlin.util.capitalizeDecapitalize.capitalizeAsciiOnly
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id ("kotlin-android")
    id ("kotlin-parcelize")
    id ("kotlin-kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}


val keystorePropertiesFile = project.property("hrnavigatoruser.properties")
val props = Properties()
props.load(FileInputStream(file(keystorePropertiesFile as String)))
val buildDate: String = SimpleDateFormat("yyyy_MM_dd_HHmm").format(Date())

android {

    signingConfigs {
        create("release") {
            storeFile = file(props["keystore"] as String)
            storePassword = props["keystore.password"] as String
            keyAlias = props["keyAlias"] as String
            keyPassword = props["keyPassword"] as String
        }
    }

    namespace = "com.hr.navigator.customer"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.hr.navigator.customer"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }


    applicationVariants.all {
        val variant = this
        variant.outputs
            .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
            .forEach { output ->
                val outputFileName = "HR Navigator User_v$versionName($versionCode)_${buildDate}.${output.outputFile.extension}"
                output.outputFileName = outputFileName
            }

        val aabPackageName = "HR Navigator User-v$versionName($versionCode)_${buildDate}.aab"
        val bundleFinalizeTaskName = StringBuilder("sign").run {
            productFlavors.forEach {
                append(it.name.capitalizeAsciiOnly())
            }
            append(buildType.name.capitalizeAsciiOnly())
            append("Bundle")
            toString()
        }
        tasks.named(bundleFinalizeTaskName, FinalizeBundleTask::class.java) {
            val file = finalBundleFile.asFile.get()
            val finalFile = File(file.parentFile, aabPackageName)
            finalBundleFile.set(finalFile)
        }
    }

    buildTypes {
        debug {
            multiDexEnabled = true
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            multiDexEnabled = true
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
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

    implementation("com.intuit.sdp:sdp-android:1.0.6")
    implementation("com.intuit.ssp:ssp-android:1.0.6")
    implementation("com.google.code.gson:gson:2.10")

    //==============Android architecture component==============
    implementation ("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation ("androidx.lifecycle:lifecycle-common-java8:2.2.0")


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