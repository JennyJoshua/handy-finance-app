# Android WebView App Template

Turn a website into an installable Android app (APK) using only GitHub and GitHub Actions.
No Android Studio needed.

## Three modes

| Mode | What it does | Set in `app-config.properties` |
|------|--------------|--------------------------------|
| REMOTE | Loads a live https website | `LOAD_MODE=REMOTE`, `START_URL=https://...` |
| BUNDLED | Website files live inside the APK (works offline) | `LOAD_MODE=BUNDLED`, files in `app/src/main/assets/www/` |
| HYBRID | Bundled frontend that calls an online backend/API | `LOAD_MODE=HYBRID`, backend hosts in `ALLOWED_HOSTS` |

Bundling a frontend does **not** bundle its backend. HYBRID apps need internet.

## Quick start

1. Edit `app-config.properties` (app name, application ID, mode, URL).
2. Commit to GitHub. The **Android CI** workflow runs.
3. Open the run on the **Actions** tab, then download the **debug-apk** artifact.
4. Unzip it and install `app-debug.apk` on your phone (allow "install unknown apps").

Or use **Actions > Android CI > Run workflow** and type the name, ID, mode and URL into the form.

Full steps: [docs/SETUP.md](docs/SETUP.md)

## Documentation

- [docs/SETUP.md](docs/SETUP.md) – upload, build, download, install
- [docs/WEBVIEW_CONFIGURATION.md](docs/WEBVIEW_CONFIGURATION.md) – every setting, icon, splash, security
- [docs/WEBSITE_IMPORT_GUIDE.md](docs/WEBSITE_IMPORT_GUIDE.md) – wrapping a site or a public GitHub project
- [docs/TROUBLESHOOTING.md](docs/TROUBLESHOOTING.md)
- [PROJECT_REQUIREMENTS.md](PROJECT_REQUIREMENTS.md), [DECISIONS.md](DECISIONS.md), [CHANGELOG.md](CHANGELOG.md), [REVIEW.md](REVIEW.md), [handoff.md](handoff.md)

## Status

Builds in GitHub Actions and installs on Android. REMOTE, BUNDLED and HYBRID modes, uploads and downloads passed device tests
(HYBRID against a public API). See [REVIEW.md](REVIEW.md).

## Known limitations

- Debug APK only; no release signing (needs secrets you must keep out of the repo).
- minSdk 26 (Android 8.0+); targetSdk 34 (fine for sideloading, Google Play needs a newer target).
- No camera, microphone or location access for web pages (denied by design).
- `blob:`/`data:` downloads, payments in pop-ups, and sites that block embedded WebViews
  (some Google/Facebook logins) may not work.
- The icon is a generic globe; replace it (see configuration guide).
- Only main-frame navigation is restricted; embedded iframes and page subresources are not.
- A WebView wrapper does not convert desktop programs into Android apps.
- Check the licence of any third-party site or project before packaging it.
