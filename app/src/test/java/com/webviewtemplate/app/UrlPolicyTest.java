package com.webviewtemplate.app;

import static com.webviewtemplate.app.UrlPolicy.Decision.BLOCK;
import static com.webviewtemplate.app.UrlPolicy.Decision.LOAD_IN_APP;
import static com.webviewtemplate.app.UrlPolicy.Decision.OPEN_EXTERNAL;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import org.junit.Test;

public class UrlPolicyTest {

    private static UrlPolicy remote(String hosts) {
        return new UrlPolicy(UrlPolicy.Mode.REMOTE, "https://example.com/app", hosts,
                UrlPolicy.ExternalLinks.BROWSER);
    }

    @Test
    public void remote_defaultsToStartUrlHost() {
        UrlPolicy p = remote("");
        assertEquals(LOAD_IN_APP, p.decide("https://example.com/page?x=1"));
        assertEquals(LOAD_IN_APP, p.decide("https://EXAMPLE.com./Page"));
        assertEquals(LOAD_IN_APP, p.decide("https://example.com:8443/x"));
        assertEquals(OPEN_EXTERNAL, p.decide("https://other.org/"));
        assertEquals(OPEN_EXTERNAL, p.decide("https://example.com.evil.org/"));
        assertEquals(OPEN_EXTERNAL, p.decide("https://sub.example.com/"));
    }

    @Test
    public void remote_rejectsTrickyAndDangerousUrls() {
        UrlPolicy p = remote("");
        assertEquals(BLOCK, p.decide("https://example.com@evil.org/"));
        assertEquals(BLOCK, p.decide("https://example.com%2f@evil.org/"));
        assertEquals(BLOCK, p.decide("https:///nohost"));
        assertEquals(BLOCK, p.decide("https:example.com"));
        assertEquals(BLOCK, p.decide("javascript:alert(1)"));
        assertEquals(BLOCK, p.decide("file:///etc/passwd"));
        assertEquals(BLOCK, p.decide("content://com.android.contacts/contacts"));
        assertEquals(BLOCK, p.decide("data:text/html,hi"));
        assertEquals(BLOCK, p.decide("blob:https://example.com/1234"));
        assertEquals(BLOCK, p.decide("intent://scan/#Intent;scheme=zxing;end"));
        assertEquals(BLOCK, p.decide(""));
        assertEquals(BLOCK, p.decide(null));
        assertEquals(BLOCK, p.decide("not a url"));
    }

    @Test
    public void backslashIsTreatedLikeASlash() {
        UrlPolicy p = remote("");
        // Chromium reads this as host evil.org, so it must not match example.com
        assertEquals(OPEN_EXTERNAL, p.decide("https://evil.org\\@example.com/"));
    }

    @Test
    public void remote_cleartextAndOtherSchemes() {
        UrlPolicy p = remote("");
        assertEquals(OPEN_EXTERNAL, p.decide("http://example.com/"));
        assertEquals(OPEN_EXTERNAL, p.decide("mailto:a@example.com"));
        assertEquals(OPEN_EXTERNAL, p.decide("tel:+6751234567"));
        assertEquals(LOAD_IN_APP, p.decide("about:blank"));
    }

    @Test
    public void remote_assetHostIsNotAllowed() {
        assertEquals(BLOCK, remote("").decide("https://appassets.androidplatform.net/assets/www/index.html"));
    }

    @Test
    public void wildcardHosts() {
        UrlPolicy p = remote("*.example.org, login.idp.com");
        assertEquals(LOAD_IN_APP, p.decide("https://a.example.org/"));
        assertEquals(LOAD_IN_APP, p.decide("https://x.a.example.org/"));
        assertEquals(OPEN_EXTERNAL, p.decide("https://example.org/"));
        assertEquals(OPEN_EXTERNAL, p.decide("https://evilexample.org/"));
        assertEquals(LOAD_IN_APP, p.decide("https://login.idp.com/auth"));
        // explicit list replaces the start-URL default
        assertEquals(OPEN_EXTERNAL, p.decide("https://example.com/"));
    }

    @Test
    public void blockModeNeverOpensExternalApps() {
        UrlPolicy p = new UrlPolicy(UrlPolicy.Mode.REMOTE, "https://example.com/", "",
                UrlPolicy.ExternalLinks.BLOCK);
        assertEquals(BLOCK, p.decide("https://other.org/"));
        assertEquals(BLOCK, p.decide("mailto:a@b.c"));
        assertEquals(LOAD_IN_APP, p.decide("https://example.com/"));
    }

    @Test
    public void bundled_onlyAssetHostIsInApp() {
        UrlPolicy p = new UrlPolicy(UrlPolicy.Mode.BUNDLED, "", "example.com",
                UrlPolicy.ExternalLinks.BROWSER);
        assertEquals(LOAD_IN_APP, p.decide("https://appassets.androidplatform.net/assets/www/index.html"));
        assertEquals(OPEN_EXTERNAL, p.decide("https://example.com/")); // ALLOWED_HOSTS ignored
    }

    @Test
    public void hybrid_assetHostPlusBackendHosts() {
        UrlPolicy p = new UrlPolicy(UrlPolicy.Mode.HYBRID, "", "api.example.com",
                UrlPolicy.ExternalLinks.BROWSER);
        assertEquals(LOAD_IN_APP, p.decide("https://appassets.androidplatform.net/assets/www/index.html"));
        assertEquals(LOAD_IN_APP, p.decide("https://api.example.com/login"));
        assertEquals(OPEN_EXTERNAL, p.decide("https://other.org/"));
    }

    @Test
    public void parseHostsNormalizes() {
        assertEquals(Arrays.asList("a.com", "*.b.com", "c.org"),
                UrlPolicy.parseHosts(" A.com,*.B.com ;c.org. \n"));
        assertEquals(0, UrlPolicy.parseHosts(null).size());
        assertEquals(0, UrlPolicy.parseHosts("  ").size());
    }

    @Test
    public void bundledStartUrl() {
        String base = "https://appassets.androidplatform.net/assets/www/";
        assertEquals(base + "index.html", UrlPolicy.bundledStartUrl("index.html"));
        assertEquals(base + "app/home.html", UrlPolicy.bundledStartUrl("/app/home.html"));
        assertEquals(base + "index.html", UrlPolicy.bundledStartUrl("../secret"));
        assertEquals(base + "index.html", UrlPolicy.bundledStartUrl(""));
        assertEquals(base + "index.html", UrlPolicy.bundledStartUrl("https://evil.org/x"));
    }
}
