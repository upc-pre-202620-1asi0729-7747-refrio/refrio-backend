package com.refrio.platform.operations.domain.model.aggregates;
import com.refrio.platform.shared.domain.DomainException;

public record ClientAggregate(String id,String name,String type,String province,String district,String recipientId,Integer orders) {
    public ClientAggregate {
        if(id==null||id.isBlank()||name==null||name.isBlank())
            throw new DomainException("Cliente debe tener código y nombre");
        if(orders!=null&&orders<0)throw new DomainException("Pedidos no puede ser negativo");
    }
}
