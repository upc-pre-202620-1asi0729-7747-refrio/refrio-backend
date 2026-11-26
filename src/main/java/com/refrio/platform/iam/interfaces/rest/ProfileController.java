package com.refrio.platform.iam.interfaces.rest;

import com.refrio.platform.iam.application.ProfileUpdateService;
import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import com.refrio.platform.iam.interfaces.rest.resources.UserResource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/iam/users")
public class ProfileController {
    private final ProfileUpdateService service;
    public ProfileController(ProfileUpdateService service){this.service=service;}
    public record ProfileInput(
        @NotBlank @Size(max=120) String firstName,
        @NotBlank @Size(max=120) String lastName,
        @NotBlank @Email @Size(max=254) String email,
        @Size(max=160) String sede) {}

    @PatchMapping("/me")
    public UserResource update(@AuthenticationPrincipal AuthenticatedUser actor,
                               @Valid @RequestBody ProfileInput request) {
        return UserResource.from(service.update(actor.id(),request.firstName(),
            request.lastName(),request.email(),request.sede()));
    }
}
