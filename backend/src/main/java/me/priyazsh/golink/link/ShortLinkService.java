package me.priyazsh.golink.link;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

@Service
public class ShortLinkService {

    private static final String CHARACTERS =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private static final int CODE_LENGTH = 7;

    private final SecureRandom secureRandom = new SecureRandom();

    private final ShortLinkRepository shortLinkRepository;

    public ShortLinkService(ShortLinkRepository shortLinkRepository) {
        this.shortLinkRepository = shortLinkRepository;
    }

    @Transactional
    public ShortLinkCreationResult createShortLink(String originalUrl) {
        String code = generateUniqueCode();

        String recoveryToken = generateRecoveryToken();

        ShortLink shortLink = new ShortLink();
        shortLink.setId(UUID.randomUUID());
        shortLink.setCode(code);
        shortLink.setOriginalUrl(originalUrl);
        shortLink.setCreatedAt(Instant.now());
        shortLink.setActive(true);
        shortLink.setClickCount(0);
        shortLink.setRecoveryTokenHash(
                hashRecoveryToken(recoveryToken)
        );

        ShortLink savedLink = shortLinkRepository.save(shortLink);

        return new ShortLinkCreationResult(
                savedLink,
                recoveryToken
        );
    }

    @Transactional
    public Optional<String> resolveAndRecordClick(String code) {
        Instant now = Instant.now();

        return shortLinkRepository.findByCode(code)
                .filter(ShortLink::isActive)
                .filter(link ->
                        link.getExpiresAt() == null
                                || link.getExpiresAt().isAfter(now)
                )
                .filter(link ->
                        shortLinkRepository.incrementClickCountIfActive(
                                code,
                                now
                        ) == 1
                )
                .map(ShortLink::getOriginalUrl);
    }

    private String generateUniqueCode() {
        for (int attempt = 0; attempt < 5; attempt++) {
            StringBuilder code = new StringBuilder(CODE_LENGTH);

            for (int i = 0; i < CODE_LENGTH; i++) {
                int index = secureRandom.nextInt(CHARACTERS.length());
                code.append(CHARACTERS.charAt(index));
            }

            String generatedCode = code.toString();

            if (!shortLinkRepository.existsByCode(generatedCode)) {
                return generatedCode;
            }
        }

        throw new IllegalStateException(
                "Unable to generate a unique short code. Please try again."
        );
    }

    private String generateRecoveryToken() {
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(tokenBytes);
    }

    public Optional<ShortLink> recoverShortLink(String recoveryToken) {
        String tokenHash = hashRecoveryToken(recoveryToken);

        return shortLinkRepository.findByRecoveryTokenHash(tokenHash);
    }

    @Transactional(readOnly = true)
    public Optional<ShortLink> findManageableLink(
            String code,
            String recoveryToken
    ) {
        if (recoveryToken == null
                || !recoveryToken.matches("^[A-Za-z0-9_-]{43}$")) {
            return Optional.empty();
        }

        String tokenHash = hashRecoveryToken(recoveryToken);

        return shortLinkRepository.findByCodeAndRecoveryTokenHash(
                code,
                tokenHash
        );
    }

    @Transactional
    public ShortLink updateDestination(
            String code,
            String recoveryToken,
            String newUrl
    ) {
        ShortLink shortLink = findManageableLink(code, recoveryToken)
                .orElseThrow(() ->
                        new org.springframework.web.server.ResponseStatusException(
                                org.springframework.http.HttpStatus.NOT_FOUND,
                                "Link not found"
                        )
                );

        shortLink.setOriginalUrl(newUrl);

        return shortLinkRepository.save(shortLink);
    }

    private String hashRecoveryToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "SHA-256 algorithm is unavailable",
                    exception
            );
        }
    }
}
