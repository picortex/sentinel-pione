import java.io.File

pluginManagement {
    includeBuild("../build-logic")
}

plugins {
    id("multimodule")
}

fun includeSubs(base: String, path: String = base, vararg subs: String) {
    subs.forEach {
        include(":$base-$it")
        project(":$base-$it").projectDir = File("$path/$it")
    }
}

listOf(
    "cinematic", "keep", "lexi", "captain", "neat", "kash-api", "geo-api", "kase",
    "kash-client", "geo-client", "sentinel-core", "sentinel-client",
    "kronecker", "symphony", "epsilon-api", "epsilon-client", "krono-core", "hormone", "identifier-api",
    "kommerce", "kollections", "koncurrent", "kommander", "cabinet-api", "cabinet-picortex", "pione", "snitch"
).forEach { includeBuild("../$it") }

rootProject.name = "sentinel-pione"

includeSubs(base = "sentinel-registration-api", path = "registration/api", "pione")
includeSubs(base = "sentinel-enterprise-authentication-api", path = "enterprise/authentication/api", "pione")
includeSubs(base = "sentinel-enterprise-profile-api", path = "enterprise/profile/api", "pione")
