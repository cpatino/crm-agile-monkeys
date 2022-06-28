package com.theagilemonkeys.crm.adapter.inbound.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CustomerDto {
  
  private String id;
  private String name;
  private String surname;
  private String photo;
  private String createdBy;
}
