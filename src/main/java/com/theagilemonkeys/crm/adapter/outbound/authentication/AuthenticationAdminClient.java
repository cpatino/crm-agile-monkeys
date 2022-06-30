package com.theagilemonkeys.crm.adapter.outbound.authentication;

import com.theagilemonkeys.crm.adapter.outbound.authentication.dto.UserDto;
import com.theagilemonkeys.crm.adapter.outbound.authentication.dto.UserRequestDto;

import java.util.List;

public interface AuthenticationAdminClient {
  
  List<UserDto> findAllUsers();
  
  UserDto findUserBy(String id);
  
  UserDto createUser(UserRequestDto newUser);
  
  UserDto updateUser(String id, UserRequestDto existingUser);
  
  void deleteUser(String id);
  
  UserDto addAdministratorStatus(String id);
  
  UserDto removeAdministratorStatus(String id);
}
