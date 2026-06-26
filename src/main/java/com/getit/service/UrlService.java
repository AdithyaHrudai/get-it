package com.getit.service;

import com.getit.config.AppProperties;
import com.getit.exception.BadRequestException;
import com.getit.exception.ResourceNotFoundException;
import com.getit.model.UrlMapping;
import com.getit.repository.UrlMappingRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;

/**
 * The brains of the app: turn long URLs into short codes, and resolve short
 * codes back to long URLs (counting visits along the way).
 */
@Service
public class UrlService {

    private static final int MAX_CODE_ATTEMPTS = 5;

    private final UrlMappingRepository repository;
    private final ShortCodeGenerator codeGenerator;
    private final AppProperties properties;

    public UrlService(UrlMappingRepository repository,
                      ShortCodeGenerator codeGenerator,
                      AppProperties properties) {
        this.repository = repository;
        this.codeGenerator = codeGenerator;
        this.properties = properties;
    }

    /** Create a short link for the given URL, optionally with a custom alias. */
    @Transactional
    public UrlMapping createShortUrl(String rawUrl, String alias) {
        String longUrl = normalizeAndValidate(rawUrl);
        String code = (alias == null || alias.isBlank())
                ? generateUniqueCode()
                : claimAlias(alias.trim());
        return repository.save(new UrlMapping(code, longUrl));
    }

    /** Look up a short link (no side effects). */
    @Transactional(readOnly = true)
    public UrlMapping getByCode(String code) {
        return repository.findByShortCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("No short link found for code: " + code));
    }

    /** Resolve a code to its long URL and count the visit. */
    @Transactional
    public String resolveAndCount(String code) {
        UrlMapping mapping = repository.findByShortCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("No short link found for code: " + code));
        mapping.registerClick();
        return mapping.getLongUrl();
    }

    /** Most recent links, for the dashboard. */
    @Transactional(readOnly = true)
    public List<UrlMapping> listRecent(int limit) {
        return repository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, limit)).getContent();
    }

    @Transactional
    public void delete(String code) {
        UrlMapping mapping = repository.findByShortCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("No short link found for code: " + code));
        repository.delete(mapping);
    }

    // --- helpers ------------------------------------------------------

    private String generateUniqueCode() {
        for (int attempt = 0; attempt < MAX_CODE_ATTEMPTS; attempt++) {
            String code = codeGenerator.generate(properties.getShortCodeLength());
            if (!repository.existsByShortCode(code)) {
                return code;
            }
        }
        throw new IllegalStateException("Could not generate a unique short code; try increasing app.short-code-length");
    }

    private String claimAlias(String alias) {
        if (repository.existsByShortCode(alias)) {
            throw new BadRequestException("Alias '" + alias + "' is already taken");
        }
        return alias;
    }

    /**
     * Accept user-friendly input ("example.com") by defaulting the scheme to
     * https, then validate that the result is a real http(s) URL with a host.
     */
    private String normalizeAndValidate(String rawUrl) {
        String url = rawUrl.trim();
        if (!url.matches("(?i)^https?://.*")) {
            url = "https://" + url;
        }
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme() == null ? "" : uri.getScheme().toLowerCase(Locale.ROOT);
            if (!scheme.equals("http") && !scheme.equals("https")) {
                throw new BadRequestException("Only http and https URLs are supported");
            }
            if (uri.getHost() == null || !uri.getHost().contains(".")) {
                throw new BadRequestException("'" + rawUrl + "' is not a valid URL");
            }
        } catch (URISyntaxException e) {
            throw new BadRequestException("'" + rawUrl + "' is not a valid URL");
        }
        return url;
    }
}
