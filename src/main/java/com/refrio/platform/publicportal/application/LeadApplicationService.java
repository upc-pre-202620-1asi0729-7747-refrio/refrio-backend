package com.refrio.platform.publicportal.application;

import com.refrio.platform.publicportal.domain.model.aggregates.LeadAggregate;
import com.refrio.platform.publicportal.domain.model.repositories.LeadRepository;
import com.refrio.platform.shared.domain.repositories.UnitOfWorkLock;
import com.refrio.platform.shared.application.compatibility.DocumentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LeadApplicationService {
    private final LeadRepository leads;
    private final DocumentRepository documents;
    private final UnitOfWorkLock mutex;
    private final ObjectMapper mapper;
    public LeadApplicationService(LeadRepository leads,DocumentRepository documents,
                                  UnitOfWorkLock mutex,ObjectMapper mapper){
        this.leads=leads;this.documents=documents;this.mutex=mutex;this.mapper=mapper;
    }
    @Transactional public ObjectNode submit(String company,String contact,String email,String phone,String plan,String notes){
        var lead=LeadAggregate.submit(company,contact,email,phone,plan,notes);
        mutex.acquire();
        leads.save(lead);
        return mapper.valueToTree(lead);
    }
    @Transactional(readOnly=true) public ArrayNode all(){return documents.all("leads");}
}
