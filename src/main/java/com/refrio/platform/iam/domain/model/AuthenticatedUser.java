package com.refrio.platform.iam.domain.model;
public record AuthenticatedUser(Long id,String username,String role) {
 public boolean supervisor(){return "Supervisor".equals(role);}
}
