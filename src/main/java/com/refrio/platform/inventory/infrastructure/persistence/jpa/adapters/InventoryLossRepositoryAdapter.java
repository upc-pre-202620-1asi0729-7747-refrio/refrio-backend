package com.refrio.platform.inventory.infrastructure.persistence.jpa.adapters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.refrio.platform.inventory.domain.model.aggregates.InventoryLoss;
import com.refrio.platform.inventory.domain.model.repositories.InventoryLossRepository;
import com.refrio.platform.shared.domain.DomainException;
import com.refrio.platform.shared.application.compatibility.DocumentRepository;
import com.refrio.platform.shared.infrastructure.persistence.jpa.adapters.JsonAggregateRepositorySupport;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public class InventoryLossRepositoryAdapter extends JsonAggregateRepositorySupport<InventoryLoss> implements InventoryLossRepository {
    public InventoryLossRepositoryAdapter(DocumentRepository store,ObjectMapper mapper){super("losses",InventoryLoss.class,store,mapper);}
    @Override public InventoryLoss append(InventoryLoss loss){
        if(findById(loss.id()).isPresent())throw new DomainException("La merma ya fue registrada");
        return save(loss.id(),loss);
    }
    @Override public List<InventoryLoss> findByAudience(String audience){
        return findAll().stream().filter(loss->audience.equals(loss.audience())).toList();
    }
}
