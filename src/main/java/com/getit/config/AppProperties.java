package com.getit.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * App-specific settings, bound from the {@code app.*} keys in
 * application.properties. Centralising them here keeps magic strings out of the
 * code and makes the short-link format easy to change.
 */
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    /** Public base URL that actually serves redirects, e.g. http://localhost:8080. */
    private String baseUrl = "http://localhost:8080";

    /** Vanity domain shown to users, e.g. get.it -> "get.it/abc123". */
    private String domain = "get.it";

    /** Number of characters in a generated short code. */
    private int shortCodeLength = 6;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public int getShortCodeLength() {
        return shortCodeLength;
    }

    public void setShortCodeLength(int shortCodeLength) {
        this.shortCodeLength = shortCodeLength;
    }
}
