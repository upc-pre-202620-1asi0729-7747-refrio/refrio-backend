package com.refrio.platform.inventory.domain.model.repositories;
import com.refrio.platform.inventory.domain.model.aggregates.TemperatureReading;
import java.util.List;
public interface TemperatureReadingRepository {
    TemperatureReading append(TemperatureReading reading);
    List<TemperatureReading> findByAudience(String audience);
}
