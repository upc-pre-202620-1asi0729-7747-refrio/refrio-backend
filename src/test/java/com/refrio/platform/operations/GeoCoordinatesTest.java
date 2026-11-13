package com.refrio.platform.operations;
import com.refrio.platform.operations.domain.model.valueobjects.GeoCoordinates;
import com.refrio.platform.shared.domain.DomainException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class GeoCoordinatesTest {
 @Test void acceptsLimaAndRejectsImpossibleCoordinates(){
  assertEquals(-12.05,new GeoCoordinates(-12.05,-77.04).latitude());
  assertThrows(DomainException.class,()->new GeoCoordinates(91,-77));
  assertThrows(DomainException.class,()->new GeoCoordinates(-12,181));
  assertThrows(DomainException.class,()->new GeoCoordinates(Double.NaN,-77));
 }
}
