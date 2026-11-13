package com.refrio.platform.operations.domain.model.valueobjects;
import com.refrio.platform.shared.domain.DomainException;

public record ShipmentRoute(String origin,String destination) {
    public ShipmentRoute {
        if(origin==null||origin.isBlank()||destination==null||destination.isBlank())
            throw new DomainException("El origen y el destino son obligatorios");
        if(origin.trim().equalsIgnoreCase(destination.trim()))
            throw new DomainException("El destino debe diferir del origen");
        origin=origin.trim();destination=destination.trim();
    }
    public String label(){return origin+" → "+destination;}
}
