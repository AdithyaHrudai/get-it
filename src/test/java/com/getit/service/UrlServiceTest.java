package com.getit.service;

import com.getit.exception.BadRequestException;
import com.getit.exception.ResourceNotFoundException;
import com.getit.model.UrlMapping;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class UrlServiceTest {

    @Autowired
    private UrlService urlService;

    @Test
    void createsShortCodeAndResolvesBack() {
        UrlMapping mapping = urlService.createShortUrl("https://spring.io/projects/spring-boot", null);

        assertNotNull(mapping.getShortCode());
        assertEquals(6, mapping.getShortCode().length());

        String resolved = urlService.resolveAndCount(mapping.getShortCode());
        assertEquals("https://spring.io/projects/spring-boot", resolved);
    }

    @Test
    void countsClicks() {
        UrlMapping mapping = urlService.createShortUrl("https://example.com", null);
        urlService.resolveAndCount(mapping.getShortCode());
        urlService.resolveAndCount(mapping.getShortCode());

        assertEquals(2, urlService.getByCode(mapping.getShortCode()).getClickCount());
    }

    @Test
    void addsHttpsWhenSchemeMissing() {
        UrlMapping mapping = urlService.createShortUrl("example.com/page", null);
        assertTrue(mapping.getLongUrl().startsWith("https://"));
    }

    @Test
    void honoursCustomAliasAndRejectsDuplicates() {
        urlService.createShortUrl("https://example.com", "myalias");
        assertEquals("myalias", urlService.getByCode("myalias").getShortCode());

        assertThrows(BadRequestException.class,
                () -> urlService.createShortUrl("https://other.com", "myalias"));
    }

    @Test
    void rejectsInvalidUrl() {
        assertThrows(BadRequestException.class,
                () -> urlService.createShortUrl("not a url", null));
    }

    @Test
    void unknownCodeThrowsNotFound() {
        assertThrows(ResourceNotFoundException.class,
                () -> urlService.getByCode("nope404"));
    }
}
