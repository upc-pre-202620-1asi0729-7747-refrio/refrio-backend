package com.refrio.platform.analytics;
import com.refrio.platform.analytics.application.*;
import com.refrio.platform.analytics.domain.model.OperationalMetrics;
import com.refrio.platform.analytics.domain.model.repositories.AnalyticsReadRepository;
import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class AnalyticsApplicationServiceTest {
 @Test void analyticsAreRestrictedToTheActorAudience(){
  AnalyticsReadRepository repo=mock(AnalyticsReadRepository.class);
  when(repo.calculate("Minorista","2")).thenReturn(new OperationalMetrics(2,3,5,4,1,10));
  AuthenticatedUser user=new AuthenticatedUser(2L,"bodega","Minorista");
  assertEquals(5,new AnalyticsService(repo).summary(user).shipmentCount());
  assertEquals(80.0,new AnalyticsReportService(repo).generate(user).deliveryRate().value());
  verify(repo,times(2)).calculate("Minorista","2");
 }
}
