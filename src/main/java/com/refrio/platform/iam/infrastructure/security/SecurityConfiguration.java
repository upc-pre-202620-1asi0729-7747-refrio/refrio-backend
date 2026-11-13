package com.refrio.platform.iam.infrastructure.security;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;
import java.util.*;
@Configuration
public class SecurityConfiguration {
 @Bean PasswordEncoder encoder(){return new BCryptPasswordEncoder(12);}
 @Bean CorsConfigurationSource cors(@Value("${app.security.allowed-origins:http://localhost:4200}") String origins){
  CorsConfiguration cfg=new CorsConfiguration();cfg.setAllowedOriginPatterns(Arrays.stream(origins.split(",")).map(String::trim).toList());
  cfg.setAllowedHeaders(List.of("Authorization","Content-Type","Accept"));cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
  cfg.setAllowCredentials(false);cfg.setMaxAge(3600L);
  UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();source.registerCorsConfiguration("/**",cfg);return source;
 }
 @Bean SecurityFilterChain chain(HttpSecurity http,JwtFilter jwt)throws Exception{
  return http.csrf(c->c.disable()).cors(c->{}).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
   .authorizeHttpRequests(a->a
    .requestMatchers(HttpMethod.OPTIONS,"/**").permitAll()
    .requestMatchers(HttpMethod.POST,"/api/session","/api/users","/api/leads","/api/v1/public-portal/leads").permitAll()
    .requestMatchers("/actuator/health/**","/swagger-ui/**","/swagger-ui.html","/v3/api-docs/**").permitAll()
    .anyRequest().authenticated())
   .exceptionHandling(e->e.authenticationEntryPoint((request,response,error)->{response.setStatus(401);response.setContentType("application/json");String accept=request.getHeader("Accept-Language");boolean spanish=accept!=null&&accept.toLowerCase(java.util.Locale.ROOT).startsWith("es");response.getWriter().write(spanish?"{\"message\":\"Autenticación requerida\"}":"{\"message\":\"Authentication required\"}");}))
   .addFilterBefore(jwt,UsernamePasswordAuthenticationFilter.class).build();
 }
}
