package com.refrio.platform.publicportal.domain.model.repositories;
import com.refrio.platform.publicportal.domain.model.aggregates.LeadAggregate;
import java.util.*;
public interface LeadRepository {
    Optional<LeadAggregate> findById(String id);
    List<LeadAggregate> findAll();
    LeadAggregate save(LeadAggregate lead);
}
