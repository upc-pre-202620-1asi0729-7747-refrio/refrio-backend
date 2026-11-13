package com.refrio.platform.shared.infrastructure.persistence;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
public interface MutexJpaRepository extends JpaRepository<OperationMutex,Integer> {
 @Lock(LockModeType.PESSIMISTIC_WRITE)
 @Query("select m from OperationMutex m where m.id = :id") Optional<OperationMutex> lock(@Param("id") Integer id);
}
