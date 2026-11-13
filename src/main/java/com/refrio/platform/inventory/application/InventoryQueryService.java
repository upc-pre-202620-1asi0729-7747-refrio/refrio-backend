package com.refrio.platform.inventory.application;
import com.refrio.platform.inventory.domain.model.aggregates.*;
import com.refrio.platform.inventory.domain.model.repositories.*;
import com.refrio.platform.shared.domain.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class InventoryQueryService {
    private final InventoryProductRepository products;
    private final StockBatchRepository batches;
    public InventoryQueryService(InventoryProductRepository products,StockBatchRepository batches){
        this.products=products;this.batches=batches;
    }
    @Transactional(readOnly=true) public List<InventoryProduct> products(String audience){return products.findByAudience(audience);}
    @Transactional(readOnly=true) public List<StockBatch> batches(String audience){return batches.findByAudience(audience);}
    @Transactional(readOnly=true) public List<StockBatch> dispatchableLots(String audience){
        LocalDate today=LocalDate.now(ZoneId.of("America/Lima"));
        return batches.findByAudience(audience).stream().filter(b->b.canDispatch(today))
            .sorted(Comparator.comparing(StockBatch::expirationDate).thenComparing(StockBatch::id)).toList();
    }
    @Transactional(readOnly=true) public InventoryProduct product(String audience,String id){
        return products.findById(id,audience).orElseThrow(()->new DomainException("Producto no encontrado"));
    }
}
