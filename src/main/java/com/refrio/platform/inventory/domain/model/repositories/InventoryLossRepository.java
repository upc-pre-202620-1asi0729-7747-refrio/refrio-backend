package com.refrio.platform.inventory.domain.model.repositories;
import com.refrio.platform.inventory.domain.model.aggregates.InventoryLoss;
import java.util.List;
public interface InventoryLossRepository {
    InventoryLoss append(InventoryLoss loss);
    List<InventoryLoss> findByAudience(String audience);
}
