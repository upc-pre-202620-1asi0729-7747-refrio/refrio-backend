package com.refrio.platform.operations.interfaces.rest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import com.refrio.platform.operations.application.OperationsQueryService;
import com.refrio.platform.operations.domain.model.aggregates.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name="Operations")
@SecurityRequirement(name="bearerAuth")
@RestController @RequestMapping("/api/v1/operations")
public class OperationsV1Controller {
    private final OperationsQueryService queries;
    public OperationsV1Controller(OperationsQueryService queries){this.queries=queries;}
    @GetMapping("/shipments") public List<ShipmentAggregate> shipments(@AuthenticationPrincipal AuthenticatedUser user){return queries.shipments(user);}
    @GetMapping("/shipments/{id}") public ShipmentAggregate shipment(@AuthenticationPrincipal AuthenticatedUser user,@PathVariable String id){return queries.shipment(user,id);}
    @GetMapping("/clients") public List<ClientAggregate> clients(@AuthenticationPrincipal AuthenticatedUser user){return queries.clients(user);}
    @GetMapping("/suppliers") public List<SupplierAggregate> suppliers(@AuthenticationPrincipal AuthenticatedUser user){return queries.suppliers(user);}
}
