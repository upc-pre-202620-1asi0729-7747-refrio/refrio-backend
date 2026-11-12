package com.refrio.platform.operations.domain.model.repositories;
import com.refrio.platform.operations.domain.model.aggregates.ShipmentAggregate;
import java.util.*;
public interface ShipmentRepository {
    Optional<ShipmentAggregate> findById(String id);
    List<ShipmentAggregate> findAll();
    ShipmentAggregate save(ShipmentAggregate shipment);
}
