// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    id("com.google.dagger.hilt.android") version "2.59.1" apply false
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
    id("org.owasp.dependencycheck") version "12.2.2"

}
allprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")

    detekt {
        config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
        buildUponDefaultConfig = true
        ignoreFailures = true 
    }

    tasks.matching { it.name.startsWith("extract") && it.name.endsWith("Annotations") }.configureEach {
        val variantName = this.name.removePrefix("extract").removeSuffix("Annotations")
        val kspTaskName = "ksp${variantName}Kotlin"
        
        dependsOn(tasks.matching { it.name == kspTaskName })
    }
}

dependencyCheck {
    // failBuildOnCVSS = 7.0f 

    nvd {
        apiKey = System.getenv("NVD_API_KEY") 
    }
    
    // Optional: Suppress false positives by generating a suppressions file
    // suppressionFile = "$rootDir/config/dependency-check-suppressions.xml"
}
