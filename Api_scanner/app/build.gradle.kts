plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.api_scanner"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.api_scanner"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    //Añadir buildFeatures para que sea mas facil acceder a las vistas
    // y no usar el id todo el rato
    buildFeatures{
        viewBinding=true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    viewBinding { enable=true }
    dataBinding {
        enable = true
    }
}
dependencies {
    // Implementaciones
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.zxing.android.embedded)  // ZXing
    implementation(libs.play.services.mlkit.barcode.scanning)  //  codigoBarras
    implementation(libs.retrofit)  // Retrofit
    implementation(libs.gson)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)  // Gson

    implementation (libs.converter.scalars)
    implementation (libs.retrofit) // Asegúrate de tener Retrofit
    implementation (libs.converter.scalars) // Agrega esta línea
    // Url Img
    //noinspection UseTomlInstead
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor (libs.compiler)
    // NAVEGACION
    //noinspection GradleDependency
    implementation (libs.androidx.navigation.fragment.ktx)
    implementation (libs.androidx.navigation.ui.ktx)
    // Desplazamiento de navegacion
    implementation (libs.androidx.viewpager2)
    //Material Component
    implementation (libs.material)
    // Buscador/Filtro Spinner
    implementation (libs.core)
    implementation(libs.androidx.navigation.ui.ktx)
    // Switch
    implementation (libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
