package com.refrio.platform.operations.infrastructure.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.refrio.platform.operations.application.ports.WeatherProvider;
import com.refrio.platform.operations.domain.model.valueobjects.AmbientWeather;
import com.refrio.platform.operations.domain.model.valueobjects.GeoCoordinates;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Locale;

/** Open-Meteo adapter, isolated behind WeatherProvider for testing/replacement. */
@Repository
public class OpenMeteoWeatherProvider implements WeatherProvider {
    private final HttpClient client;
    private final ObjectMapper mapper;
    private final boolean enabled;
    private final String baseUrl;
    public OpenMeteoWeatherProvider(ObjectMapper mapper,
                    @Value("${app.external.weather.enabled:true}") boolean enabled,
                    @Value("${app.external.weather.base-url:https://api.open-meteo.com}") String baseUrl) {
        this.client=HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(4)).build();
        this.mapper=mapper;
        this.enabled=enabled;
        this.baseUrl=baseUrl.replaceAll("/+$", "");
        if(!this.baseUrl.startsWith("https://") && !this.baseUrl.startsWith("http://localhost:") && !this.baseUrl.startsWith("http://127.0.0.1:"))
            throw new IllegalArgumentException("Weather provider must use HTTPS");
    }
    @Override public AmbientWeather current(GeoCoordinates coordinates) {
        if(!enabled) throw new ExternalWeatherUnavailableException("Weather provider disabled");
        String url=String.format(Locale.ROOT,
            "%s/v1/forecast?latitude=%.5f&longitude=%.5f&current=temperature_2m,relative_humidity_2m&timezone=UTC",
            baseUrl,coordinates.latitude(),coordinates.longitude());
        try {
            HttpRequest request=HttpRequest.newBuilder(URI.create(url))
                .GET().timeout(Duration.ofSeconds(7)).header("Accept","application/json").build();
            HttpResponse<String> response=client.send(request,HttpResponse.BodyHandlers.ofString());
            if(response.statusCode()!=200)throw new ExternalWeatherUnavailableException("Weather provider HTTP " +response.statusCode());
            JsonNode current=mapper.readTree(response.body()).path("current");
            if(!current.path("temperature_2m").isNumber()||!current.path("relative_humidity_2m").isNumber())
                throw new ExternalWeatherUnavailableException("Unexpected weather provider response");
            String at=current.path("time").asText();
            // Open-Meteo UTC timestamps are local ISO strings, often without seconds or Z.
            Instant observedAt=at.endsWith("Z")?Instant.parse(at)
                :LocalDateTime.parse(at).toInstant(ZoneOffset.UTC);
            return new AmbientWeather(coordinates,current.path("temperature_2m").asDouble(),
                current.path("relative_humidity_2m").asInt(),observedAt,"Open-Meteo");
        }catch(ExternalWeatherUnavailableException ex){throw ex;}
        catch(InterruptedException e){Thread.currentThread().interrupt();throw new ExternalWeatherUnavailableException("Weather request interrupted",e);}
        catch(Exception e){throw new ExternalWeatherUnavailableException("Unable to retrieve current ambient weather",e);}
    }
}
