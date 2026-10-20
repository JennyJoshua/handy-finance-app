package com.webviewtemplate.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Decides what to do with a URL the WebView wants to navigate to.
 *
 * Deliberately free of Android imports so it can be unit-tested on the plain JVM.
 * The parser is strict and fails closed: anything it cannot understand is BLOCKed.
 */
public final class UrlPolicy {

    public enum Decision { LOAD_IN_APP, OPEN_EXTERNAL, BLOCK }

    public enum Mode { REMOTE, BUNDLED, HYBRID }

    public enum ExternalLinks { BROWSER, BLOCK }

    /** Virtual HTTPS host served by WebViewAssetLoader for bundled content. */
    public static final String ASSET_HOST = "appassets.androidplatform.net";

    private static final Pattern SCHEME = Pattern.compile("^([a-zA-Z][a-zA-Z0-9+.\\-]*):(.*)$", Pattern.DOTALL);

    private final Mode mode;
    private final ExternalLinks externalLinks;
    private final List<String> allowedHosts;

    public UrlPolicy(Mode mode, String startUrl, String allowedHostsCsv, ExternalLinks externalLinks) {
        this.mode = mode;
        this.externalLinks = externalLinks;

        List<String> hosts = new ArrayList<>();
        if (mode == Mode.REMOTE) {
            hosts.addAll(parseHosts(allowedHostsCsv));
            if (hosts.isEmpty()) {
                String startHost = hostOfHttps(startUrl);
                if (startHost != null) {
                    hosts.add(startHost);
                }
            }
        } else if (mode == Mode.HYBRID) {
            hosts.addAll(parseHosts(allowedHostsCsv));
        }
        // BUNDLED: ALLOWED_HOSTS is ignored on purpose; only the asset host is in-app.
        this.allowedHosts = Collections.unmodifiableList(hosts);
    }

    public List<String> getAllowedHosts() {
        return allowedHosts;
    }

    public Decision decide(String url) {
        if (url == null) {
            return Decision.BLOCK;
        }
        String trimmed = url.trim();
        if (trimmed.equalsIgnoreCase("about:blank")) {
            return Decision.LOAD_IN_APP;
        }
        Matcher m = SCHEME.matcher(trimmed);
        if (!m.matches()) {
            return Decision.BLOCK;
        }
        String scheme = m.group(1).toLowerCase(Locale.ROOT);
        String rest = m.group(2);

        switch (scheme) {
            case "https":
                return decideHttps(rest);
            case "http":
                // Cleartext never loads in the app; at most hand it to the browser.
                return external();
            case "mailto":
            case "tel":
            case "sms":
            case "smsto":
            case "geo":
                return external();
            default:
                // javascript:, file:, content:, data:, intent:, blob: ... are never allowed.
                return Decision.BLOCK;
        }
    }

    private Decision decideHttps(String afterScheme) {
        String host = extractHost(afterScheme);
        if (host == null) {
            return Decision.BLOCK;
        }
        if (host.equals(ASSET_HOST)) {
            return mode == Mode.REMOTE ? Decision.BLOCK : Decision.LOAD_IN_APP;
        }
        for (String pattern : allowedHosts) {
            if (matches(host, pattern)) {
                return Decision.LOAD_IN_APP;
            }
        }
        return external();
    }

    private Decision external() {
        return externalLinks == ExternalLinks.BROWSER ? Decision.OPEN_EXTERNAL : Decision.BLOCK;
    }

    /** Returns the normalized host of "//authority/..." or null if the URL looks unsafe/malformed. */
    static String extractHost(String afterScheme) {
        if (!afterScheme.startsWith("//")) {
            return null;
        }
        int end = afterScheme.length();
        for (int i = 2; i < afterScheme.length(); i++) {
            char c = afterScheme.charAt(i);
            // Chromium treats backslash like a slash in https URLs, so stop there too.
            if (c == '/' || c == '?' || c == '#' || c == '\\') {
                end = i;
                break;
            }
        }
        String authority = afterScheme.substring(2, end);
        if (authority.isEmpty()) {
            return null;
        }
        for (int i = 0; i < authority.length(); i++) {
            char c = authority.charAt(i);
            if (c <= 0x20 || c >= 0x7f || c == '@' || c == '%') {
                return null; // userinfo tricks, control characters, percent-encoded hosts
            }
        }
        String host;
        if (authority.startsWith("[")) {
            int close = authority.indexOf(']');
            if (close < 0) {
                return null;
            }
            host = authority.substring(0, close + 1);
            String tail = authority.substring(close + 1);
            if (!tail.isEmpty() && !isPort(tail)) {
                return null;
            }
        } else {
            int colon = authority.lastIndexOf(':');
            if (colon >= 0) {
                if (!isPort(authority.substring(colon))) {
                    return null;
                }
                host = authority.substring(0, colon);
            } else {
                host = authority;
            }
        }
        return normalizeHost(host);
    }

    private static boolean isPort(String colonAndDigits) {
        if (colonAndDigits.length() < 1 || colonAndDigits.charAt(0) != ':') {
            return false;
        }
        for (int i = 1; i < colonAndDigits.length(); i++) {
            if (!Character.isDigit(colonAndDigits.charAt(i))) {
                return false;
            }
        }
        return true; // an empty port ("host:") is allowed
    }

    static String normalizeHost(String host) {
        String h = host.toLowerCase(Locale.ROOT);
        while (h.endsWith(".")) {
            h = h.substring(0, h.length() - 1);
        }
        return h.isEmpty() ? null : h;
    }

    static boolean matches(String host, String pattern) {
        if (pattern.startsWith("*.")) {
            String suffix = pattern.substring(1); // ".example.com"
            return host.endsWith(suffix) && host.length() > suffix.length();
        }
        return host.equals(pattern);
    }

    public static List<String> parseHosts(String csv) {
        List<String> out = new ArrayList<>();
        if (csv == null) {
            return out;
        }
        for (String part : csv.split("[,;\\s]+")) {
            String p = part.trim().toLowerCase(Locale.ROOT);
            while (p.endsWith(".")) {
                p = p.substring(0, p.length() - 1);
            }
            if (!p.isEmpty()) {
                out.add(p);
            }
        }
        return out;
    }

    static String hostOfHttps(String url) {
        if (url == null) {
            return null;
        }
        Matcher m = SCHEME.matcher(url.trim());
        if (!m.matches() || !m.group(1).equalsIgnoreCase("https")) {
            return null;
        }
        return extractHost(m.group(2));
    }

    /** URL of the first page in bundled/hybrid mode, served by WebViewAssetLoader from assets/www/. */
    public static String bundledStartUrl(String entry) {
        String e = entry == null ? "" : entry.trim();
        while (e.startsWith("/")) {
            e = e.substring(1);
        }
        if (e.isEmpty() || e.contains("..") || e.contains("\\") || e.contains(":")) {
            e = "index.html";
        }
        return "https://" + ASSET_HOST + "/assets/www/" + e;
    }
}
