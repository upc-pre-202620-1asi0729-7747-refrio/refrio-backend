package com.refrio.platform.publicportal.domain.model.valueobjects;
import com.refrio.platform.shared.domain.DomainException;
import java.util.Locale;
public record ContactEmail(String value) {
    public ContactEmail {
        if(value==null||!value.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")||value.length()>254)
            throw new DomainException("Correo de contacto inválido");
        value=value.trim().toLowerCase(Locale.ROOT);
    }
}
