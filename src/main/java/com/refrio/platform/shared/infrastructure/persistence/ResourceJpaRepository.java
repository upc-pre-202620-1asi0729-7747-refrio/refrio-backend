package com.refrio.platform.shared.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface ResourceJpaRepository extends JpaRepository<ResourceRecord,Long> {
 List<ResourceRecord> findByResourceOrderByIdAsc(String resource);
 Optional<ResourceRecord> findByResourceAndExternalId(String resource,String externalId);
}
