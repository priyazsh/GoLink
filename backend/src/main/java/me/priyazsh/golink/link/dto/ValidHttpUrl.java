package me.priyazsh.golink.link.dto;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = HttpUrlValidator.class)
@Target({
        ElementType.FIELD,
        ElementType.PARAMETER,
        ElementType.RECORD_COMPONENT
})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidHttpUrl {

    String message() default "A valid HTTP or HTTPS URL is required";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}