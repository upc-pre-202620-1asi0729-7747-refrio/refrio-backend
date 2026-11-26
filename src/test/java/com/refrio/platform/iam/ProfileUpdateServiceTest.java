package com.refrio.platform.iam;

import com.refrio.platform.iam.application.ProfileUpdateService;
import com.refrio.platform.iam.domain.model.aggregates.UserAccount;
import com.refrio.platform.iam.domain.model.repositories.UserAccountRepository;
import com.refrio.platform.shared.domain.DomainException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Optional;

class ProfileUpdateServiceTest {
    @Test void cannotReuseAnotherUsersEmail() {
        var users=mock(UserAccountRepository.class);
        when(users.byId(1L)).thenReturn(Optional.of(new UserAccount(1L,"sample","a@demo.test","bcrypt-hash","Ana","Sol","Minorista","Basico","Lima")));
        when(users.emailExists("b@demo.test")).thenReturn(true);
        var service=new ProfileUpdateService(users);
        assertThrows(DomainException.class,()->service.update(1L,"Ana","Sol","b@demo.test","Lima"));
        verify(users,never()).save(any());
    }
}
