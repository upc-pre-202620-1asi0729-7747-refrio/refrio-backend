package com.refrio.platform.alerting.domain.model.repositories;
import com.refrio.platform.alerting.domain.model.aggregates.AlertAggregate;
import java.util.*;
public interface AlertRepository {
    Optional<AlertAggregate> findById(String id);
    List<AlertAggregate> findAll();
}
