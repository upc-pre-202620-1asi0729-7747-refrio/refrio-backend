package com.refrio.platform.iam.application.commands;
import com.refrio.platform.shared.domain.DomainException;
public record SignInCommand(String username,String password) {
    public SignInCommand {
        if(username==null||username.isBlank()||password==null||password.isBlank())
            throw new DomainException("Usuario y contraseña obligatorios");
    }
}
