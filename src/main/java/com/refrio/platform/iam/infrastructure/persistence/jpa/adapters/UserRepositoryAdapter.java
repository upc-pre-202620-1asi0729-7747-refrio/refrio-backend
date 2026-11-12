package com.refrio.platform.iam.infrastructure.persistence.jpa.adapters;
import com.refrio.platform.iam.domain.model.aggregates.UserAccount;
import com.refrio.platform.iam.domain.model.repositories.UserAccountRepository;
import com.refrio.platform.iam.infrastructure.persistence.jpa.entities.UserEntity;
import com.refrio.platform.iam.infrastructure.persistence.jpa.repositories.UserJpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class UserRepositoryAdapter implements UserAccountRepository {
 private final UserJpaRepository repo;
 public UserRepositoryAdapter(UserJpaRepository repo){this.repo=repo;}
 public Optional<UserAccount> byUsername(String username){return repo.findByUsernameIgnoreCase(username).map(this::toDomain);}
 public Optional<UserAccount> byId(Long id){return repo.findById(id).map(this::toDomain);}
 public boolean usernameExists(String x){return repo.existsByUsernameIgnoreCase(x);}
 public boolean emailExists(String x){return repo.existsByEmailIgnoreCase(x);}
 public long count(){return repo.count();}
 public UserAccount save(UserAccount user){UserEntity entity=user.id()==null?new UserEntity():repo.findById(user.id()).orElseThrow();
 entity.username=user.username();entity.email=user.email();entity.passwordHash=user.passwordHash();entity.firstName=user.firstName();entity.lastName=user.lastName();entity.role=user.role();entity.subscriptionPlan=user.subscriptionPlan();entity.sede=user.sede();return toDomain(repo.save(entity));}
 private UserAccount toDomain(UserEntity e){return new UserAccount(e.id,e.username,e.email,e.passwordHash,e.firstName,e.lastName,e.role,e.subscriptionPlan,e.sede);}
}
