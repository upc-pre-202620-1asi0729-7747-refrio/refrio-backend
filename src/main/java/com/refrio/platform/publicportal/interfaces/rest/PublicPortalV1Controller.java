package com.refrio.platform.publicportal.interfaces.rest;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import com.refrio.platform.publicportal.application.LeadQueryService;
import com.refrio.platform.publicportal.application.LeadApplicationService;
import com.refrio.platform.publicportal.application.commands.SubmitLeadCommand;
import org.springframework.http.HttpStatus;
import com.refrio.platform.publicportal.domain.model.LeadStatus;
import com.refrio.platform.publicportal.domain.model.aggregates.LeadAggregate;
import com.refrio.platform.shared.domain.DomainException;
import com.refrio.platform.shared.domain.ForbiddenOperationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name="Public Portal")
@RestController @RequestMapping("/api/v1/public-portal")
public class PublicPortalV1Controller {
    private final LeadQueryService service;
    private final LeadApplicationService intake;
    public PublicPortalV1Controller(LeadQueryService service,LeadApplicationService intake){this.service=service;this.intake=intake;}
    @GetMapping("/leads") public List<LeadAggregate> leads(@AuthenticationPrincipal AuthenticatedUser user){
        if(!user.supervisor())throw new ForbiddenOperationException("Solo supervisores pueden consultar leads");
        return service.all();
    }
    public record UpdateLeadStatus(String status){}
    @PatchMapping("/leads/{id}") public LeadAggregate status(@PathVariable String id,
           @RequestBody UpdateLeadStatus request,@AuthenticationPrincipal AuthenticatedUser user){
        if(!user.supervisor())throw new ForbiddenOperationException("Solo supervisores pueden gestionar leads");
        return service.updateStatus(id,LeadStatus.valueOf(request.status()));
    }
    @PostMapping("/leads") @ResponseStatus(HttpStatus.CREATED)
    public com.fasterxml.jackson.databind.node.ObjectNode submit(@RequestBody SubmitLeadCommand request){
        return intake.submit(request.companyName(),request.contactName(),request.email(),
            request.phone(),request.interestedPlan(),request.notes());
    }
}
