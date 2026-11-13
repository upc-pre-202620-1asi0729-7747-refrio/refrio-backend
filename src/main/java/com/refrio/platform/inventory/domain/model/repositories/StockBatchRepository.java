package com.refrio.platform.inventory.domain.model.repositories;
import com.refrio.platform.inventory.domain.model.aggregates.StockBatch;
import java.util.*;
public interface StockBatchRepository {
    Optional<StockBatch> findById(String id);
    List<StockBatch> findByAudience(String audience);
    StockBatch save(StockBatch batch);
}
