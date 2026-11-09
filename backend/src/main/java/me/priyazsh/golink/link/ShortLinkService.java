package me.priyazsh.golink.link;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
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
    public ShortLink createShortLink(String originalUrl) {

        String code = generateUniqueCode();

        ShortLink shortLink = new ShortLink();
        shortLink.setId(UUID.randomUUID());
        shortLink.setCode(code);
        shortLink.setOriginalUrl(originalUrl);
        shortLink.setCreatedAt(Instant.now());
        shortLink.setActive(true);
        shortLink.setClickCount(0);

        return shortLinkRepository.save(shortLink);
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
}