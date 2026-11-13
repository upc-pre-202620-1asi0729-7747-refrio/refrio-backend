package com.refrio.platform.events.domain.model.repositories;
import com.refrio.platform.events.domain.model.aggregates.OperationalEventAggregate;
import java.util.*;
public interface OperationalEventRepository {
    Optional<OperationalEventAggregate> findById(String id);
    List<OperationalEventAggregate> findAll();
    OperationalEventAggregate append(OperationalEventAggregate event);
}
