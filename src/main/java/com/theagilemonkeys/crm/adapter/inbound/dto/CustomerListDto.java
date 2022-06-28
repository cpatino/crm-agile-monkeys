package com.theagilemonkeys.crm.adapter.inbound.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CustomerListDto {
  
  private List<CustomerDto> customers;
}
