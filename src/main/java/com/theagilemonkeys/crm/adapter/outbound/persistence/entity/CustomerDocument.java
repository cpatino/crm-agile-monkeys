package com.theagilemonkeys.crm.adapter.outbound.persistence.entity;

import lombok.Builder;
import lombok.Getter;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

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
  private boolean deleted;
  @Indexed
  private String lastUpdatedBy;
  
  public static CustomerDocumentBuilder copyAsBuilder(CustomerDocument customerDocument) {
    return CustomerDocument.builder()
      .id(customerDocument.getId())
      .name(customerDocument.getName())
      .surname(customerDocument.getSurname())
      .photo(customerDocument.getPhoto())
      .createdBy(customerDocument.getCreatedBy())
      .deleted(customerDocument.isDeleted())
      .lastUpdatedBy(customerDocument.getLastUpdatedBy());
  }
}
