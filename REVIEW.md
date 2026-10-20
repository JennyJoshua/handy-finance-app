# Project Review and Quality Checklist

## 1. Current Status

Status: Builds in GitHub Actions, installs on the owner's Android phone, and passed the owner's device tests for
REMOTE, BUNDLED and HYBRID modes, the error screen, the workflow form, uploads, downloads and link handling.

Nothing below is ticked unless it was actually checked or reported by the owner.

## 2. Repository Structure

- [x] All required Android source files are present. (scripts/validate-project.sh)
- [x] Root Gradle configuration is present.
- [x] settings.gradle.kts is present.
- [x] App-level Gradle configuration is present.
- [x] gradlew and gradlew.bat are present.
- [x] Gradle wrapper properties are present.
- [x] Gradle wrapper JAR is present (43,504-byte genuine JAR from the Gradle 8.9.0 source tag, valid ZIP).
- [x] AndroidManifest.xml is present.
- [x] Required resources and assets are present.

## 3. WebView Functionality

- [x] Remote HTTPS website mode works. (owner device test, 2026-10-20)
- [x] Bundled website mode works. (owner device test: sample page offline, local storage counter)
- [x] Hybrid frontend and backend mode is documented and tested. (owner device test with hybrid-test.html: bundled page called a public HTTPS API)
- [ ] Loading progress is displayed appropriately.
- [x] Android back navigation works. (owner device test)
- [x] Network errors are handled. (owner device test: airplane-mode error screen and Retry)
- [x] Navigation restrictions work as documented. (device: allowed host opened in the app, other host opened in the browser; policy also unit-tested)
- [x] Login and redirect behaviour is checked. (owner device test: login stayed in the app for the owner's site)
- [x] Cookies and storage work where required. (localStorage verified in bundled mode; cookies via the owner's site login)
- [x] File uploads and downloads are tested where supported. (device: file picker worked; GitHub ZIP download completed)
- [x] External links are handled appropriately. (device: non-allowed link opened in the browser app)

## 4. Security Review

- [x] HTTPS is the default (build fails for non-https START_URL in REMOTE mode).
- [x] TLS certificate errors are not bypassed (code review: handler.cancel()).
- [x] Cleartext HTTP is disabled by default (manifest + network security config).
- [x] Unsafe local-file access is avoided (code review).
- [x] JavaScript bridges are not unnecessarily exposed (none registered).
- [ ] Allowed-host restrictions are reviewed by the owner.
- [x] Secrets are not committed to the repository (validate script scan).
- [ ] Third-party licences are reviewed. (Dependencies are AndroidX, Apache-2.0; gradle-wrapper.jar is Apache-2.0. Owner review pending.)

## 5. GitHub Actions

- [x] Project validation workflow passes. (Android CI green on 2026-10-20 after the Kotlin BOM fix)
- [x] Automated tests pass. (the unit-test job is a prerequisite of the build job, which succeeded)
- [x] Debug APK builds successfully. (Android CI run after the fix)
- [x] Build failures are reported clearly. (observed on run 1: failing Gradle task and error text visible, failure summary step ran)
- [x] APK artifact is uploaded successfully. (upload step fails the run if no APK exists)
- [x] APK can be downloaded from the workflow run. (owner downloaded and installed it)

## 6. Android Device Testing

- [x] APK installs on a compatible Android device. (owner's phone; device model and Android version not recorded)
- [x] App launches successfully.
- [x] Website loading is verified.
- [x] Navigation is verified.
- [x] Error handling is verified.
- [x] No critical crashes are observed. (none reported during the owner's tests)

## 7. Documentation

- [x] README.md is complete.
- [x] PROJECT_REQUIREMENTS.md matches the implementation.
- [x] DECISIONS.md reflects approved decisions.
- [x] CHANGELOG.md records notable changes.
- [x] Setup instructions are tested. (owner followed the config, workflow-form and install steps)
- [ ] Website import instructions are tested.
- [x] Troubleshooting instructions are written.
- [x] Known limitations are documented.

## 8. Review Results

Date: 2026-10-20
Reviewer: Claude (sandbox checks and CI logs) plus owner device report
Build workflow URL: not recorded
Build result:
- Run 1 (2026-10-19) FAILED at :app:checkDebugDuplicateClasses (Kotlin stdlib 1.8.22 vs 1.6.21 conflict).
  Fixed by adding the kotlin-bom 1.8.22 platform dependency.
- Run after the fix: SUCCESS (reported by owner): validate, unit tests, debug APK build and artifact upload.
- gradle/actions confirmed the wrapper JAR is a known valid one (SHA-256 498495120a03...4f17).
Automated test result: unit-test job passed in CI (11 UrlPolicyTest cases; also run on a plain JVM during authoring).
Android installation result: APK installed on the owner's phone and was reported as working.

Owner device tests (2026-10-20), all passed: own site loads and back button works; airplane-mode error screen and Retry;
BUNDLED sample page works offline with local storage; workflow form built a second app (different name and ID)
installed beside the first; login stayed inside the app.

Second round of owner device tests (2026-10-20), all 5 passed, using the bundled hybrid-test.html page in HYBRID mode
(ALLOWED_HOSTS=github.com,codeload.github.com): backend API call from the bundled page; allowed-host link opened in the
app with back returning; non-allowed link opened in the browser app; file upload picker; file download via
DownloadManager.

Not yet verified: loading-progress bar appearance, website import instructions, a real custom backend with
login/cookies in HYBRID mode, third-party licence review, owner review of allowed-host restrictions.

Issues found:
- Kotlin stdlib duplicate-class conflict (fixed).

Required fixes:
- None known. Optional: verify the remaining items in section 3 when needed.

## 9. Release Decision

- [ ] All required acceptance criteria are satisfied.
- [ ] Critical security issues are resolved.
- [x] Documentation is complete.
- [x] A successful debug APK is available.

Release status: Approved for use as a template for REMOTE, BUNDLED and HYBRID modes (2026-10-20).
Debug APK only (no release signing). Hybrid was tested against a public API, not a custom backend with login.
