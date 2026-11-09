package me.priyazsh.golink.link;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class LinkRedirectController {

    private final ShortLinkService shortLinkService;

    public LinkRedirectController(ShortLinkService shortLinkService) {
        this.shortLinkService = shortLinkService;
    }

    @GetMapping("/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {

        return shortLinkService.resolveAndRecordClick(code)
                .map(url -> ResponseEntity
                        .status(HttpStatus.FOUND)
                        .location(URI.create(url))
                        .<Void>build())
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}