package com.refrio.platform.iam.interfaces.rest;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.refrio.platform.iam.application.IamApplicationService;
import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import com.refrio.platform.iam.interfaces.rest.resources.UserResource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name="IAM")
@RestController @RequestMapping("/api/v1/iam")
public class IamV1Controller {
    private final IamApplicationService application;
    public IamV1Controller(IamApplicationService application){this.application=application;}
    @GetMapping("/users/me") public UserResource me(@AuthenticationPrincipal AuthenticatedUser actor){
        return UserResource.from(application.user(actor.id()));
    }
}
