package me.priyazsh.golink.link;

public record ShortLinkCreationResult(
        ShortLink shortLink,
        String recoveryToken
) {
}