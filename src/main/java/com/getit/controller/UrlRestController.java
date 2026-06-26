package com.getit.controller;

import com.getit.config.AppProperties;
import com.getit.dto.CreateUrlRequest;
import com.getit.dto.UrlResponse;
import com.getit.model.UrlMapping;
import com.getit.service.UrlService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

/**
 * JSON REST API for managing short links.
 *
 *   POST   /api/shorten        create a short link
 *   GET    /api/urls/{code}    stats for one link
 *   GET    /api/urls           recent links (for the dashboard)
 *   DELETE /api/urls/{code}    remove a link
 */
@RestController
@RequestMapping("/api")
public class UrlRestController {

    private final UrlService urlService;
    private final AppProperties properties;

    public UrlRestController(UrlService urlService, AppProperties properties) {
        this.urlService = urlService;
        this.properties = properties;
    }

    @PostMapping("/shorten")
    @ResponseStatus(HttpStatus.CREATED)
    public UrlResponse shorten(@Valid @RequestBody CreateUrlRequest request) {
        UrlMapping mapping = urlService.createShortUrl(request.getUrl(), request.getAlias());
        return new UrlResponse(mapping, resolveBaseUrl(), properties.getDomain());
    }

    @GetMapping("/urls/{code}")
    public UrlResponse getOne(@PathVariable String code) {
        return new UrlResponse(urlService.getByCode(code), resolveBaseUrl(), properties.getDomain());
    }

    @GetMapping("/urls")
    public List<UrlResponse> listRecent(@RequestParam(defaultValue = "20") int limit) {
        String baseUrl = resolveBaseUrl();
        return urlService.listRecent(Math.min(Math.max(limit, 1), 100)).stream()
                .map(m -> new UrlResponse(m, baseUrl, properties.getDomain()))
                .toList();
    }

    @DeleteMapping("/urls/{code}")
    public ResponseEntity<Void> delete(@PathVariable String code) {
        urlService.delete(code);
        return ResponseEntity.noContent().build();
    }

    /**
     * The public base URL to build short links from. Prefers an explicit
     * {@code app.base-url} (e.g. a custom vanity domain); otherwise derives it
     * from the current request, so links are correct on localhost, Render, or
     * anywhere else with no configuration. Proxy headers (X-Forwarded-Proto/Host)
     * are honoured via {@code server.forward-headers-strategy}, so links served
     * behind Render's HTTPS proxy come out as {@code https://...}.
     */
    private String resolveBaseUrl() {
        String configured = properties.getBaseUrl();
        if (configured != null && !configured.isBlank()) {
            return configured;
        }
        return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    }
}
