package com.refrio.platform.shared.domain.model.valueobjects;
import com.refrio.platform.shared.domain.DomainException;

public record EntityId(String value) {
    public EntityId {
        if (value == null || value.isBlank() || value.length() > 150)
            throw new DomainException("El identificador debe tener entre 1 y 150 caracteres");
        value = value.trim();
    }
    @Override public String toString() { return value; }
}
