package me.priyazsh.golink.link.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateShortLinkRequest(

        @NotBlank(message = "URL is required")
        @Size(max = 2048, message = "URL must not exceed 2048 characters")
        @Pattern(
                regexp = "^https?://.+$",
                message = "Only HTTP and HTTPS URLs are allowed"
        )
        String url

) {
}