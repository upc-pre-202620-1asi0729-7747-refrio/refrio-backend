package com.refrio.platform.iam.interfaces.rest;

import com.refrio.platform.iam.application.PasswordResetService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/password-reset")
public class PasswordResetController {
    private final PasswordResetService service;
    public PasswordResetController(PasswordResetService service){this.service=service;}

    public record Request(@NotBlank @Email String email) {}
    public record Confirm(@NotBlank String token,@NotBlank String newPassword) {}

    @PostMapping("/request") @ResponseStatus(HttpStatus.ACCEPTED)
    public Map<String,String> request(@Valid @RequestBody Request request) {
        service.request(request.email());
        return Map.of("message","Si existe una cuenta, recibirás instrucciones por correo.");
    }
    @PostMapping("/confirm")
    public Map<String,String> confirm(@Valid @RequestBody Confirm request) {
        service.confirm(request.token(),request.newPassword());
        return Map.of("message","Contraseña actualizada. Inicia sesión nuevamente.");
    }
}
