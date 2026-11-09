package me.priyazsh.golink.link;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface ShortLinkRepository extends JpaRepository<ShortLink, UUID> {

    Optional<ShortLink> findByCode(String code);
    Optional<ShortLink> findByRecoveryTokenHash(String recoveryTokenHash);
    boolean existsByCode(String code);

    @Modifying
    @Query("""
    UPDATE ShortLink s
    SET s.clickCount = s.clickCount + 1
    WHERE s.code = :code
      AND s.active = true
      AND (s.expiresAt IS NULL OR s.expiresAt > :now)
    """)
    int incrementClickCountIfActive(
            @Param("code") String code,
            @Param("now") Instant now
    );
}