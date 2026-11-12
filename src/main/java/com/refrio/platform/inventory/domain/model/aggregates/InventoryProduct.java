package com.refrio.platform.inventory.domain.model.aggregates;

import com.refrio.platform.inventory.domain.model.valueobjects.StockQuantity;
import com.refrio.platform.shared.domain.DomainException;

/** The stock of a product is maintained by inventory commands, never by a REST controller. */
public record InventoryProduct(String id, String name, String category, String stock,
                               String temp, String exp, String warehouse,
                               String status, String sourceProductId) {
    public InventoryProduct {
        if (id == null || id.isBlank()) throw new DomainException("ID de producto requerido");
        if (name == null || name.isBlank()) throw new DomainException("Nombre de producto requerido");
        new StockQuantity(parseStock(stock));
    }
    private static int parseStock(String value) {
        try { return Integer.parseInt(value); }
        catch (Exception e) { throw new DomainException("El stock debe ser entero"); }
    }
    public int availableUnits() { return parseStock(stock); }
    public InventoryProduct withStockAdjustment(int amount) {
        int next = new StockQuantity(availableUnits()).add(amount).units();
        return new InventoryProduct(id, name, category, String.valueOf(next),temp, exp, warehouse,status,sourceProductId);
    }
    public InventoryProduct withTemperature(double reading) {
        if (!Double.isFinite(reading) || reading < -100 || reading > 100)
            throw new DomainException("Lectura de temperatura inválida");
        return new InventoryProduct(id,name,category,stock,reading+"°C",exp,warehouse,status,sourceProductId);
    }
}
