package com.refrio.platform.iam.interfaces.rest;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.refrio.platform.iam.domain.model.aggregates.UserAccount;
import com.refrio.platform.iam.application.*;
import com.refrio.platform.iam.domain.model.*;
import com.refrio.platform.iam.application.commands.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@Tag(name="IAM")
@RestController @RequestMapping("/api")
public class IamController {
 private final IamApplicationService service;
 public IamController(IamApplicationService service){this.service=service;}
 public record Credentials(@NotBlank String username,@NotBlank String password){}
 public record Registration(@NotBlank String username,@Email @NotBlank String email,@Size(min=8) String password,@NotBlank String firstName,@NotBlank String lastName,String role,String subscriptionPlan,String sede){}
 @PostMapping("/session") public Map<String,Object> login(@Valid @RequestBody Credentials c){return service.signIn(new SignInCommand(c.username(),c.password()));}
 @GetMapping("/session") public Map<String,Object> session(@AuthenticationPrincipal AuthenticatedUser principal){UserAccount user=service.user(principal.id());return profile(user);}
 @PostMapping("/users") public ResponseEntity<Map<String,Object>> register(@Valid @RequestBody Registration r){return ResponseEntity.status(201).body(service.register(new RegisterUserCommand(r.username(),r.email(),r.password(),r.firstName(),r.lastName(),r.role(),r.subscriptionPlan(),r.sede())));}
 private Map<String,Object> profile(UserAccount u){return Map.of("id",u.id(),"username",u.username(),"email",u.email(),"firstName",u.firstName(),"lastName",u.lastName(),"role",u.role(),"subscriptionPlan",u.subscriptionPlan(),"sede",u.sede());}
 @ExceptionHandler(InvalidCredentialsException.class)
 public ResponseEntity<Map<String,String>> authError(java.util.Locale locale){
  String message=locale.getLanguage().equals("es")?"Usuario o contraseña incorrectos":"Invalid username or password";
  return ResponseEntity.status(401).body(Map.of("message",message));
 }
}
