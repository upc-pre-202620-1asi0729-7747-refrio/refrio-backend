package com.refrio.platform.inventory.domain.model.valueobjects;
import com.refrio.platform.shared.domain.DomainException;
import java.time.LocalDate;

public record ExpirationDate(LocalDate value) {
    public ExpirationDate { if (value == null) throw new DomainException("Fecha de vencimiento requerida"); }
    public static ExpirationDate parse(String iso) {
        try { return new ExpirationDate(LocalDate.parse(iso)); }
        catch (Exception e) { throw new DomainException("Fecha de vencimiento no válida: " + iso); }
    }
    public boolean expiredOn(LocalDate today) { return !value.isAfter(today); }
}
