package com.refrio.platform.iam.application;
import com.refrio.platform.iam.domain.model.aggregates.UserAccount;
import com.refrio.platform.iam.domain.model.*;
import com.refrio.platform.iam.domain.model.repositories.UserAccountRepository;
import com.refrio.platform.iam.infrastructure.security.TokenService;
import com.refrio.platform.shared.domain.DomainException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
@Service
public class IamApplicationService {
 private final UserAccountRepository users;private final PasswordEncoder passwords;private final TokenService tokens;private final boolean allowSupervisor;
 public IamApplicationService(UserAccountRepository users,PasswordEncoder passwords,TokenService tokens,@Value("${app.bootstrap.allow-supervisor-registration:false}") boolean allowSupervisor){this.users=users;this.passwords=passwords;this.tokens=tokens;this.allowSupervisor=allowSupervisor;}

 public Map<String,Object> register(com.refrio.platform.iam.application.commands.RegisterUserCommand command){
  return register(command.username(),command.email(),command.password(),command.firstName(),
      command.lastName(),command.role(),command.subscriptionPlan(),command.sede());
 }
 public Map<String,Object> signIn(com.refrio.platform.iam.application.commands.SignInCommand command){
  return signIn(command.username(),command.password());
 }
 @Transactional public Map<String,Object> register(String username,String email,String password,String firstName,String lastName,String role,String plan,String sede){
  if(username==null||username.isBlank()||email==null||!email.contains("@")||password==null||password.length()<8)throw new DomainException("Usuario, correo y contraseña de al menos 8 caracteres son obligatorios");
  if(users.usernameExists(username)||users.emailExists(email))throw new DomainException("El usuario o correo ya existe");
  if("Supervisor".equals(role)&&!allowSupervisor)throw new DomainException("El registro público de supervisores está deshabilitado");
  UserAccount created=users.save(new UserAccount(null,username.trim(),email.trim(),passwords.encode(password),firstName==null?"":firstName,lastName==null?"":lastName,role==null?"Minorista":role,plan==null?"Basico":plan,sede==null?"":sede));
  return Map.of("id",created.id(),"username",created.username(),"message","User created successfully");
 }
 @Transactional(readOnly=true) public Map<String,Object> signIn(String username,String password){
  UserAccount user=users.byUsername(username==null?"":username).orElseThrow(()->new com.refrio.platform.iam.application.InvalidCredentialsException());
  if(!passwords.matches(password==null?"":password,user.passwordHash()))throw new InvalidCredentialsException();
  return Map.of("token",tokens.issue(new AuthenticatedUser(user.id(),user.username(),user.role())),"tokenType","Bearer","expiresIn",tokens.ttl(),"role",user.role());
 }
 @Transactional(readOnly=true) public UserAccount user(Long id){return users.byId(id).orElseThrow(()->new DomainException("Usuario inexistente"));}
}
