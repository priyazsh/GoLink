package me.priyazsh.golink.link.dto;

public record ShortLinkResponse(
        String code,
        String shortUrl,
        String originalUrl,
        String recoveryToken
) {
}