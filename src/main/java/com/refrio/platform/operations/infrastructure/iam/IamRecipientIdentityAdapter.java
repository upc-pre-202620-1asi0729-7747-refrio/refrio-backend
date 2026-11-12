package com.refrio.platform.operations.infrastructure.iam;
import com.refrio.platform.iam.domain.model.repositories.UserAccountRepository;
import com.refrio.platform.operations.application.ports.RecipientIdentityPort;
import org.springframework.stereotype.Component;
@Component
public class IamRecipientIdentityAdapter implements RecipientIdentityPort {
    private final UserAccountRepository users;
    public IamRecipientIdentityAdapter(UserAccountRepository users){this.users=users;}
    @Override public boolean isExistingMinorista(String userId){
        try {
            return users.byId(Long.parseLong(userId)).filter(u->"Minorista".equals(u.role())).isPresent();
        }catch(NumberFormatException ex){return false;}
    }
}
