package com.theagilemonkeys.crm.adapter.outbound.authentication.keycloak;

import com.theagilemonkeys.crm.adapter.outbound.authentication.AuthenticationAdminClient;
import com.theagilemonkeys.crm.adapter.outbound.authentication.dto.UserDto;
import com.theagilemonkeys.crm.adapter.outbound.authentication.dto.UserRequestDto;
import com.theagilemonkeys.crm.domain.exception.ObjectAlreadyExistsException;
import com.theagilemonkeys.crm.domain.exception.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;

import javax.ws.rs.NotFoundException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class KeycloakClient implements AuthenticationAdminClient {
  
  private static final String REALM = "crm";
  private static final String ROLE_ADMIN = "admin";
  private static final String ROLE_USER = "user";
  
  private final Keycloak keycloak;
  
  @Override
  public List<UserDto> findAllUsers() {
    return keycloak.realm(REALM)
      .users()
      .list()
      .stream().filter(UserRepresentation::isEnabled)
      .map(this::toUserDto)
      .toList();
  }
  
  @Override
  public UserDto findUserBy(String id) {
    return toUserDto(findUserRepresentationById(id));
  }
  
  @Override
  public UserDto createUser(UserRequestDto newUser) {
    if (hasUsernameNotBeingUsed(newUser)) {
      keycloak.realm(REALM)
        .users()
        .create(buildUserRepresentation(newUser));
      
      var userRepresentation = findByUsername(newUser.getUsername()).get(0);
      addRoleToUser(userRepresentation.getId(), List.of(findRole(ROLE_USER)));
      return toUserDto(userRepresentation);
    } else {
      var userRepresentation = findByUsername(newUser.getUsername()).get(0);
      if (userRepresentation.isEnabled()) {
        throw new ObjectAlreadyExistsException(newUser.getUsername());
      } else {
        updateUser(userRepresentation.getId(), newUser, userRepresentation);
        return toUserDto(userRepresentation);
      }
    }
  }
  
  @Override
  public UserDto updateUser(String id, UserRequestDto existingUser) {
    var toBeUpdatedUserRepresentation = findUserRepresentationById(id);
    return updateUser(id, existingUser, toBeUpdatedUserRepresentation);
  }
  
  private UserDto updateUser(String id, UserRequestDto existingUser, UserRepresentation toBeUpdatedUserRepresentation) {
    if (hasUsernameNotBeingUsed(id, existingUser)) {
      
      toBeUpdatedUserRepresentation.setUsername(existingUser.getUsername());
      toBeUpdatedUserRepresentation.setCredentials(List.of(buildCredentialRepresentation(existingUser.getPassword())));
      toBeUpdatedUserRepresentation.setEnabled(true);
      keycloak.realm(REALM)
        .users()
        .get(id)
        .update(toBeUpdatedUserRepresentation);
      
      return toUserDto(toBeUpdatedUserRepresentation);
    } else {
      throw new ObjectAlreadyExistsException(existingUser.getUsername());
    }
  }
  
  @Override
  public void deleteUser(String id) {
    var toBeDeletedUserRepresentation = findUserRepresentationById(id);
    toBeDeletedUserRepresentation.setEnabled(false);
    keycloak.realm(REALM)
      .users()
      .get(id)
      .update(toBeDeletedUserRepresentation);
  }
  
  @Override
  public UserDto addAdministratorStatus(String id) {
    var userRepresentationById = findUserRepresentationById(id);
    addRoleToUser(id, List.of(findRole(ROLE_ADMIN)));
    return toUserDto(userRepresentationById);
  }
  
  @Override
  public UserDto removeAdministratorStatus(String id) {
    var userRepresentationById = findUserRepresentationById(id);
    keycloak.realm(REALM)
      .users()
      .get(id)
      .roles()
      .realmLevel()
      .remove(List.of(findRole(ROLE_ADMIN)));
    return toUserDto(userRepresentationById);
  }
  
  private UserRepresentation findUserRepresentationById(String id) {
    try {
      var userRepresentation = keycloak.realm(REALM)
        .users()
        .get(id)
        .toRepresentation();
      
      if (!userRepresentation.isEnabled()) {
        throw new ObjectNotFoundException(id);
      }
      
      return userRepresentation;
    } catch (NotFoundException ex) {
      throw new ObjectNotFoundException(id);
    }
  }
  
  private boolean hasUsernameNotBeingUsed(UserRequestDto newUser) {
    return findByUsername(newUser.getUsername()).isEmpty();
  }
  
  private boolean hasUsernameNotBeingUsed(String id, UserRequestDto newUser) {
    var users = findByUsername(newUser.getUsername());
    return users.isEmpty() || users.get(0).getId().equals(id);
  }
  
  private void addRoleToUser(String id, List<RoleRepresentation> roleRepresentations) {
    keycloak.realm(REALM)
      .users()
      .get(id)
      .roles()
      .realmLevel()
      .add(roleRepresentations);
  }
  
  private RoleRepresentation findRole(String role) {
    return keycloak.realm(REALM)
      .roles()
      .get(role)
      .toRepresentation();
  }
  
  private UserDto toUserDto(UserRepresentation userRepresentation) {
    return UserDto.builder()
      .id(userRepresentation.getId())
      .username(userRepresentation.getUsername())
      .build();
  }
  
  private List<UserRepresentation> findByUsername(String username) {
    return keycloak.realm(REALM)
      .users()
      .search(username);
  }
  
  private CredentialRepresentation buildCredentialRepresentation(String password) {
    var representation = new CredentialRepresentation();
    representation.setTemporary(false);
    representation.setType(CredentialRepresentation.PASSWORD);
    representation.setValue(password);
    return representation;
  }
  
  private UserRepresentation buildUserRepresentation(UserRequestDto requestDto) {
    var userRepresentation = new UserRepresentation();
    userRepresentation.setUsername(requestDto.getUsername());
    userRepresentation.setCredentials(List.of(buildCredentialRepresentation(requestDto.getPassword())));
    userRepresentation.setEnabled(true);
    return userRepresentation;
  }
}
