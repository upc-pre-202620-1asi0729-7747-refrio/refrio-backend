package com.refrio.platform.inventory.domain.model.aggregates;
import com.refrio.platform.shared.domain.DomainException;
import java.time.Instant;
import java.util.UUID;
/** Historical telemetry reading. Does not imply a physical sensor integration. */
public record TemperatureReading(String id,String productId,String audience,double value,String at) {
    public TemperatureReading {
        if(id==null||id.isBlank()||productId==null||productId.isBlank())
            throw new DomainException("La lectura requiere identificador y producto");
        if(!"Supervisor".equals(audience)&&!"Minorista".equals(audience))throw new DomainException("Audiencia inválida");
        if(!Double.isFinite(value)||value< -100||value>100)throw new DomainException("Lectura de temperatura inválida");
    }
    public static TemperatureReading capture(String productId,String audience,double value){
        return new TemperatureReading(UUID.randomUUID().toString(),productId,audience,value,Instant.now().toString());
    }
}
