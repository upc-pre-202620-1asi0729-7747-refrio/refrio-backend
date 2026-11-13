package com.refrio.platform.operations.application;
import com.refrio.platform.operations.application.ports.WeatherProvider;
import com.refrio.platform.operations.domain.model.valueobjects.*;
import org.springframework.stereotype.Service;
/** Access to third-party ambient weather with an anti-corruption boundary. */
@Service
public class ExternalWeatherService {
    private final WeatherProvider provider;
    public ExternalWeatherService(WeatherProvider provider){this.provider=provider;}
    public AmbientWeather current(double latitude,double longitude){return provider.current(new GeoCoordinates(latitude,longitude));}
}
