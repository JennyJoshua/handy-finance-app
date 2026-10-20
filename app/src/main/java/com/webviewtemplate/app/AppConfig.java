package com.webviewtemplate.app;

/** Thin bridge between generated BuildConfig values (from app-config.properties) and the code. */
final class AppConfig {

    private AppConfig() { }

    static UrlPolicy.Mode mode() {
        return UrlPolicy.Mode.valueOf(BuildConfig.LOAD_MODE);
    }

    static boolean usesBundledAssets() {
        return mode() != UrlPolicy.Mode.REMOTE;
    }

    static String startUrl() {
        return usesBundledAssets()
                ? UrlPolicy.bundledStartUrl(BuildConfig.BUNDLED_ENTRY)
                : BuildConfig.START_URL;
    }

    static UrlPolicy newPolicy() {
        return new UrlPolicy(
                mode(),
                BuildConfig.START_URL,
                BuildConfig.ALLOWED_HOSTS,
                UrlPolicy.ExternalLinks.valueOf(BuildConfig.EXTERNAL_LINKS));
    }
}
