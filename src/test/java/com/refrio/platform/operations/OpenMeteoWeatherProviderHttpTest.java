package com.refrio.platform.operations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.refrio.platform.operations.infrastructure.external.ExternalWeatherUnavailableException;
import com.refrio.platform.operations.infrastructure.external.OpenMeteoWeatherProvider;
import com.refrio.platform.operations.domain.model.valueobjects.GeoCoordinates;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.*;

/** Real JDK HTTP adapter integration against a local stub; no third-party network required. */
class OpenMeteoWeatherProviderHttpTest {
 @Test void parsesTimeTemperatureAndHumidity() throws Exception {
  HttpServer server=HttpServer.create(new InetSocketAddress("127.0.0.1",0),0);
  server.createContext("/v1/forecast", exchange -> {
   byte[] json="{\"current\":{\"time\":\"2026-11-11T12:00\",\"temperature_2m\":24.3,\"relative_humidity_2m\":65}}"
      .getBytes(StandardCharsets.UTF_8);
   exchange.getResponseHeaders().set("Content-Type","application/json");
   exchange.sendResponseHeaders(200,json.length);
   try(var output=exchange.getResponseBody()){output.write(json);}
  });
  server.start();
  try {
   var client=new OpenMeteoWeatherProvider(new ObjectMapper(),true,"http://127.0.0.1:"+server.getAddress().getPort());
   var result=client.current(new GeoCoordinates(-12.05,-77.04));
   assertEquals(24.3,result.temperatureCelsius());
   assertEquals(65,result.humidityPercent());
   assertEquals("Open-Meteo",result.provider());
   assertEquals("2026-11-11T12:00:00Z",result.observedAt().toString());
  } finally {server.stop(0);}
 }
 @Test void upstreamFailureMapsToServiceUnavailableException() throws Exception {
  HttpServer server=HttpServer.create(new InetSocketAddress("127.0.0.1",0),0);
  server.createContext("/v1/forecast",exchange -> {
   exchange.sendResponseHeaders(503,-1);exchange.close();
  });
  server.start();
  try{
   var client=new OpenMeteoWeatherProvider(new ObjectMapper(),true,"http://127.0.0.1:"+server.getAddress().getPort());
   assertThrows(ExternalWeatherUnavailableException.class,
      ()->client.current(new GeoCoordinates(-12.05,-77.04)));
  } finally {server.stop(0);}
 }
}
