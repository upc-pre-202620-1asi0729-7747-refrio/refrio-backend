package com.refrio.platform.inventory.domain.model.valueobjects;
import com.refrio.platform.shared.domain.DomainException;

public record StockQuantity(int units) {
    public StockQuantity {
        if (units < 0) throw new DomainException("El stock no puede ser negativo");
    }
    public StockQuantity add(int units) {
        if (this.units + (long)units < 0 || this.units + (long)units > Integer.MAX_VALUE)
            throw new DomainException("La operación supera las existencias permitidas");
        return new StockQuantity(this.units + units);
    }
}
