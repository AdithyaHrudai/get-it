package com.getit.dto;

import com.getit.config.AppProperties;
import com.getit.model.UrlMapping;

import java.time.Instant;

/** Response returned by the API for a short link. */
public class UrlResponse {

    private final String shortCode;
    private final String shortUrl;    // clickable link that actually redirects
    private final String brandedUrl;  // vanity display, e.g. get.it/abc123
    private final String longUrl;
    private final Instant createdAt;
    private final long clickCount;

    public UrlResponse(UrlMapping m, AppProperties props) {
        this.shortCode = m.getShortCode();
        this.shortUrl = props.getBaseUrl() + "/" + m.getShortCode();
        this.brandedUrl = props.getDomain() + "/" + m.getShortCode();
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
