package com.refrio.platform.operations.application;
import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import com.refrio.platform.operations.domain.model.aggregates.*;
import com.refrio.platform.operations.domain.model.repositories.*;
import com.refrio.platform.shared.domain.DomainException;
import com.refrio.platform.shared.domain.ForbiddenOperationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class OperationsQueryService {
    private final ShipmentRepository shipments;
    private final BusinessPartnerRepository partners;
    public OperationsQueryService(ShipmentRepository shipments,BusinessPartnerRepository partners){
        this.shipments=shipments;this.partners=partners;
    }
    @Transactional(readOnly=true) public List<ShipmentAggregate> shipments(AuthenticatedUser user){
        return shipments.findAll().stream().filter(s->user.supervisor()||String.valueOf(user.id()).equals(s.recipientId())).toList();
    }
    @Transactional(readOnly=true) public ShipmentAggregate shipment(AuthenticatedUser user,String id){
        ShipmentAggregate s=shipments.findById(id).orElseThrow(()->new DomainException("Despacho inexistente"));
        if(!s.canReceive(user.id(),user.supervisor()))throw new ForbiddenOperationException("No autorizado a consultar este despacho");
        return s;
    }
    @Transactional(readOnly=true) public List<ClientAggregate> clients(AuthenticatedUser user){
        if(!user.supervisor())throw new ForbiddenOperationException("Solo supervisores pueden consultar clientes");
        return partners.clients();
    }
    @Transactional(readOnly=true) public List<SupplierAggregate> suppliers(AuthenticatedUser user){
        if(!user.supervisor())throw new ForbiddenOperationException("Solo supervisores pueden consultar proveedores");
        return partners.suppliers();
    }
}
