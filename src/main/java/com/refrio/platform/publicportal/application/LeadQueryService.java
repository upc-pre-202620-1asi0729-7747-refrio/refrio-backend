package com.refrio.platform.publicportal.application;
import com.refrio.platform.publicportal.domain.model.aggregates.LeadAggregate;
import com.refrio.platform.publicportal.domain.model.repositories.LeadRepository;
import com.refrio.platform.publicportal.domain.model.LeadStatus;
import com.refrio.platform.shared.domain.DomainException;
import com.refrio.platform.shared.domain.repositories.UnitOfWorkLock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class LeadQueryService {
    private final LeadRepository leads;
    private final UnitOfWorkLock mutex;
    public LeadQueryService(LeadRepository leads,UnitOfWorkLock mutex){this.leads=leads;this.mutex=mutex;}
    @Transactional(readOnly=true) public List<LeadAggregate> all(){return leads.findAll();}
    @Transactional public LeadAggregate updateStatus(String id,LeadStatus status){
        mutex.acquire();
        LeadAggregate lead=leads.findById(id).orElseThrow(()->new DomainException("Contacto no existe"));
        return leads.save(lead.moveTo(status));
    }
}
