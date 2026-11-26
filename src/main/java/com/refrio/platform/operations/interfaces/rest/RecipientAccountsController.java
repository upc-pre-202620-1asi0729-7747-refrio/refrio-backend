package com.refrio.platform.operations.interfaces.rest;

import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import com.refrio.platform.iam.domain.model.repositories.UserAccountRepository;
import com.refrio.platform.iam.infrastructure.persistence.jpa.entities.UserEntity;
import com.refrio.platform.shared.application.compatibility.DocumentRepository;
import com.refrio.platform.shared.domain.DomainException;
import com.refrio.platform.shared.domain.ForbiddenOperationException;
import com.refrio.platform.shared.domain.repositories.UnitOfWorkLock;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RecipientAccountsController {
    private final EntityManager em;
    private final UserAccountRepository users;
    private final DocumentRepository store;
    private final UnitOfWorkLock mutex;

    public RecipientAccountsController(EntityManager em, UserAccountRepository users,
                                       DocumentRepository store, UnitOfWorkLock mutex) {
        this.em=em;this.users=users;this.store=store;this.mutex=mutex;
    }
    public record RetailAccount(String id,String username){}
    public record LinkRecipientRequest(String recipientId){}

    @GetMapping("/retail-accounts")
    @Transactional
    public List<RetailAccount> list(@AuthenticationPrincipal AuthenticatedUser actor) {
        supervisor(actor);
        return em.createQuery("select u from UserEntity u where u.role = :role order by u.username",UserEntity.class)
            .setParameter("role","Minorista").setMaxResults(500).getResultList().stream()
            .map(u->new RetailAccount(u.id.toString(),u.username)).toList();
    }

    @PatchMapping("/clients/{id}/recipient")
    @Transactional
    public ObjectNode link(@PathVariable String id,@RequestBody LinkRecipientRequest request,
                           @AuthenticationPrincipal AuthenticatedUser actor) {
        supervisor(actor);
        if(request==null)throw new DomainException("Solicitud inválida");
        String recipient=request.recipientId()==null?"":request.recipientId().trim();
        if(!recipient.isBlank()) {
            Long recipientId;
            try {recipientId=Long.valueOf(recipient);} catch(NumberFormatException ex) {
                throw new DomainException("El ID del destinatario debe ser numérico");
            }
            var account=users.byId(recipientId)
                .orElseThrow(()->new DomainException("Destinatario inexistente"));
            if(!"Minorista".equals(account.role()))
                throw new DomainException("El destinatario debe tener rol Minorista");
        }
        mutex.acquire();
        ObjectNode client=store.get("clients",id);
        client.put("recipientId",recipient);
        store.put("clients",client);
        return store.get("clients",id);
    }

    private static void supervisor(AuthenticatedUser actor) {
        if(actor==null||!actor.supervisor())
            throw new ForbiddenOperationException("Esta acción requiere rol Supervisor");
    }
}
