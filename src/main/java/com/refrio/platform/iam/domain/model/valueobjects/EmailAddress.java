package com.refrio.platform.iam.domain.model.valueobjects;
import com.refrio.platform.shared.domain.DomainException;
import java.util.Locale;
public record EmailAddress(String value) {
    public EmailAddress {
        if(value==null||!value.trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"))
            throw new DomainException("Dirección de correo inválida");
        value=value.trim().toLowerCase(Locale.ROOT);
    }
}
