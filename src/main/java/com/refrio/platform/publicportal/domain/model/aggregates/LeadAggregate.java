package com.refrio.platform.publicportal.domain.model.aggregates;
import com.refrio.platform.publicportal.domain.model.LeadStatus;
import com.refrio.platform.publicportal.domain.model.valueobjects.ContactEmail;
import com.refrio.platform.shared.domain.DomainException;
import java.time.Instant;
import java.util.UUID;

public record LeadAggregate(String id,String companyName,String contactName,String email,
                            String phone,String interestedPlan,String notes,String status,String submittedAt) {
    public LeadAggregate {
        if(id==null||id.isBlank()||companyName==null||companyName.isBlank()||contactName==null||contactName.isBlank())
            throw new DomainException("ID, empresa y contacto son obligatorios");
        new ContactEmail(email);
        LeadStatus.valueOf(status);
    }
    public static LeadAggregate submit(String company,String contact,String email,String phone,String plan,String notes){
        return new LeadAggregate("LEAD-"+UUID.randomUUID(),company,contact,new ContactEmail(email).value(),
            phone==null?"":phone,plan==null?"":plan,notes==null?"":notes,"NEW",Instant.now().toString());
    }
    public LeadAggregate moveTo(LeadStatus next){
        LeadStatus current=LeadStatus.valueOf(status);
        if(current==LeadStatus.CLOSED||next.ordinal()<current.ordinal())throw new DomainException("Transición comercial no permitida");
        return new LeadAggregate(id,companyName,contactName,email,phone,interestedPlan,notes,next.name(),submittedAt);
    }
}
