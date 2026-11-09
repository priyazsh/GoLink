
package me.priyazsh.golink.link;

import jakarta.validation.Valid;
import me.priyazsh.golink.link.dto.CreateShortLinkRequest;
import me.priyazsh.golink.link.dto.RecoverShortLinkRequest;
import me.priyazsh.golink.link.dto.RecoveredShortLinkResponse;
import me.priyazsh.golink.link.dto.ShortLinkResponse;
import me.priyazsh.golink.link.dto.UpdateShortLinkRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/links")
public class ShortLinkController {

    private final ShortLinkService shortLinkService;
    private final String baseUrl;

    public ShortLinkController(
            ShortLinkService shortLinkService,
            @Value("${golink.base-url}") String baseUrl
    ) {
        this.shortLinkService = shortLinkService;
        this.baseUrl = baseUrl;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShortLinkResponse createShortLink(
            @Valid @RequestBody CreateShortLinkRequest request
    ) {
        ShortLinkCreationResult result =
                shortLinkService.createShortLink(request.url());

        ShortLink shortLink = result.shortLink();

        return new ShortLinkResponse(
                shortLink.getCode(),
                baseUrl + "/" + shortLink.getCode(),
                shortLink.getOriginalUrl(),
                result.recoveryToken()
        );
    }

    @PostMapping("/recover")
    public RecoveredShortLinkResponse recoverShortLink(
            @Valid @RequestBody RecoverShortLinkRequest request
    ) {
        ShortLink shortLink = shortLinkService
                .recoverShortLink(request.recoveryToken())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Link not found"
                        )
                );

        return toRecoveredResponse(shortLink);
    }

    @PatchMapping("/{code}")
    public RecoveredShortLinkResponse updateDestination(
            @PathVariable String code,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @Valid @RequestBody UpdateShortLinkRequest request
    ) {
        String recoveryToken = extractRecoveryToken(authorization);

        ShortLink shortLink = shortLinkService.updateDestination(
                code,
                recoveryToken,
                request.url()
        );

        return toRecoveredResponse(shortLink);
    }

    private String extractRecoveryToken(String authorization) {
        if (authorization == null
                || !authorization.startsWith("Bearer ")) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Bearer token required"
            );
        }

        return authorization.substring(7);
    }

    private RecoveredShortLinkResponse toRecoveredResponse(
            ShortLink shortLink
    ) {
        return new RecoveredShortLinkResponse(
                shortLink.getCode(),
                baseUrl + "/" + shortLink.getCode(),
                shortLink.getOriginalUrl(),
                shortLink.isActive(),
                shortLink.getClickCount()
        );
    }
}
