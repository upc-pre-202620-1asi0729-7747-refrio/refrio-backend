package com.refrio.platform.events.domain.model.aggregates;
import com.refrio.platform.events.domain.model.valueobjects.EventOutcome;
import com.refrio.platform.shared.domain.DomainException;
import java.time.Instant;
import java.util.UUID;

/** Immutable audit record. Business operations append events in their own DB transaction. */
public record OperationalEventAggregate(String id,String name,String aggregateId,String description,
              String context,String occurredAt,String actor,String source,String outcome,String audience,String ruleId) {
    public OperationalEventAggregate {
        if(id==null||id.isBlank()||name==null||name.isBlank()||aggregateId==null||aggregateId.isBlank())
            throw new DomainException("El evento requiere ID, nombre y entidad relacionada");
        if(outcome!=null)EventOutcome.valueOf(outcome);
    }
    public boolean visibleTo(String role,boolean supervisor){
        return supervisor || "ALL".equals(audience) || role.equals(audience);
    }
    public static OperationalEventAggregate userAction(String name,String aggregateId,String description,
                           String context,String actor,String outcome,String audience){
        return new OperationalEventAggregate("EVT-"+UUID.randomUUID(),name,aggregateId,
            description,context,Instant.now().toString(),actor,"USER",outcome,audience,null);
    }
}
