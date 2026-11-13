package com.refrio.platform.inventory.interfaces.rest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import com.refrio.platform.inventory.application.InventoryQueryService;
import com.refrio.platform.inventory.application.InventoryCommandService;
import com.refrio.platform.inventory.application.commands.*;
import org.springframework.http.HttpStatus;
import com.refrio.platform.inventory.domain.model.aggregates.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name="Inventory")
@SecurityRequirement(name="bearerAuth")
@RestController @RequestMapping("/api/v1/inventory")
public class InventoryV1Controller {
    private final InventoryQueryService queries;
    private final InventoryCommandService commands;
    public InventoryV1Controller(InventoryQueryService queries,InventoryCommandService commands){this.queries=queries;this.commands=commands;}
    @GetMapping("/products") public List<InventoryProduct> products(@AuthenticationPrincipal AuthenticatedUser principal){
        return queries.products(principal.role());
    }
    @GetMapping("/products/{id}") public InventoryProduct product(@PathVariable String id,@AuthenticationPrincipal AuthenticatedUser principal){
        return queries.product(principal.role(),id);
    }
    @GetMapping("/batches") public List<StockBatch> batches(@AuthenticationPrincipal AuthenticatedUser principal){
        return queries.batches(principal.role());
    }
    @GetMapping("/batches/fefo") public List<StockBatch> fefo(@AuthenticationPrincipal AuthenticatedUser principal){
        return queries.dispatchableLots(principal.role());
    }
    @PostMapping("/products") @ResponseStatus(HttpStatus.CREATED)
    public InventoryProduct createProduct(@RequestBody CreateProductCommand command,@AuthenticationPrincipal AuthenticatedUser actor){
        return commands.create(command,actor);
    }
    @PostMapping("/batches") @ResponseStatus(HttpStatus.CREATED)
    public StockBatch registerBatch(@RequestBody RegisterBatchCommand command,@AuthenticationPrincipal AuthenticatedUser actor){
        return commands.register(command,actor);
    }
    @PostMapping("/losses") @ResponseStatus(HttpStatus.CREATED)
    public com.refrio.platform.inventory.domain.model.aggregates.InventoryLoss recordLoss(
            @RequestBody RecordLossCommand command,@AuthenticationPrincipal AuthenticatedUser actor){
        return commands.recordLoss(command,actor);
    }
    @PostMapping("/telemetry") @ResponseStatus(HttpStatus.CREATED)
    public com.refrio.platform.inventory.domain.model.aggregates.TemperatureReading recordTemperature(
            @RequestBody RecordTemperatureCommand command,@AuthenticationPrincipal AuthenticatedUser actor){
        return commands.recordTemperature(command,actor);
    }
}
