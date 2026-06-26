package com.getit.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * Generates random Base62 short codes (e.g. "a3Kf9Q").
 *
 * Base62 = digits + lower/upper letters, so codes stay URL-safe and compact.
 * With 6 characters there are 62^6 ≈ 56 billion possibilities, so random
 * collisions are extremely rare — and the service double-checks the database
 * for uniqueness anyway.
 */
@Component
public class ShortCodeGenerator {

    private static final String ALPHABET =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private final SecureRandom random = new SecureRandom();

    public String generate(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}
