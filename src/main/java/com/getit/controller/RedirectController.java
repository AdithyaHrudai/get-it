package com.getit.controller;

import com.getit.exception.ResourceNotFoundException;
import com.getit.service.UrlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * Handles the actual redirect: GET /{code} -> 302 Found to the long URL.
 *
 * The path pattern only matches a single segment of letters/digits/-/_ (no
 * dots), so it never swallows static files like /index.html, /style.css or
 * /favicon.ico — those keep being served by Spring's static resource handler.
 */
@RestController
public class RedirectController {

    private final UrlService urlService;

    public RedirectController(UrlService urlService) {
        this.urlService = urlService;
    }

    @GetMapping("/{code:[A-Za-z0-9_-]{1,16}}")
    public ResponseEntity<String> redirect(@PathVariable String code) {
        try {
            String longUrl = urlService.resolveAndCount(code);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(longUrl))
                    .build();
        } catch (ResourceNotFoundException e) {
            // Friendly HTML for humans who follow a dead/expired link.
            String html = "<!doctype html><html><head><meta charset='utf-8'>"
                    + "<title>Snip — link not found</title></head>"
                    + "<body style=\"font-family:system-ui;text-align:center;margin-top:15vh;color:#334\">"
                    + "<h1 style=\"font-size:3rem;margin:0\">404</h1>"
                    + "<p>No short link exists for <code>/" + escape(code) + "</code>.</p>"
                    + "<p><a href=\"/\">← Make a new short link</a></p>"
                    + "</body></html>";
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.TEXT_HTML)
                    .body(html);
        }
    }

    private static String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
