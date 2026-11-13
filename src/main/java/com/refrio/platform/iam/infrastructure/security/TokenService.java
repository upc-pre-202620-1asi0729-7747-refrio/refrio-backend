package com.refrio.platform.iam.infrastructure.security;
import com.refrio.platform.iam.domain.model.AuthenticatedUser;
import com.fasterxml.jackson.databind.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
@Component
public class TokenService {
 private final byte[] secret;
 private final long ttl;
 private final ObjectMapper mapper;
 public TokenService(@Value("${app.security.jwt-secret:}") String secret,@Value("${app.security.token-ttl-seconds:86400}") long ttl,ObjectMapper mapper){
  if(secret.getBytes(StandardCharsets.UTF_8).length<32)throw new IllegalStateException("JWT_SECRET debe tener al menos 32 bytes");
  this.secret=secret.getBytes(StandardCharsets.UTF_8);this.ttl=ttl;this.mapper=mapper;
 }
 public long ttl(){return ttl;}
 public String issue(AuthenticatedUser user){
  try{
   String header=enc("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
   String body=enc(mapper.writeValueAsString(Map.of("sub",user.id().toString(),"username",user.username(),"role",user.role(),"iat",Instant.now().getEpochSecond(),"exp",Instant.now().getEpochSecond()+ttl)));
   String signed=header+"."+body;return signed+"."+Base64.getUrlEncoder().withoutPadding().encodeToString(sign(signed));
  }catch(Exception e){throw new IllegalStateException(e);}
 }
 public Optional<AuthenticatedUser> verify(String token){
  try{
   String[] parts=token.split("\\.");if(parts.length!=3)return Optional.empty();
   String msg=parts[0]+"."+parts[1];byte[] actual=Base64.getUrlDecoder().decode(parts[2]);
   if(!java.security.MessageDigest.isEqual(sign(msg),actual))return Optional.empty();
   JsonNode header=mapper.readTree(Base64.getUrlDecoder().decode(parts[0]));
   if(!"HS256".equals(header.path("alg").asText()))return Optional.empty();
   JsonNode claims=mapper.readTree(Base64.getUrlDecoder().decode(parts[1]));
   if(claims.path("exp").asLong(0)<=Instant.now().getEpochSecond())return Optional.empty();
   return Optional.of(new AuthenticatedUser(Long.parseLong(claims.path("sub").asText()),claims.path("username").asText(),claims.path("role").asText()));
  }catch(Exception e){return Optional.empty();}
 }
 private String enc(String x){return Base64.getUrlEncoder().withoutPadding().encodeToString(x.getBytes(StandardCharsets.UTF_8));}
 private byte[] sign(String data)throws Exception{Mac mac=Mac.getInstance("HmacSHA256");mac.init(new SecretKeySpec(secret,"HmacSHA256"));return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));}
}
