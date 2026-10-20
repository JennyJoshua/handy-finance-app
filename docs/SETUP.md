# Setup

## What you need

A GitHub account and a phone browser. Nothing else.

## 1. Put the project in the repository

The repository must contain the files from the ZIP with their folder structure, including the
hidden `.github` folder and `gradle/wrapper/gradle-wrapper.jar`.

Phone uploads often lose folders. Safest ways:
- Use your existing `_inbox` ZIP-extraction automation if it accepts a `.github/` folder, or
- Upload the ZIP's contents folder by folder, and create `.github/workflows/android-build.yml`
  in the GitHub web editor (**Add file > Create new file**, type the path with slashes).

Afterwards run the check: on the Actions tab the first job (**Validate project files**) tells
you exactly which file is missing.

Executable bit: `gradlew` may lose it when uploaded. The workflow runs `chmod +x gradlew`, so CI is not affected.

## 2. Configure

Edit `app-config.properties` in the GitHub web editor (pencil icon). See
[WEBVIEW_CONFIGURATION.md](WEBVIEW_CONFIGURATION.md).

## 3. Build

Every push to `main` builds automatically. To build on demand with different settings:

1. **Actions** tab > **Android CI** > **Run workflow**.
2. Fill in any of: app name, application ID, load mode, start URL, allowed hosts. Empty fields use `app-config.properties`.
3. Tap **Run workflow**.

The workflow runs three jobs: validate, unit tests, build debug APK.

## 4. Download the APK

1. Open the finished run (green tick).
2. Scroll to **Artifacts** and tap **debug-apk**. A ZIP downloads (artifact download needs you signed in to GitHub).
3. Extract the ZIP with your Files app. Open `app-debug.apk` to install; allow "Install unknown apps" for your browser/Files app if asked.

If a job is red, open it and read the failing step. See [TROUBLESHOOTING.md](TROUBLESHOOTING.md).

## Optional: release signing

Not included. If you later need a signed release, store the keystore as base64 in
**Settings > Secrets and variables > Actions** and never commit it. This is a separate task.
