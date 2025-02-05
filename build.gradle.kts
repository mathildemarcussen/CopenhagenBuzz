// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "dk.itu.moapd.copenhagenbuzz.msem"
    compileSdk = 35

    defaultConfig {
        applicationId = "dk.itu.moapd.copenhagenbuzz.msem"
        minSdk = 27
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

