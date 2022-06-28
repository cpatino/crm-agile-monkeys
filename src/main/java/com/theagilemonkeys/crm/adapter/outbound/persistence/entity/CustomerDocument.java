package com.theagilemonkeys.crm.adapter.outbound.persistence.entity;

import lombok.Builder;
import lombok.Getter;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "customer")
@Builder
@Getter
public class CustomerDocument {
  
  @Id
  private String id;
  private String name;
  private String surname;
  private Binary photo;
  @Indexed
  private String createdBy;
  private Instant created;
  private boolean deleted;
}
