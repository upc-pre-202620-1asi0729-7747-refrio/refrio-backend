package com.refrio.platform.iam.infrastructure.security;
import com.refrio.platform.iam.domain.model.repositories.UserAccountRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;
@Component
public class JwtFilter extends OncePerRequestFilter {
 private final TokenService tokens;private final UserAccountRepository users;
 public JwtFilter(TokenService tokens,UserAccountRepository users){this.tokens=tokens;this.users=users;}
 @Override protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain)throws ServletException,IOException{
  String header=req.getHeader("Authorization");
  if(header!=null&&header.startsWith("Bearer ")&&SecurityContextHolder.getContext().getAuthentication()==null){
   tokens.verify(header.substring(7)).flatMap(user->users.byId(user.id()).filter(db->db.username().equals(user.username())&&db.role().equals(user.role())).map(db->user)).ifPresent(user->{
    var auth=new UsernamePasswordAuthenticationToken(user,null,List.of(new SimpleGrantedAuthority("ROLE_"+user.role())));
    SecurityContextHolder.getContext().setAuthentication(auth);
   });
  }
  chain.doFilter(req,res);
 }
}
