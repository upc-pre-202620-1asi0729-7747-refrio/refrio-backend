package com.refrio.platform.iam.application;

import com.refrio.platform.iam.infrastructure.persistence.jpa.entities.PasswordResetTokenEntity;
import com.refrio.platform.iam.infrastructure.persistence.jpa.entities.UserEntity;
import com.refrio.platform.iam.infrastructure.persistence.jpa.repositories.PasswordResetJpaRepository;
import com.refrio.platform.shared.domain.DomainException;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class PasswordResetService {
    private final EntityManager em;
    private final PasswordResetJpaRepository resets;
    private final PasswordEncoder encoder;
    private final ObjectProvider<JavaMailSender> senders;
    private final SecureRandom random = new SecureRandom();
    private final String publicUrl;
    private final String from;

    public PasswordResetService(EntityManager em, PasswordResetJpaRepository resets,
                                PasswordEncoder encoder, ObjectProvider<JavaMailSender> senders,
                                @Value("${app.frontend.public-url:}") String publicUrl,
                                @Value("${app.mail.from:}") String from) {
        this.em=em;this.resets=resets;this.encoder=encoder;this.senders=senders;
        this.publicUrl=publicUrl;this.from=from;
    }

    /** Return a generic acknowledgement whether the email exists or not. */
    @Transactional
    public void request(String email) {
        JavaMailSender sender = senders.getIfAvailable();
        if (sender == null || publicUrl.isBlank() || from.isBlank())
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "Recuperación por correo no configurada");
        if (!publicUrl.startsWith("https://") && !publicUrl.startsWith("http://localhost:"))
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "URL pública de recuperación inválida");
        var users = em.createQuery("select u from UserEntity u where lower(u.email) = :email", UserEntity.class)
                .setParameter("email", email.trim().toLowerCase(java.util.Locale.ROOT))
                .setMaxResults(1).getResultList();
        if (users.isEmpty()) return;
        byte[] bytes = new byte[32];random.nextBytes(bytes);
        String token=Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        var entry=new PasswordResetTokenEntity();
        entry.userId=users.get(0).id;
        entry.tokenHash=sha256(token);
        entry.expiresAt=Instant.now().plusSeconds(900);
        resets.save(entry);
        String url=publicUrl.replaceAll("/+$", "")+"/reset-password?token="+token;
        var message=new SimpleMailMessage();
        message.setTo(users.get(0).email);
        message.setFrom(from);
        message.setSubject("Refrio: restablecer contraseña");
        message.setText("Si solicitaste recuperar el acceso, abre este enlace (caduca en 15 minutos):\n"+url+"\nSi no lo solicitaste, ignora este correo.");
        sender.send(message); // On failure, transaction rolls back; never simulate email delivery.
    }

    @Transactional
    public void confirm(String token, String newPassword) {
        if (token==null || !token.matches("[A-Za-z0-9_-]{43}"))
            throw new DomainException("Enlace de recuperación inválido");
        if (newPassword==null || newPassword.length()<8 || newPassword.length()>72)
            throw new DomainException("La contraseña debe tener de 8 a 72 caracteres");
        var entry=resets.findByTokenHashAndUsedAtIsNull(sha256(token))
                .orElseThrow(()->new DomainException("Enlace inválido o ya utilizado"));
        if (!entry.expiresAt.isAfter(Instant.now()))
            throw new DomainException("Enlace de recuperación caducado");
        var user=em.find(UserEntity.class,entry.userId);
        if(user==null)throw new DomainException("Cuenta inexistente");
        user.passwordHash=encoder.encode(newPassword);
        entry.usedAt=Instant.now();
        // La escritura JPA y el uso único se confirman en la misma transacción.
    }

    private static String sha256(String raw) {
        try {
            byte[] digest=MessageDigest.getInstance("SHA-256")
                .digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception ex) {throw new IllegalStateException(ex);}
    }
}
