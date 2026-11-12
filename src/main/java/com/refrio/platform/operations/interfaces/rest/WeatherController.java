package com.refrio.platform.operations.interfaces.rest;
import com.refrio.platform.operations.application.ExternalWeatherService;
import com.refrio.platform.operations.domain.model.valueobjects.AmbientWeather;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/operations/weather")
@Tag(name="Operations",description="Ambient weather via a third-party provider")
@SecurityRequirement(name="bearerAuth")
public class WeatherController {
    private final ExternalWeatherService service;
    public WeatherController(ExternalWeatherService service){this.service=service;}
    @Operation(summary="Get ambient temperature and humidity for a coordinate (not cargo temperature)")
    @GetMapping public AmbientWeather current(@RequestParam double latitude,@RequestParam double longitude){
        return service.current(latitude,longitude);
    }
}
