package com.refrio.platform.operations.interfaces.rest;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import com.refrio.platform.operations.application.OperationsApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** Semantic REST adapter; the legacy /api/operations/{command} contract remains unchanged. */
@RestController
@RequestMapping("/api/v1/operations/shipments")
@Tag(name="Operations", description="FEFO-based shipments, progression and proof of receipt")
@SecurityRequirement(name="bearerAuth")
public class ShipmentCommandController {
    private final OperationsApplicationService operations;
    public ShipmentCommandController(OperationsApplicationService operations) { this.operations=operations; }

    public record ShipmentLine(@NotBlank String productId, @Min(1) int quantity) {}
    public record CreateShipmentRequest(@NotBlank String clientId, String expectedRecipientId,
                                        @NotBlank String origin, @NotBlank String destination,
                                        @NotBlank String driver, @NotBlank String vehicle,
                                        @NotBlank String eta,
                                        @NotEmpty List<@Valid ShipmentLine> lines,
                                        @NotEmpty List<@NotBlank String> scanned) {}
    public record ReceivedAllocation(@NotBlank String batchId, @NotBlank String productId,
                                     @Min(0) int quantity) {}
    public record ReceiveShipmentRequest(@NotBlank String decision,
                                         @NotEmpty List<@Valid ReceivedAllocation> quantities,
                                         String notes) {}

    @Operation(summary="Prepare a shipment, reserving FEFO batches atomically")
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Map<String,String> create(@Valid @RequestBody CreateShipmentRequest request,
                                     @AuthenticationPrincipal AuthenticatedUser actor) {
        ObjectNode draft=operationsInput();
        draft.put("clientId", request.clientId());
        draft.put("expectedRecipientId", request.expectedRecipientId());
        draft.put("origin", request.origin());
        draft.put("destination", request.destination());
        draft.put("driver", request.driver());
        draft.put("vehicle", request.vehicle());
        draft.put("eta", request.eta());
        ArrayNode lines=draft.putArray("lines");
        request.lines().forEach(line->lines.add(operationsInput().put("productId",line.productId()).put("quantity",line.quantity())));
        ArrayNode scanned=draft.putArray("scanned");
        request.scanned().forEach(scanned::add);
        String id=operations.execute("createShipment", args(draft),actor).path("result").asText();
        return Map.of("id",id,"status","Preparing");
    }
    @Operation(summary="Advance an active shipment to its next business state")
    @PostMapping("/{id}/advance")
    public Map<String,String> advance(@PathVariable String id,@AuthenticationPrincipal AuthenticatedUser actor) {
        ArrayNode a=com.fasterxml.jackson.databind.node.JsonNodeFactory.instance.arrayNode().add(id);
        operations.execute("advanceShipment",a,actor);
        return Map.of("id",id,"result","advanced");
    }
    @Operation(summary="Accept, partially accept or reject a shipment with quantities by batch")
    @PostMapping("/{id}/receipt")
    public Map<String,String> receipt(@PathVariable String id,
                           @Valid @RequestBody ReceiveShipmentRequest request,
                           @AuthenticationPrincipal AuthenticatedUser actor) {
        ArrayNode quantities=com.fasterxml.jackson.databind.node.JsonNodeFactory.instance.arrayNode();
        request.quantities().forEach(q->quantities.add(operationsInput()
            .put("batchId",q.batchId()).put("productId",q.productId()).put("quantity",q.quantity())));
        ArrayNode a=com.fasterxml.jackson.databind.node.JsonNodeFactory.instance.arrayNode();
        a.add(id); a.add(request.decision()); a.add(quantities); a.add(request.notes()==null?"":request.notes());
        operations.execute("receiveShipment",a,actor);
        return Map.of("id",id,"result","receipt-recorded");
    }
    private ObjectNode operationsInput(){return com.fasterxml.jackson.databind.node.JsonNodeFactory.instance.objectNode();}
    private ArrayNode args(ObjectNode input){return com.fasterxml.jackson.databind.node.JsonNodeFactory.instance.arrayNode().add(input);}
}
