package com.refrio.platform.iam.domain.model.valueobjects;
import com.refrio.platform.shared.domain.DomainException;
public record Username(String value) {
    public Username {
        if(value==null||!value.trim().matches("[A-Za-z0-9._-]{3,40}"))
            throw new DomainException("El usuario debe tener 3 a 40 caracteres sin espacios");
        value=value.trim();
    }
}
