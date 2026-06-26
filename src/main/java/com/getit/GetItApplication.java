package com.getit;

import com.getit.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * get.it — a tiny URL shortener.
 *
 * Paste a long URL, get a short code back (get.it/abc123), and every visit to
 * that code redirects to the original URL.
 */
@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class GetItApplication {

    public static void main(String[] args) {
        SpringApplication.run(GetItApplication.class, args);
    }
}
