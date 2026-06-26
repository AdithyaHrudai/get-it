package com.getit.dto;

import com.getit.model.UrlMapping;

import java.time.Instant;

/** Response returned by the API for a short link. */
public class UrlResponse {

    private final String shortCode;
    private final String shortUrl;    // clickable link that actually redirects
    private final String brandedUrl;  // vanity display label, e.g. snip/abc123
    private final String longUrl;
    private final Instant createdAt;
    private final long clickCount;

    /**
     * @param baseUrl the public base URL the link is served from (e.g.
     *                {@code https://get-it.onrender.com}), resolved per request
     * @param domain  the vanity brand for display only (e.g. {@code snip})
     */
    public UrlResponse(UrlMapping m, String baseUrl, String domain) {
        this.shortCode = m.getShortCode();
        this.shortUrl = baseUrl + "/" + m.getShortCode();
        this.brandedUrl = domain + "/" + m.getShortCode();
        this.longUrl = m.getLongUrl();
        this.createdAt = m.getCreatedAt();
        this.clickCount = m.getClickCount();
    }

    public String getShortCode() {
        return shortCode;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public String getBrandedUrl() {
        return brandedUrl;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public long getClickCount() {
        return clickCount;
    }
}
