package com.theagilemonkeys.crm.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class Customer {
  
  private String id;
  private String name;
  private String surname;
  private byte[] photo;
  private String createdBy;
  private String lastUpdatedBy;
  
  public static Customer.CustomerBuilder cloneAsBuilder(Customer toBeCopiedCustomer) {
    return Customer.builder()
      .id(toBeCopiedCustomer.getId())
      .name(toBeCopiedCustomer.getName())
      .surname(toBeCopiedCustomer.getSurname())
      .photo(toBeCopiedCustomer.getPhoto())
      .createdBy(toBeCopiedCustomer.getCreatedBy());
  }
}
