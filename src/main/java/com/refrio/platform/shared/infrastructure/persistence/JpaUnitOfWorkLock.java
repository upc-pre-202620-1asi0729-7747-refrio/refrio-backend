package com.refrio.platform.shared.infrastructure.persistence;
import com.refrio.platform.shared.domain.repositories.UnitOfWorkLock;
import org.springframework.stereotype.Component;
@Component
public class JpaUnitOfWorkLock implements UnitOfWorkLock {
 private final MutexJpaRepository mutex;
 public JpaUnitOfWorkLock(MutexJpaRepository mutex){this.mutex=mutex;}
 public void acquire(){mutex.lock(1).orElseThrow(()->new IllegalStateException("Missing operation mutex, run Flyway migration"));}
}
