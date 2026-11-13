package com.refrio.platform.operations.application.ports;
import com.refrio.platform.operations.domain.model.valueobjects.*;
public interface WeatherProvider {
    AmbientWeather current(GeoCoordinates coordinates);
}
