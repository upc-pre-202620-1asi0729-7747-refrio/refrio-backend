package com.refrio.platform.iam.domain.model.aggregates;

import com.refrio.platform.iam.domain.model.valueobjects.*;
import com.refrio.platform.shared.domain.DomainException;

/** IAM aggregate root. The domain keeps credentials hashed and never exposes them in resources. */
public record UserAccount(Long id,String username,String email,String passwordHash,
                          String firstName,String lastName,String role,String subscriptionPlan,String sede) {
    public UserAccount {
        username=new Username(username).value();
        email=new EmailAddress(email).value();
        UserRole.parse(role);
        if(id!=null&&id<=0)throw new DomainException("Identificador de usuario inválido");
        if(passwordHash==null||passwordHash.isBlank())throw new DomainException("Hash de contraseña requerido");
        if(firstName==null||lastName==null)throw new DomainException("Nombre y apellido requeridos");
    }
}
