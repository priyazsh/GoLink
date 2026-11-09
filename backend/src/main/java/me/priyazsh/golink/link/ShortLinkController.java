package me.priyazsh.golink.link;

import jakarta.validation.Valid;
import me.priyazsh.golink.link.dto.CreateShortLinkRequest;
import me.priyazsh.golink.link.dto.ShortLinkResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
        ShortLink shortLink =
                shortLinkService.createShortLink(request.url());

        String shortUrl = baseUrl + "/" + shortLink.getCode();

        return new ShortLinkResponse(
                shortLink.getCode(),
                shortUrl,
                shortLink.getOriginalUrl()
        );
    }
}