package com.refrio.platform.iam.domain.model.valueobjects;
import com.refrio.platform.shared.domain.DomainException;
public enum UserRole {
    SUPERVISOR("Supervisor"), MINORISTA("Minorista");
    private final String wire;
    UserRole(String wire){this.wire=wire;}
    public String wire(){return wire;}
    public static UserRole parse(String raw){
        for(UserRole role:values())if(role.wire.equals(raw))return role;
        throw new DomainException("Rol de usuario no válido");
    }
}
