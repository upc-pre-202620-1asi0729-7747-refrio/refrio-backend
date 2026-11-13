package com.refrio.platform.iam.application.commands;
import com.refrio.platform.iam.domain.model.valueobjects.*;
import com.refrio.platform.shared.domain.DomainException;
public record RegisterUserCommand(String username,String email,String password,String firstName,
             String lastName,String role,String subscriptionPlan,String sede) {
    public RegisterUserCommand {
        new Username(username);new EmailAddress(email);UserRole.parse(role==null?"Minorista":role);
        if(password==null||password.length()<8||firstName==null||firstName.isBlank()
                ||lastName==null||lastName.isBlank())throw new DomainException("Datos personales o contraseña inválidos");
    }
}
