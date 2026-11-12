package com.refrio.platform.operations;
import com.refrio.platform.operations.application.ExternalWeatherService;
import com.refrio.platform.operations.application.ports.WeatherProvider;
import com.refrio.platform.operations.domain.model.valueobjects.*;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;
class ExternalWeatherServiceTest {
 @Test void invokesTheExternalPortWithoutEmbeddingHttpInTheDomain(){
  WeatherProvider fake=coordinates -> new AmbientWeather(coordinates,20.5,50,Instant.parse("2026-11-11T12:00:00Z"),"Fake");
  var service=new ExternalWeatherService(fake);
  assertEquals(20.5,service.current(-12.05,-77.04).temperatureCelsius());
  assertEquals("Fake",service.current(-12.05,-77.04).provider());
 }
}
