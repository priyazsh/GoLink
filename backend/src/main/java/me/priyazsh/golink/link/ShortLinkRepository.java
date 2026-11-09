package me.priyazsh.golink.link;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ShortLinkRepository extends JpaRepository<ShortLink, UUID> {

    Optional<ShortLink> findByCode(String code);

    boolean existsByCode(String code);
}