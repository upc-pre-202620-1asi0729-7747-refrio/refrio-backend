package com.refrio.platform.iam.infrastructure.persistence.jpa.repositories;

import com.refrio.platform.iam.infrastructure.persistence.jpa.entities.PasswordResetTokenEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import java.util.Optional;

public interface PasswordResetJpaRepository extends JpaRepository<PasswordResetTokenEntity,Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PasswordResetTokenEntity> findByTokenHashAndUsedAtIsNull(String tokenHash);
}
