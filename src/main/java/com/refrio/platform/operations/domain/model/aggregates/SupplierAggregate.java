package com.refrio.platform.operations.domain.model.aggregates;
import com.refrio.platform.shared.domain.DomainException;
public record SupplierAggregate(String id,String name,String category,Integer deliveries,Integer score,String rating) {
    public SupplierAggregate {
        if(id==null||id.isBlank()||name==null||name.isBlank())
            throw new DomainException("Proveedor debe tener código y nombre");
        if(deliveries!=null&&deliveries<0)throw new DomainException("Entregas no puede ser negativo");
    }
}
