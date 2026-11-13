package com.refrio.platform.iam.infrastructure.persistence.jpa.entities;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="app_users")
public class UserEntity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false,unique=true) public String username;
 @Column(nullable=false,unique=true) public String email;
 @Column(name="password_hash",nullable=false) public String passwordHash;
 @Column(name="first_name",nullable=false) public String firstName;
 @Column(name="last_name",nullable=false) public String lastName;
 @Column(nullable=false) public String role;
 @Column(name="subscription_plan",nullable=false) public String subscriptionPlan;
 @Column(nullable=false) public String sede;
 @Column(name="created_at",nullable=false) public Instant createdAt=Instant.now();
}
