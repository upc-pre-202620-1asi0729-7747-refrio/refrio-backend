package com.refrio.platform.inventory.domain.model.repositories;
import com.refrio.platform.inventory.domain.model.aggregates.InventoryProduct;
import java.util.*;
public interface InventoryProductRepository {
    Optional<InventoryProduct> findById(String id, String audience);
    List<InventoryProduct> findByAudience(String audience);
    InventoryProduct save(InventoryProduct product, String audience);
}
