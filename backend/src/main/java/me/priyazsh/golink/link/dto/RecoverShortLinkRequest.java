package me.priyazsh.golink.link.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RecoverShortLinkRequest(

        @NotBlank(message = "Recovery token is required")
        @Pattern(
                regexp = "^[A-Za-z0-9_-]{43}$",
                message = "Invalid recovery token format"
        )
        String recoveryToken

) {
}