package com.theagilemonkeys.crm.adapter.inbound.controller;

import com.theagilemonkeys.crm.adapter.outbound.authentication.AuthenticationAdminClient;
import com.theagilemonkeys.crm.adapter.outbound.authentication.dto.UserDto;
import com.theagilemonkeys.crm.adapter.outbound.authentication.dto.UserRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
  
  private final AuthenticationAdminClient authenticationAdminClient;
  
  @GetMapping
  public List<UserDto> findAll() {
    return authenticationAdminClient.findAllUsers();
  }
  
  @GetMapping("/{id}")
  public UserDto findBy(@PathVariable("id") String id) {
    return authenticationAdminClient.findUserBy(id);
  }
  
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public UserDto create(@RequestBody UserRequestDto requestDto) {
    return authenticationAdminClient.createUser(requestDto);
  }
  
  @PutMapping("/{id}")
  public UserDto update(@PathVariable("id") String id, @RequestBody UserRequestDto requestDto) {
    return authenticationAdminClient.updateUser(id, requestDto);
  }
  
  @DeleteMapping("/{id}")
  public void delete(@PathVariable("id") String id) {
    authenticationAdminClient.deleteUser(id);
  }
  
  @PutMapping("/{id}/promote")
  public Object promoteToAdministrator(@PathVariable("id") String id) {
    return authenticationAdminClient.addAdministratorStatus(id);
  }
  
  @PutMapping("/{id}/revoke")
  public Object revokeAdministrativePermission(@PathVariable("id") String id) {
    return authenticationAdminClient.removeAdministratorStatus(id);
  }
}
