package com.refrio.platform.inventory.domain.model.aggregates;
import com.refrio.platform.shared.domain.DomainException;
import java.time.Instant;
import java.util.UUID;
/** Immutable loss ledger entry. A loss never belongs to a different inventory audience. */
public record InventoryLoss(String id,String batchId,String productId,int quantity,String cause,String at,String audience) {
    public InventoryLoss {
        if(id==null||id.isBlank()||batchId==null||batchId.isBlank()||productId==null||productId.isBlank())
            throw new DomainException("La merma requiere identificador, lote y producto");
        if(quantity<=0)throw new DomainException("La merma debe ser positiva");
        if(cause==null||cause.isBlank())throw new DomainException("La causa de merma es obligatoria");
        if(!"Supervisor".equals(audience)&&!"Minorista".equals(audience))throw new DomainException("Audiencia inválida");
    }
    public static InventoryLoss record(String batchId,String productId,int quantity,String cause,String audience) {
        return new InventoryLoss(UUID.randomUUID().toString(),batchId,productId,quantity,cause,Instant.now().toString(),audience);
    }
}
