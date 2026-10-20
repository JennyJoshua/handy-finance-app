# WebView configuration

All settings are in `app-config.properties`. Any key can be overridden with `-PKEY=value`
(Gradle) or the environment variable `WEBVIEW_KEY` (this is what the workflow form uses).
Wrong values stop the build with a message starting `app-config.properties:`.

| Key | Values | Notes |
|-----|--------|-------|
| `APP_NAME` | 1–50 chars | Not allowed: `< > & " ' @ ? %` and backslash |
| `APPLICATION_ID` | e.g. `com.school.myapp` | Lowercase, dotted, at least two parts. Changing it installs as a different app |
| `VERSION_CODE` / `VERSION_NAME` | integer / text | Increase VERSION_CODE for each update |
| `LOAD_MODE` | `REMOTE`, `BUNDLED`, `HYBRID` | See below |
| `START_URL` | `https://...` | REMOTE only. `http://` is rejected |
| `BUNDLED_ENTRY` | `index.html` | BUNDLED/HYBRID first page, relative to `assets/www/` |
| `ALLOWED_HOSTS` | comma list | `example.com` exact; `*.example.com` subdomains only |
| `EXTERNAL_LINKS` | `BROWSER`, `BLOCK` | What happens to links outside the allow-list |
| `THEME_COLOR` | `#RRGGBB` | Status bar, splash, progress bar, icon background |
| `JAVASCRIPT_ENABLED` | true/false | Most sites need true |
| `DOM_STORAGE_ENABLED` | true/false | localStorage; most modern sites need true |
| `COOKIES_ENABLED` / `THIRD_PARTY_COOKIES` | true/false | Third-party off by default |
| `FILE_UPLOADS_ENABLED` / `FILE_DOWNLOADS_ENABLED` | true/false | |

## Modes

**REMOTE** – loads `START_URL`. In-app hosts = `ALLOWED_HOSTS`, or the host of `START_URL` if empty.
Needs internet. For logins that redirect to another host (for example an identity provider),
add that host to `ALLOWED_HOSTS` or login will open in the browser and not return to the app.
Remember to list the site's own host too when you set the list.

**BUNDLED** – serves `app/src/main/assets/www/` through WebViewAssetLoader at
`https://appassets.androidplatform.net/assets/www/`. No file:// access is used. `ALLOWED_HOSTS`
is ignored; every other link goes to `EXTERNAL_LINKS`. Works offline if the site needs no server.
Use relative links (`about.html`), not absolute paths like `/about.html` unless you include the `/assets/www/` prefix.

**HYBRID** – same as BUNDLED, plus `ALLOWED_HOSTS` may open in the app. The page's own
`fetch`/XHR calls are not filtered by this app. Your backend must:
- serve over HTTPS,
- allow CORS from origin `https://appassets.androidplatform.net`,
- set cookies with `SameSite=None; Secure` if the app relies on third-party cookies
  (and then set `THIRD_PARTY_COOKIES=true`).
The app needs internet for backend features. Bundling the frontend does not bundle the backend.

## Navigation rules (UrlPolicy)

- https + allowed host → opens in the app. https + other host → browser or blocked.
- http → never in the app (browser or blocked). mailto/tel/sms/geo → external app or blocked.
- javascript:, file:, content:, data:, blob:, intent: → always blocked.
- URLs with `user@` credentials, `%` in the host, control characters, or a malformed host → blocked.
- Applies to top-level page navigations, including redirects.

## Icon

The icon is an adaptive vector: background = `THEME_COLOR`, foreground =
`app/src/main/res/drawable/ic_launcher_foreground.xml`. To use your own artwork, delete that XML
and add a transparent PNG named `ic_launcher_foreground.png` in `app/src/main/res/drawable/`
(108×108 dp canvas; keep the logo inside the central 66×66). It is also used on the splash screen.

## Splash screen

Android's SplashScreen API: background = `THEME_COLOR`, icon = the foreground drawable above.
Change colours in `app/src/main/res/values/themes.xml` (`Theme.App.Starting`).

## Security defaults

HTTPS only; certificate errors are never bypassed (the error screen is shown); cleartext disabled in
manifest and network security config; `allowBackup=false`; file/content access disabled; mixed content
blocked; Safe Browsing on; no JavaScript-to-native bridge; camera, microphone and location requests
from pages are denied. Never commit passwords, API keys, keystores or `.env` files — the validate
script fails if it finds common ones.

## Offline

BUNDLED mode works offline by nature. REMOTE mode has no offline cache; a site's own service
worker may provide one if the site supports it.
