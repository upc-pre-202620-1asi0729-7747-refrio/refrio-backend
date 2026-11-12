package com.refrio.platform.shared.infrastructure.persistence;
import jakarta.persistence.*;
@Entity @Table(name="operation_mutex")
public class OperationMutex {
 @Id private Integer id;
 protected OperationMutex(){}
}
