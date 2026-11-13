package com.refrio.platform.inventory.application;
import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import com.refrio.platform.inventory.application.commands.*;
import com.refrio.platform.inventory.domain.model.aggregates.*;
import com.refrio.platform.inventory.domain.model.repositories.*;
import com.refrio.platform.events.application.OperationalEventPublisher;
import com.refrio.platform.shared.domain.DomainException;
import com.refrio.platform.shared.domain.ForbiddenOperationException;
import com.refrio.platform.shared.domain.repositories.UnitOfWorkLock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

/** Inventory application service: stock mutations and telemetry owned by the Inventory BC. */
@Service
public class InventoryCommandService {
    private final InventoryProductRepository products;
    private final StockBatchRepository batches;
    private final InventoryLossRepository losses;
    private final TemperatureReadingRepository telemetry;
    private final UnitOfWorkLock lock;
    private final OperationalEventPublisher publisher;
    public InventoryCommandService(InventoryProductRepository products,StockBatchRepository batches,
        InventoryLossRepository losses,TemperatureReadingRepository telemetry,UnitOfWorkLock lock,
        OperationalEventPublisher publisher) {
        this.products=products;this.batches=batches;this.losses=losses;this.telemetry=telemetry;
        this.lock=lock;this.publisher=publisher;
    }
    @Transactional public InventoryProduct create(CreateProductCommand cmd,AuthenticatedUser actor){
        lock.acquire();
        String id="PRD-"+UUID.randomUUID().toString().substring(0,8).toUpperCase();
        String name=cmd.name()==null?"":cmd.name().trim();
        if(name.isBlank())throw new DomainException("El nombre del producto es obligatorio");
        var product=new InventoryProduct(id,name,cmd.category()==null?"":cmd.category(),"0",
            "4°C",cmd.expirationDate()==null?"":cmd.expirationDate(),
            cmd.warehouse()==null?"Almacén principal":cmd.warehouse(),"Active",null)
            .withTemperature(cmd.initialTemperature());
        products.save(product,actor.role());
        publisher.publish("Producto creado",id,"Inventory & FEFO",actor,"SUCCESS","Alta de producto",actor.role());
        return product;
    }
    @Transactional public StockBatch register(RegisterBatchCommand cmd,AuthenticatedUser actor){
        lock.acquire();
        InventoryProduct product=products.findById(cmd.productId(),actor.role())
            .orElseThrow(()->new DomainException("Producto inexistente"));
        if(cmd.quantity()<=0)throw new DomainException("La cantidad debe ser positiva");
        String id="BT-"+UUID.randomUUID().toString().substring(0,8).toUpperCase();
        String received=cmd.receptionDate()==null||cmd.receptionDate().isBlank()
            ?LocalDate.now(ZoneId.of("America/Lima")).toString():cmd.receptionDate();
        var batch=new StockBatch(id,cmd.productId(),cmd.quantity(),cmd.weight(),received,
            cmd.expirationDate(),cmd.supplier(),cmd.location(),"Disponible",actor.role(),cmd.notes());
        batches.save(batch);
        products.save(product.withStockAdjustment(cmd.quantity()),actor.role());
        publisher.publish("Lote registrado",id,"Inventory & FEFO",actor,"SUCCESS",
            "Ingreso de "+cmd.quantity()+" unidades",actor.role());
        return batch;
    }
    @Transactional public InventoryLoss recordLoss(RecordLossCommand cmd,AuthenticatedUser actor){
        lock.acquire();
        StockBatch batch=batches.findById(cmd.batchId()).orElseThrow(()->new DomainException("Lote inexistente"));
        if(!actor.role().equals(batch.audience()))throw new ForbiddenOperationException("El lote no pertenece a tu almacén");
        InventoryProduct product=products.findById(batch.productId(),actor.role())
            .orElseThrow(()->new DomainException("Producto inexistente"));
        StockBatch updated=batch.debit(cmd.quantity());
        InventoryProduct adjusted=product.withStockAdjustment(-cmd.quantity());
        var loss=InventoryLoss.record(batch.id(),batch.productId(),cmd.quantity(),cmd.cause(),actor.role());
        batches.save(updated);products.save(adjusted,actor.role());losses.append(loss);
        publisher.publish("Merma registrada",batch.id(),"Inventory & FEFO",actor,"SUCCESS",
            cmd.quantity()+" unidades retiradas: "+cmd.cause(),actor.role());
        return loss;
    }
    @Transactional public TemperatureReading recordTemperature(RecordTemperatureCommand cmd,AuthenticatedUser actor){
        lock.acquire();
        InventoryProduct product=products.findById(cmd.productId(),actor.role())
            .orElseThrow(()->new DomainException("Producto inexistente"));
        InventoryProduct updated=product.withTemperature(cmd.value());
        TemperatureReading reading=TemperatureReading.capture(cmd.productId(),actor.role(),cmd.value());
        products.save(updated,actor.role());telemetry.append(reading);
        publisher.publish("Lectura manual registrada",cmd.productId(),"Storage & Telemetry",actor,"SUCCESS",
            cmd.value()+" °C",actor.role());
        return reading;
    }
}
