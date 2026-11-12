package com.refrio.platform.iam.interfaces.rest.resources;
import com.refrio.platform.iam.domain.model.aggregates.UserAccount;

/** Explicit public representation: never exposes the password hash. */
public record UserResource(Long id,String username,String email,String firstName,
      String lastName,String role,String subscriptionPlan,String sede) {
    public static UserResource from(UserAccount user){
        return new UserResource(user.id(),user.username(),user.email(),user.firstName(),
            user.lastName(),user.role(),user.subscriptionPlan(),user.sede());
    }
}
