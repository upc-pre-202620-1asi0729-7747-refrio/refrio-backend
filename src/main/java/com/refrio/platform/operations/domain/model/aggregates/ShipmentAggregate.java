package com.refrio.platform.operations.domain.model.aggregates;

import com.refrio.platform.inventory.domain.model.Allocation;
import com.refrio.platform.operations.domain.model.valueobjects.ShipmentStatus;
import com.refrio.platform.operations.domain.services.ShipmentPolicy;
import com.refrio.platform.shared.domain.DomainException;
import java.time.Instant;
import java.util.*;

/** Shipment is the aggregate root governing shipping transitions and receiving constraints. */
public record ShipmentAggregate(String id,String status,String recipientId,String clientId,
        List<Allocation> allocations,List<StatusEntry> timeline,Receipt receipt) {
    public record StatusEntry(String status,String at) {}
    public record Receipt(String decision,String notes,String at,String actor,List<Allocation> quantities) {}
    public ShipmentAggregate {
        if(id==null||id.isBlank())throw new DomainException("Código de despacho requerido");
        ShipmentStatus.fromWire(status);
        allocations=allocations==null?List.of():List.copyOf(allocations);
        timeline=timeline==null?List.of():List.copyOf(timeline);
    }
    public ShipmentAggregate advance() {
        String next=ShipmentPolicy.advance(status);
        ArrayList<StatusEntry> history=new ArrayList<>(timeline);
        history.add(new StatusEntry(next,Instant.now().toString()));
        return new ShipmentAggregate(id,next,recipientId,clientId,allocations,history,receipt);
    }
    public ShipmentAggregate receive(String decision,List<Allocation> quantities,String notes,String actor) {
        ShipmentPolicy.validateReceipt(status,receipt!=null,allocations,quantities,decision,notes);
        String next="rejected".equals(decision)?"Rejected":"Delivered";
        String at=Instant.now().toString();
        ArrayList<StatusEntry> history=new ArrayList<>(timeline);
        history.add(new StatusEntry(next,at));
        return new ShipmentAggregate(id,next,recipientId,clientId,allocations,history,
            new Receipt(decision,notes,at,actor,List.copyOf(quantities)));
    }
    public boolean canReceive(Long userId,boolean supervisor) {
        return supervisor||String.valueOf(userId).equals(recipientId);
    }
}
