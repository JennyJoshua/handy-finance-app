# Changelog

All notable changes to this project will be documented here.

## [Unreleased]

### Added

- `app/src/main/assets/www/hybrid-test.html` and a link to it from the sample home page, for testing HYBRID mode,
  uploads, downloads and link handling on a device.

- `PROJECT_REQUIREMENTS.md` — project requirements and acceptance criteria.
- `DECISIONS.md` — approved initial project decisions (D001-D007) plus implementation choices (D008-D014).
- Complete Android project: Gradle build (AGP 8.5.2, Gradle 8.9), manifest, resources, Java sources.
- Gradle wrapper: `gradlew`, `gradlew.bat`, `gradle-wrapper.properties`, real `gradle-wrapper.jar`.
- `app-config.properties` — app name, application ID, load mode, start URL, allowed hosts,
  external link handling, theme colour, JavaScript/storage/cookie/upload/download switches.
- Three modes: REMOTE, BUNDLED (WebViewAssetLoader) and HYBRID.
- WebView: progress bar, back navigation, network and TLS error screen with retry,
  navigation allow-list (`UrlPolicy`), external link handling, file upload, downloads,
  splash screen, renderer-crash recovery.
- Security defaults: HTTPS only, TLS errors never bypassed, cleartext disabled, no JavaScript
  bridge, file access disabled, mixed content blocked, backups disabled.
- Sample bundled site in `app/src/main/assets/www/`.
- Unit tests for `UrlPolicy`.
- GitHub Actions workflow `android-build.yml`: validate, unit tests, debug APK, artifact upload.
- `scripts/validate-project.sh`.
- Documentation: README, docs/SETUP.md, docs/WEBVIEW_CONFIGURATION.md,
  docs/WEBSITE_IMPORT_GUIDE.md, docs/TROUBLESHOOTING.md.

### Fixed

- Build failed in `:app:checkDebugDuplicateClasses` (kotlin-stdlib 1.8.22 vs kotlin-stdlib-jdk7/jdk8 1.6.21).
  Added `kotlin-bom:1.8.22` platform dependency to align them.

### Verified

- Android CI passes (validate, unit tests, debug APK, artifact upload) and the APK installs on an Android phone.
- Owner device tests: REMOTE mode with own site (loading, back button, login stays in app), error screen and Retry,
  BUNDLED mode offline with local storage, and the workflow form building a second app side by side.
- Owner device tests with the new `hybrid-test.html` page: HYBRID backend call, in-app vs external link handling,
  file upload, file download (5 of 5 passed).

### Not yet verified

- HYBRID mode with a custom backend that uses login/cookies, and the website import instructions. See REVIEW.md.
