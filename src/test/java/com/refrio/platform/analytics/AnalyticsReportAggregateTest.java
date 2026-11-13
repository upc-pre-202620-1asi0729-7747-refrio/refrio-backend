package com.refrio.platform.analytics;
import com.refrio.platform.analytics.domain.model.OperationalMetrics;
import com.refrio.platform.analytics.domain.model.aggregates.AnalyticsReportAggregate;
import com.refrio.platform.analytics.domain.model.valueobjects.Percentage;
import com.refrio.platform.shared.domain.DomainException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class AnalyticsReportAggregateTest {
 @Test void reportsAreComputedFromFactsNotMockedBusinessNumbers(){
  var metrics=new OperationalMetrics(10,7,4,3,12,50);
  var report=AnalyticsReportAggregate.create("Supervisor",metrics);
  assertEquals("Supervisor",report.audience());assertEquals(75.0,report.deliveryRate().value());
  assertEquals(12,report.metrics().recordedLossUnits());
  assertNotNull(report.generatedAt());
 }
 @Test void percentageCannotExceedItsNumeratorDenominatorConstraints(){
  assertEquals(0.0,Percentage.ratio(0,0).value());
  assertThrows(DomainException.class,()->Percentage.ratio(3,2));
  assertThrows(DomainException.class,()->new Percentage(-1));
 }
}
