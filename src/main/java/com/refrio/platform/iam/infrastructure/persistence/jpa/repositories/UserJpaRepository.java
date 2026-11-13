package com.refrio.platform.iam.infrastructure.persistence.jpa.repositories;
import com.refrio.platform.iam.infrastructure.persistence.jpa.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface UserJpaRepository extends JpaRepository<UserEntity,Long> {
 Optional<UserEntity> findByUsernameIgnoreCase(String username);
 boolean existsByUsernameIgnoreCase(String username);
 boolean existsByEmailIgnoreCase(String email);
}
