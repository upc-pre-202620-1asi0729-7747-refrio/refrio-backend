package com.refrio.platform.inventory.infrastructure.persistence.jpa.adapters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.refrio.platform.inventory.domain.model.aggregates.TemperatureReading;
import com.refrio.platform.inventory.domain.model.repositories.TemperatureReadingRepository;
import com.refrio.platform.shared.domain.DomainException;
import com.refrio.platform.shared.application.compatibility.DocumentRepository;
import com.refrio.platform.shared.infrastructure.persistence.jpa.adapters.JsonAggregateRepositorySupport;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public class TemperatureReadingRepositoryAdapter extends JsonAggregateRepositorySupport<TemperatureReading> implements TemperatureReadingRepository {
    public TemperatureReadingRepositoryAdapter(DocumentRepository store,ObjectMapper mapper){super("telemetry",TemperatureReading.class,store,mapper);}
    @Override public TemperatureReading append(TemperatureReading reading){
        if(findById(reading.id()).isPresent())throw new DomainException("La lectura ya fue registrada");
        return save(reading.id(),reading);
    }
    @Override public List<TemperatureReading> findByAudience(String audience){
        return findAll().stream().filter(t->audience.equals(t.audience())).toList();
    }
}
