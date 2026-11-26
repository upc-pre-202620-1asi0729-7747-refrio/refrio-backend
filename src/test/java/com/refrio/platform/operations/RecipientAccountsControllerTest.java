package com.refrio.platform.operations;

import com.refrio.platform.operations.interfaces.rest.RecipientAccountsController;
import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import com.refrio.platform.iam.domain.model.repositories.UserAccountRepository;
import com.refrio.platform.shared.application.compatibility.DocumentRepository;
import com.refrio.platform.shared.domain.ForbiddenOperationException;
import com.refrio.platform.shared.domain.repositories.UnitOfWorkLock;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class RecipientAccountsControllerTest {
    @Test void retailCannotEnumerateAccounts() {
        var controller=new RecipientAccountsController(mock(EntityManager.class),
            mock(UserAccountRepository.class),mock(DocumentRepository.class),mock(UnitOfWorkLock.class));
        assertThrows(ForbiddenOperationException.class,
            ()->controller.list(new AuthenticatedUser(1L,"retail","Minorista")));
    }
}
