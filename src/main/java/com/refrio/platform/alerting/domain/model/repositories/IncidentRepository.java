package com.refrio.platform.alerting.domain.model.repositories;
import com.refrio.platform.alerting.domain.model.aggregates.IncidentAggregate;
import java.util.*;
public interface IncidentRepository {
    Optional<IncidentAggregate> findById(String id);
    List<IncidentAggregate> findAll();
    IncidentAggregate save(IncidentAggregate incident);
}
