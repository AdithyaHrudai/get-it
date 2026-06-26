package com.getit.repository;

import com.getit.model.UrlMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA gives us all the CRUD methods for free; we only declare the
 * couple of lookups specific to short links. Spring derives the queries from
 * the method names.
 */
public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {

    Optional<UrlMapping> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);

    Page<UrlMapping> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
