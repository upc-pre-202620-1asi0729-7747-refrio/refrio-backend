package com.refrio.platform.shared.infrastructure.bootstrap;
import com.refrio.platform.iam.domain.model.aggregates.UserAccount;
import com.refrio.platform.iam.domain.model.repositories.UserAccountRepository;
import com.refrio.platform.shared.infrastructure.persistence.ResourceStore;
import com.fasterxml.jackson.databind.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.nio.file.*;
import java.util.*;
@Component
public class StartupData implements ApplicationRunner {
 private final ResourceStore resources;private final UserAccountRepository users;private final PasswordEncoder passwords;private final ObjectMapper mapper;
 private final String adminName,adminPassword,seedFile,retailPassword;private final boolean seed;
 public StartupData(ResourceStore resources,UserAccountRepository users,PasswordEncoder passwords,ObjectMapper mapper,
  @Value("${app.bootstrap.admin-username:}") String adminName,@Value("${app.bootstrap.admin-password:}") String adminPassword,
  @Value("${app.seed.enabled:false}") boolean seed,@Value("${app.seed.file:}") String seedFile,
  @Value("${app.seed.retail-password:}") String retailPassword){
  this.resources=resources;this.users=users;this.passwords=passwords;this.mapper=mapper;this.adminName=adminName;this.adminPassword=adminPassword;this.seed=seed;this.seedFile=seedFile;this.retailPassword=retailPassword;
 }
 @Override @Transactional public void run(ApplicationArguments args)throws Exception {
  if(!adminName.isBlank()){
   if(adminPassword.length()<12)throw new IllegalStateException("BOOTSTRAP_ADMIN_PASSWORD debe tener al menos 12 caracteres");
   if(users.byUsername(adminName).isEmpty())users.save(new UserAccount(null,adminName,adminName+"@refrio.local",passwords.encode(adminPassword),"Administrador","Refrio","Supervisor","Profesional","Lima Central"));
  }
  if(seed){
   if(retailPassword.length()<12)throw new IllegalStateException("DEMO_RETAIL_PASSWORD debe tener al menos 12 caracteres cuando SEED_DEMO=true");
   var demoRetail=users.byUsername("bodega-demo")
     .orElseGet(()->users.save(new UserAccount(null,"bodega-demo","bodega.demo@refrio.local",
        passwords.encode(retailPassword),"Bodega","Demo","Minorista","Basico","Bodega Central")));
   String demoRetailId=String.valueOf(demoRetail.id());
   JsonNode data=seedFile.isBlank()?mapper.readTree(new ClassPathResource("demo-seed.json").getInputStream()):mapper.readTree(Files.readString(Path.of(seedFile)));
   Set<String> accepted=Set.of("inventory","bodeguero-inventory","batches","shipments","clients","suppliers","alerts","incidents","losses","telemetry","events","certificates","leads");
   var fields=data.fields();while(fields.hasNext()){var field=fields.next();if(!accepted.contains(field.getKey())||!field.getValue().isArray())continue;
    for(JsonNode item:field.getValue()){
     String id=item.path("id").asText("");
     if(!id.isBlank()&&resources.find(field.getKey(),id).isEmpty()) {
      var copy=resources.copy(item);
      if("clients".equals(field.getKey())&&"2".equals(item.path("recipientId").asText()))
       copy.put("recipientId",demoRetailId);
      resources.put(field.getKey(),copy);
     }
    }
   }
  }
 }
}
