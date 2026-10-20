# Troubleshooting

| Problem | What to do |
|---------|-----------|
| Validate job: "missing required file" | Upload the file named in the message, keeping its folder path |
| Validate job: "gradle-wrapper.jar ... placeholder / not a valid JAR" | The JAR was lost or corrupted during upload. Re-upload the real file from the ZIP |
| `./gradlew: Permission denied` | The workflow already runs `chmod +x`. If you run it elsewhere: `chmod +x gradlew` |
| Build fails: `app-config.properties: ...` | Fix the value named in the message (see WEBVIEW_CONFIGURATION.md) |
| Build fails: "SDK location not found" | Only on your own computer; GitHub runners have the SDK. Set `ANDROID_HOME` |
| Build fails: dependency cannot be resolved | Usually a network blip; re-run the job. Persisting errors: copy the log into a new chat for help |
| Unit tests fail | Download the **unit-test-reports** artifact from the run |
| Artifact missing | The build job failed before upload; open it and read the failing step |
| APK will not install | Allow "Install unknown apps"; uninstall an older copy that has a different signing key; needs Android 8.0+ |
| App shows "page could not be loaded" | No internet, wrong URL, or server down. Tap Retry |
| App shows "secure connection could not be established" | The site's certificate is invalid/expired or the phone clock is wrong. The app deliberately does not bypass this |
| Link opens in the browser instead of the app | The host is not in `ALLOWED_HOSTS` |
| Login bounces to the browser | Add the identity provider's host to `ALLOWED_HOSTS` |
| "This link is blocked" | `javascript:`, `file:`, `data:` and similar links, or `EXTERNAL_LINKS=BLOCK` |
| Bundled page is blank or CSS missing | Check file names/case and use relative links; the entry file must exist in `assets/www/` |
| Backend calls fail in HYBRID | CORS (allow origin `https://appassets.androidplatform.net`), https only, cookie `SameSite` settings |
| Download does nothing | Only https downloads are supported; `blob:` downloads are not |
| Icon/splash did not change | Rebuild and reinstall; Android caches launcher icons |

When asking for help, include: the run URL, the failing step name, and the last ~30 lines of its log.
