package com.refrio.platform.inventory.infrastructure.persistence.jpa.adapters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.refrio.platform.inventory.domain.model.aggregates.StockBatch;
import com.refrio.platform.inventory.domain.model.repositories.StockBatchRepository;
import com.refrio.platform.shared.application.compatibility.DocumentRepository;
import com.refrio.platform.shared.infrastructure.persistence.jpa.adapters.JsonAggregateRepositorySupport;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class StockBatchRepositoryAdapter extends JsonAggregateRepositorySupport<StockBatch> implements StockBatchRepository {
    public StockBatchRepositoryAdapter(DocumentRepository documents,ObjectMapper mapper){super("batches",StockBatch.class,documents,mapper);}
    @Override public List<StockBatch> findByAudience(String audience) {
        return findAll().stream().filter(b -> audience.equals(b.audience())).toList();
    }
    @Override public StockBatch save(StockBatch batch){return super.save(batch.id(),batch);}
}
