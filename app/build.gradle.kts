import java.util.Properties

plugins {
    id("com.android.application")
}

// ---------------------------------------------------------------
// Configuration: app-config.properties < env WEBVIEW_<KEY> < -P<KEY>
// ---------------------------------------------------------------
fun fail(message: String): Nothing = throw GradleException("app-config.properties: $message")

val fileProps = Properties().apply {
    val f = rootProject.file("app-config.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}

fun cfg(key: String, default: String = ""): String {
    val fromProject = project.findProperty(key)?.toString()
    val fromEnv = System.getenv("WEBVIEW_$key")?.takeIf { it.isNotBlank() }
    return (fromProject ?: fromEnv ?: fileProps.getProperty(key) ?: default).trim()
}

fun cfgBool(key: String, default: Boolean): Boolean =
    when (val v = cfg(key, default.toString()).lowercase()) {
        "true" -> true
        "false" -> false
        else -> fail("$key must be true or false (got '$v')")
    }

val appName = cfg("APP_NAME", "WebView App")
if (!Regex("^[^<>&\"'@?%\\\\]{1,50}$").matches(appName)) {
    fail("APP_NAME must be 1-50 characters and must not contain < > & \" ' @ ? % or backslash")
}

val appId = cfg("APPLICATION_ID")
if (!Regex("^[a-z][a-z0-9_]*(\\.[a-z][a-z0-9_]*)+$").matches(appId)) {
    fail("APPLICATION_ID '$appId' is invalid. Use lowercase dotted form, e.g. com.mycompany.myapp")
}

val versionCodeValue = cfg("VERSION_CODE", "1").toIntOrNull()?.takeIf { it > 0 }
    ?: fail("VERSION_CODE must be a positive integer")
val versionNameValue = cfg("VERSION_NAME", "1.0.0")

val loadMode = cfg("LOAD_MODE", "REMOTE").uppercase()
if (loadMode !in listOf("REMOTE", "BUNDLED", "HYBRID")) {
    fail("LOAD_MODE must be REMOTE, BUNDLED or HYBRID (got '$loadMode')")
}

val startUrl = cfg("START_URL")
if (loadMode == "REMOTE" && !startUrl.startsWith("https://")) {
    fail("START_URL must start with https:// in REMOTE mode (got '$startUrl'). Cleartext HTTP is not supported.")
}

val bundledEntry = cfg("BUNDLED_ENTRY", "index.html")
val allowedHosts = cfg("ALLOWED_HOSTS")
val externalLinks = cfg("EXTERNAL_LINKS", "BROWSER").uppercase()
if (externalLinks !in listOf("BROWSER", "BLOCK")) {
    fail("EXTERNAL_LINKS must be BROWSER or BLOCK (got '$externalLinks')")
}

val themeColor = cfg("THEME_COLOR", "#1E88E5")
if (!Regex("^#[0-9A-Fa-f]{6}$").matches(themeColor)) {
    fail("THEME_COLOR must look like #1E88E5 (got '$themeColor')")
}

fun q(s: String) = "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\""

android {
    namespace = "com.webviewtemplate.app"
    compileSdk = 34

    defaultConfig {
        applicationId = appId
        minSdk = 26
        targetSdk = 34
        versionCode = versionCodeValue
        versionName = versionNameValue

        buildConfigField("String", "LOAD_MODE", q(loadMode))
        buildConfigField("String", "START_URL", q(startUrl))
        buildConfigField("String", "BUNDLED_ENTRY", q(bundledEntry))
        buildConfigField("String", "ALLOWED_HOSTS", q(allowedHosts))
        buildConfigField("String", "EXTERNAL_LINKS", q(externalLinks))
        buildConfigField("boolean", "JAVASCRIPT_ENABLED", cfgBool("JAVASCRIPT_ENABLED", true).toString())
        buildConfigField("boolean", "DOM_STORAGE_ENABLED", cfgBool("DOM_STORAGE_ENABLED", true).toString())
        buildConfigField("boolean", "COOKIES_ENABLED", cfgBool("COOKIES_ENABLED", true).toString())
        buildConfigField("boolean", "THIRD_PARTY_COOKIES", cfgBool("THIRD_PARTY_COOKIES", false).toString())
        buildConfigField("boolean", "FILE_UPLOADS_ENABLED", cfgBool("FILE_UPLOADS_ENABLED", true).toString())
        buildConfigField("boolean", "FILE_DOWNLOADS_ENABLED", cfgBool("FILE_DOWNLOADS_ENABLED", true).toString())

        resValue("string", "app_name", appName)
        resValue("color", "theme_color", themeColor)
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            // Release signing is intentionally not configured (no secrets in the repo).
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // Aligns kotlin-stdlib, -jdk7 and -jdk8 (AndroidX libraries pull in mixed Kotlin versions,
    // which otherwise fails :app:checkDebugDuplicateClasses). The app itself is Java.
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.22"))

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.webkit:webkit:1.11.0")
    implementation("androidx.core:core-splashscreen:1.0.1")

    testImplementation("junit:junit:4.13.2")
}
