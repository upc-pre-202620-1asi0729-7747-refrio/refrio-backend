package com.refrio.platform.inventory.infrastructure.persistence.jpa.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.refrio.platform.inventory.domain.model.aggregates.InventoryProduct;
import com.refrio.platform.inventory.domain.model.repositories.InventoryProductRepository;
import com.refrio.platform.shared.domain.DomainException;
import com.refrio.platform.shared.application.compatibility.DocumentRepository;
import com.refrio.platform.shared.infrastructure.persistence.jpa.adapters.JsonAggregateRepositorySupport;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class InventoryProductRepositoryAdapter implements InventoryProductRepository {
    private final Products supervisor;
    private final Products minorista;
    public InventoryProductRepositoryAdapter(DocumentRepository documents, ObjectMapper mapper) {
        supervisor = new Products("inventory", documents, mapper);
        minorista = new Products("bodeguero-inventory", documents, mapper);
    }
    private Products select(String audience) {
        return switch (audience) {
            case "Supervisor" -> supervisor;
            case "Minorista" -> minorista;
            default -> throw new DomainException("Audiencia inválida");
        };
    }
    @Override public Optional<InventoryProduct> findById(String id, String audience) {
        return select(audience).findById(id);
    }
    @Override public List<InventoryProduct> findByAudience(String audience) {
        return select(audience).findAll();
    }
    @Override public InventoryProduct save(InventoryProduct product,String audience) {
        return select(audience).store(product);
    }
    private static final class Products extends JsonAggregateRepositorySupport<InventoryProduct> {
        Products(String resource,DocumentRepository d,ObjectMapper m){super(resource,InventoryProduct.class,d,m);}
        InventoryProduct store(InventoryProduct item){return save(item.id(),item);}
    }
}
