package com.refrio.platform.iam;
import com.refrio.platform.iam.domain.model.aggregates.UserAccount;
import com.refrio.platform.shared.domain.DomainException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class UserAccountDomainTest {
 @Test void userValidatesIdentityAndStoresOnlyHashedPassword(){
  UserAccount u=new UserAccount(7L,"bodega","MIN@EXAMPLE.ORG","bcrypt-hash","Ana","Ríos","Minorista","Basico","Surco");
  assertEquals("min@example.org",u.email());assertEquals("bcrypt-hash",u.passwordHash());
  assertThrows(DomainException.class,()->new UserAccount(2L,"x","ana@example.org","hash","Ana","Ríos","Minorista","Basico","Surco"));
  assertThrows(DomainException.class,()->new UserAccount(2L,"good","ana@example.org","hash","Ana","Ríos","ROOT","Basico","Surco"));
  assertThrows(DomainException.class,()->new UserAccount(2L,"good","ana@example.org","","Ana","Ríos","Minorista","Basico","Surco"));
 }
}
