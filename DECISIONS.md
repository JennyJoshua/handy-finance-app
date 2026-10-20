# Project Decisions — Android WebView App Template

## Status

Initial decisions approved for implementation. Decisions D008-D014 were made during
implementation inside the approved scope and are awaiting owner review.

## D001 — Reusable Android Template

Build a general-purpose Android WebView template that can be
reused for multiple website-to-Android app projects.

Do not build the template around one specific website.

## D002 — Three Website Modes

Support these application modes:

1. Remote website loaded from an HTTPS URL.
2. Website frontend bundled inside the APK.
3. Bundled frontend connected to an online backend or API.

Document the differences and limitations of each mode.

## D003 — Phone-First Development

The owner primarily manages the project using an Android phone,
GitHub, and GitHub Actions.

Do not require Android Studio for routine template configuration,
building, or APK downloading.

## D004 — GitHub Actions Builds

Use GitHub Actions to validate, test, and build the Android APK.

The basic debug-build workflow must not require paid services,
private API keys, or signing secrets.

## D005 — Complete Gradle Wrapper

Commit every required Gradle wrapper file, including:

- gradlew
- gradlew.bat
- gradle/wrapper/gradle-wrapper.properties
- gradle/wrapper/gradle-wrapper.jar

Never assume that phone uploads preserve hidden or nested files.

## D006 — Secure WebView Defaults

Use HTTPS by default.

Do not bypass TLS errors or enable cleartext HTTP by default.
Avoid unsafe file access and unrestricted JavaScript bridges.
Use AndroidX WebKit WebViewAssetLoader for bundled content
where appropriate.

## D007 — Configuration Must Be Documented

Make common customizations straightforward:

- App name
- Application ID
- App icon
- Splash screen
- Website

## D008 — One configuration file (implementation choice)

All common settings live in `app-config.properties`. Any key can be overridden by a
Gradle property (`-PKEY=value`) or an environment variable (`WEBVIEW_KEY`), which is how
the GitHub Actions "Run workflow" form changes the app without editing files. Invalid
values fail the build with a clear message.

## D009 — Java, no Kotlin plugin (implementation choice)

The app is written in Java to keep the dependency set minimal (D-rule: avoid unnecessary
dependencies). Runtime dependencies: appcompat, androidx.webkit, core-splashscreen.

## D010 — Toolchain versions (implementation choice)

Gradle 8.9, Android Gradle Plugin 8.5.2, JDK 17 in CI, compileSdk/targetSdk 34,
**minSdk 26** (Android 8.0). minSdk 26 lets the launcher icon be a single adaptive
vector icon with no PNG files. Lowering minSdk needs legacy PNG icons.
Newer targetSdk values are needed for Google Play, not for sideloaded debug APKs.

## D011 — Navigation policy (implementation choice)

Implemented in `UrlPolicy` (pure Java, unit-tested). Only https hosts in the allow-list
(and the asset host in BUNDLED/HYBRID) load in the app. Other https links open in the
browser or are blocked (`EXTERNAL_LINKS`). `javascript:`, `file:`, `content:`, `data:`,
`blob:` and `intent:` are always blocked. URLs with embedded credentials, percent-encoded
hosts or control characters are blocked. Checks apply to main-frame navigations.

## D012 — Debug builds only (implementation choice)

The template builds a debug APK. Release signing is not configured because it needs
secrets; see docs/SETUP.md for the recommended approach if the owner wants it later.

## D013 — Downloads (implementation choice)

Downloads use Android's DownloadManager (https only, no storage permission). On
Android 8-9 (API below 29) the link is handed to the browser instead. `blob:` and
`data:` downloads are not supported.

## D014 — Requirements file name (implementation choice)

The repository holds `PROJECT_REQUIREMENTS` without an extension. `PROJECT_REQUIREMENTS.md`
with identical content is provided; the old file can be deleted.
