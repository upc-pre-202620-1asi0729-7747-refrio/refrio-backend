package com.refrio.platform.inventory.domain.model.aggregates;

import com.refrio.platform.inventory.domain.model.valueobjects.ExpirationDate;
import com.refrio.platform.inventory.domain.model.valueobjects.StockQuantity;
import com.refrio.platform.shared.domain.DomainException;
import java.time.LocalDate;
import java.util.Locale;

public record StockBatch(String id, String productId, int quantity, String weight,
                         String receptionDate, String expirationDate, String supplier,
                         String location, String status, String audience, String notes) {
    public StockBatch {
        if (id == null || id.isBlank() || productId == null || productId.isBlank())
            throw new DomainException("Lote y producto requeridos");
        new StockQuantity(quantity);
        ExpirationDate.parse(expirationDate);
        if (!"Minorista".equals(audience) && !"Supervisor".equals(audience))
            throw new DomainException("Audiencia de lote no válida");
    }
    public boolean canDispatch(LocalDate today) {
        return quantity > 0 && !new ExpirationDate(LocalDate.parse(expirationDate)).expiredOn(today)
            && !String.valueOf(status).toLowerCase(Locale.ROOT).matches(".*(inmovilizado|bloqueado|descartado|vencido|hold).*");
    }
    public StockBatch debit(int units) {
        if (units <= 0) throw new DomainException("La cantidad debe ser positiva");
        return new StockBatch(id,productId,new StockQuantity(quantity).add(-units).units(),weight,
            receptionDate,expirationDate,supplier,location,status,audience,notes);
    }
}
