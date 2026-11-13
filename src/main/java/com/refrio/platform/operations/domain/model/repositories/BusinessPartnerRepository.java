package com.refrio.platform.operations.domain.model.repositories;
import com.refrio.platform.operations.domain.model.aggregates.*;
import java.util.*;
public interface BusinessPartnerRepository {
    Optional<ClientAggregate> findClientById(String id);
    List<ClientAggregate> clients();
    List<SupplierAggregate> suppliers();
    ClientAggregate save(ClientAggregate client);
    SupplierAggregate save(SupplierAggregate supplier);
}
