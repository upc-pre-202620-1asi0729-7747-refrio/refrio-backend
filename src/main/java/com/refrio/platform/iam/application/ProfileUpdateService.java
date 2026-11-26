package com.refrio.platform.iam.application;

import com.refrio.platform.iam.domain.model.aggregates.UserAccount;
import com.refrio.platform.iam.domain.model.repositories.UserAccountRepository;
import com.refrio.platform.shared.domain.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileUpdateService {
    private final UserAccountRepository users;
    public ProfileUpdateService(UserAccountRepository users){this.users=users;}

    @Transactional
    public UserAccount update(Long userId,String firstName,String lastName,String email,String sede) {
        var user=users.byId(userId).orElseThrow(()->new DomainException("Cuenta inexistente"));
        if(firstName==null||firstName.isBlank()||lastName==null||lastName.isBlank())
            throw new DomainException("Nombre y apellido obligatorios");
        if(email==null||email.isBlank())throw new DomainException("Correo obligatorio");
        String normalized=email.trim().toLowerCase(java.util.Locale.ROOT);
        if(!user.email().equalsIgnoreCase(normalized)&&users.emailExists(normalized))
            throw new DomainException("El correo ya está registrado");
        // La identidad, el rol, la suscripción y el hash nunca vienen del formulario.
        return users.save(new UserAccount(user.id(),user.username(),normalized,user.passwordHash(),
            firstName.trim(),lastName.trim(),user.role(),user.subscriptionPlan(),
            sede==null?"":sede.trim()));
    }
}
