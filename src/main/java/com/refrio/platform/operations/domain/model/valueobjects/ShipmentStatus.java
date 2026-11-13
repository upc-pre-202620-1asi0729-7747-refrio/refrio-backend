package com.refrio.platform.operations.domain.model.valueobjects;
import com.refrio.platform.shared.domain.DomainException;

/** Status names intentionally follow the existing Angular wire contract. */
public enum ShipmentStatus {
    PREPARING("Preparing"), IN_TRANSIT("In Transit"), DELAYED("Delayed"),
    AWAITING_RECEIPT("Awaiting Receipt"), DELIVERED("Delivered"), REJECTED("Rejected");
    private final String wire;
    ShipmentStatus(String wire){this.wire=wire;}
    public String wire(){return wire;}
    public static ShipmentStatus fromWire(String raw) {
        for(var status:values())if(status.wire.equals(raw))return status;
        throw new DomainException("Estado de despacho no válido: "+raw);
    }
}
