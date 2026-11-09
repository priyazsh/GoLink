package me.priyazsh.golink.link.dto;

public record RecoveredShortLinkResponse(
        String code,
        String shortUrl,
        String originalUrl,
        boolean active,
        long clickCount
) {
}