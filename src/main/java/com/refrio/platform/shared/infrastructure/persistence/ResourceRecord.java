package com.refrio.platform.shared.infrastructure.persistence;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="resource_records",uniqueConstraints=@UniqueConstraint(columnNames={"resource","external_id"}))
public class ResourceRecord {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(nullable=false,length=80) private String resource;
  @Column(name="external_id",nullable=false,length=150) private String externalId;
  @Column(nullable=false,columnDefinition="TEXT") private String payload;
  @Column(name="created_at",nullable=false) private Instant createdAt=Instant.now();
  @Version private long version;
  protected ResourceRecord() {}
  public ResourceRecord(String resource,String externalId,String payload){this.resource=resource;this.externalId=externalId;this.payload=payload;}
  public String getResource(){return resource;}
  public String getExternalId(){return externalId;}
  public String getPayload(){return payload;}
  public void setPayload(String value){payload=value;}
}
