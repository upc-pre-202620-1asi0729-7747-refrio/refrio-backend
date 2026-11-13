package com.refrio.platform.iam;
import com.refrio.platform.iam.domain.model.aggregates.UserAccount;
import com.refrio.platform.iam.application.*;
import com.refrio.platform.iam.domain.model.*;
import com.refrio.platform.iam.domain.model.repositories.UserAccountRepository;
import com.refrio.platform.iam.infrastructure.security.TokenService;
import com.refrio.platform.shared.domain.DomainException;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
class IamApplicationServiceTest {
 @Test void publicRegistrationRejectsSupervisorAndDuplicateUsername(){
  UserAccountRepository repo=mock(UserAccountRepository.class);PasswordEncoder passwords=mock(PasswordEncoder.class);TokenService jwt=mock(TokenService.class);
  IamApplicationService app=new IamApplicationService(repo,passwords,jwt,false);
  assertThrows(DomainException.class,()->app.register("admin","admin@example.com","secure-pass-123","Admin","Refrio","Supervisor","Pro","Lima"));
  when(repo.usernameExists("bodega")).thenReturn(true);
  assertThrows(DomainException.class,()->app.register("bodega","bodega@example.com","secure-pass-123","Ana","Rios","Minorista","Basico","Surco"));
  verify(repo,never()).save(any());
 }
 @Test void wrongPasswordDoesNotIssueToken(){
  UserAccountRepository repo=mock(UserAccountRepository.class);PasswordEncoder passwords=mock(PasswordEncoder.class);TokenService jwt=mock(TokenService.class);
  IamApplicationService app=new IamApplicationService(repo,passwords,jwt,false);
  when(repo.byUsername("bodega")).thenReturn(Optional.of(new UserAccount(1L,"bodega","bodega@example.org","hash","Ana","Rios","Minorista","Basico","Lima")));
  assertThrows(InvalidCredentialsException.class,()->app.signIn("bodega","incorrect"));
  verify(jwt,never()).issue(any());
 }
}
