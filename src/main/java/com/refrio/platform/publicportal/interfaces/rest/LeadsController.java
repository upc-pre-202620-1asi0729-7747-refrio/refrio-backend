package com.refrio.platform.publicportal.interfaces.rest;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.refrio.platform.publicportal.application.LeadApplicationService;
import com.refrio.platform.shared.domain.DomainException;
import com.refrio.platform.shared.domain.ForbiddenOperationException;
import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import com.fasterxml.jackson.databind.node.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@Tag(name="Public Portal")
@RestController @RequestMapping("/api/leads")
public class LeadsController {
 private final LeadApplicationService service;
 public LeadsController(LeadApplicationService service){this.service=service;}
 public record LeadRequest(@NotBlank String companyName,@NotBlank String contactName,@NotBlank @Email String email,String phone,String interestedPlan,String notes){}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public ObjectNode submit(@Valid @RequestBody LeadRequest input){return service.submit(input.companyName(),input.contactName(),input.email(),input.phone()==null?"":input.phone(),input.interestedPlan()==null?"":input.interestedPlan(),input.notes()==null?"":input.notes());}
 @GetMapping public ArrayNode list(@AuthenticationPrincipal AuthenticatedUser user){if(!user.supervisor())throw new ForbiddenOperationException("Solo supervisores pueden consultar contactos comerciales");return service.all();}
}
