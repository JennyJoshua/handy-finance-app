# Implementation Handover — Android WebView App Template

## 1. Project Identity

Repository:
JennyJoshua/android-webview-app-template

Project type:
Reusable Android WebView application template.

Primary development environment:
GitHub, GitHub Actions, and an Android phone.

Implementation assistant:
Claude or another capable coding assistant.

## 2. Mission

Implement a complete, reusable Android application template for
packaging websites and web frontends into installable Android APKs.

The project must be reusable for future applications without
rewriting the Android infrastructure for every new project.

Read these documents before implementing:

- PROJECT_REQUIREMENTS.md
- DECISIONS.md
- CHANGELOG.md
- REVIEW.md

These documents define the requirements, approved decisions,
change history, and verification checklist.

## 3. Required Website Modes

### Mode A — Remote Website

Load a configurable HTTPS website inside Android WebView.

### Mode B — Bundled Website

Package HTML, CSS, JavaScript, images, and other frontend assets
inside the APK.

Use AndroidX WebKit WebViewAssetLoader where appropriate.

### Mode C — Hybrid Website

Bundle the frontend inside the APK while connecting to an online
backend, API, authentication service, or database.

Explain that bundling the frontend does not bundle the backend.

Implement these modes with a clear, documented configuration
mechanism.

## 4. Implementation Status (updated 2026-10-20)

Implemented: Android project, Gradle build and wrapper, three modes configured through
`app-config.properties`, WebView behaviour and security defaults, sample bundled site,
unit tests, `Android CI` workflow, and all documents listed in PROJECT_REQUIREMENTS.md section 8.

Verified: Android CI passes (validate, unit tests, debug APK, artifact). The APK installs on an Android phone.
Owner device tests passed for REMOTE mode, the error screen with Retry, BUNDLED mode offline with local storage,
the workflow form building a second app beside the first, and HYBRID mode, link handling, upload and download
(via the sample hybrid-test.html page). One build failure (Kotlin stdlib conflict) was found
and fixed with a kotlin-bom dependency.

Not yet tested: HYBRID mode with a custom backend using login/cookies; loading-progress bar appearance.
Debug APK only; release signing is not configured.

Next steps for whoever continues:
1. Test HYBRID mode against your own backend (CORS, cookies) when you have one.
2. Delete the old extension-less `PROJECT_REQUIREMENTS` file if it still exists.
