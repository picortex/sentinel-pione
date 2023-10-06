plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("tz.co.asoft.library")
}

val generated = buildDir.resolve("generated/commonTest/kotlin").apply {
    if (!exists()) mkdirs()
}

kotlin {
    jvm { library() }
    js(IR) { library() }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.sentinel.registration.api.core)
                api(libs.pione.rest)
            }
        }

        val commonTest by getting {
            kotlin.srcDirs(generated)
            dependencies {
                implementation(projects.sentinelEnterpriseAuthenticationApiPione)
                implementation(libs.koncurrent.later.coroutines)
                implementation(libs.kommander.coroutines)
                implementation(libs.keep.mock)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(ktor.client.cio)
            }
        }
    }
}

afterEvaluate {
    val content = """
        package registra.pione
        
        val API_URL = "${System.getenv("API_URL") ?: ""}"
    """.trimIndent()
    println(content)
    generated.resolve("registra/pione/TestConfig.kt").apply {
        if (parentFile?.exists() == false) mkdir(parent)
        if (!exists()) createNewFile()
        writeText(content)
    }
}
