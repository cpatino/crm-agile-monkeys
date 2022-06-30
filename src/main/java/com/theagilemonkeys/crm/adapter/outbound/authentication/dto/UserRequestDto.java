package com.theagilemonkeys.crm.adapter.outbound.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserRequestDto {
  
  private String username;
  private String password;
}
