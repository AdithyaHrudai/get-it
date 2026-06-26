package com.getit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Request body for POST /api/shorten. */
public class CreateUrlRequest {

    @NotBlank(message = "url is required")
    @Size(max = 2048, message = "url is too long")
    private String url;

    /** Optional custom code, e.g. "my-link" -> snip/my-link. */
    @Size(min = 3, max = 16, message = "alias must be 3-16 characters")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "alias may only contain letters, digits, '-' and '_'")
    private String alias;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
