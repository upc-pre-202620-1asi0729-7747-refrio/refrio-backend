package com.refrio.platform.operations.infrastructure.persistence.jpa.adapters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.refrio.platform.operations.domain.model.aggregates.*;
import com.refrio.platform.operations.domain.model.repositories.BusinessPartnerRepository;
import com.refrio.platform.shared.application.compatibility.DocumentRepository;
import com.refrio.platform.shared.infrastructure.persistence.jpa.adapters.JsonAggregateRepositorySupport;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class BusinessPartnerRepositoryAdapter implements BusinessPartnerRepository {
    private final ClientDocuments clientDocs;
    private final SupplierDocuments supplierDocs;
    public BusinessPartnerRepositoryAdapter(DocumentRepository d,ObjectMapper mapper){
        clientDocs=new ClientDocuments(d,mapper);supplierDocs=new SupplierDocuments(d,mapper);
    }
    @Override public Optional<ClientAggregate> findClientById(String id){return clientDocs.findById(id);}
    @Override public List<ClientAggregate> clients(){return clientDocs.findAll();}
    @Override public List<SupplierAggregate> suppliers(){return supplierDocs.findAll();}
    @Override public ClientAggregate save(ClientAggregate c){return clientDocs.store(c);}
    @Override public SupplierAggregate save(SupplierAggregate s){return supplierDocs.store(s);}
    private static final class ClientDocuments extends JsonAggregateRepositorySupport<ClientAggregate>{
        ClientDocuments(DocumentRepository d,ObjectMapper m){super("clients",ClientAggregate.class,d,m);}
        ClientAggregate store(ClientAggregate c){return super.save(c.id(),c);}
    }
    private static final class SupplierDocuments extends JsonAggregateRepositorySupport<SupplierAggregate>{
        SupplierDocuments(DocumentRepository d,ObjectMapper m){super("suppliers",SupplierAggregate.class,d,m);}
        SupplierAggregate store(SupplierAggregate s){return super.save(s.id(),s);}
    }
}
