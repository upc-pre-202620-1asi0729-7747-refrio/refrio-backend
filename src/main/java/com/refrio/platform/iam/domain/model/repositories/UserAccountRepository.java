package com.refrio.platform.iam.domain.model.repositories;
import com.refrio.platform.iam.domain.model.aggregates.UserAccount;
import java.util.*;
public interface UserAccountRepository {
 Optional<UserAccount> byUsername(String username);
 Optional<UserAccount> byId(Long id);
 boolean usernameExists(String username);
 boolean emailExists(String email);
 UserAccount save(UserAccount user);
 long count();
}
