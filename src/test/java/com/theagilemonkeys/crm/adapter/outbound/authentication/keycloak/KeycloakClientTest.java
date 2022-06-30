package com.theagilemonkeys.crm.adapter.outbound.authentication.keycloak;

import com.theagilemonkeys.crm.adapter.outbound.authentication.AuthenticationAdminClient;
import com.theagilemonkeys.crm.adapter.outbound.authentication.dto.UserRequestDto;
import com.theagilemonkeys.crm.domain.exception.ObjectAlreadyExistsException;
import com.theagilemonkeys.crm.domain.exception.ObjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.ws.rs.NotFoundException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = KeycloakClient.class)
class KeycloakClientTest {
  
  private static final String REALM = "crm";
  
  @MockBean
  private Keycloak keycloak;
  @Mock
  private UsersResource usersResource;
  @Autowired
  private AuthenticationAdminClient authenticationAdminClient;
  
  @BeforeEach
  void setup() {
    var realmResource = Mockito.mock(RealmResource.class);
    when(keycloak.realm(REALM)).thenReturn(realmResource);
    when(realmResource.users()).thenReturn(usersResource);
    
    var userResource1 = Mockito.mock(UserResource.class);
    when(usersResource.get("id_1")).thenReturn(userResource1);
    when(userResource1.toRepresentation()).thenReturn(buildUserRepresentation("_1", true));
    var roleMappingResource = Mockito.mock(RoleMappingResource.class);
    when(userResource1.roles()).thenReturn(roleMappingResource);
    when(roleMappingResource.realmLevel()).thenReturn(Mockito.mock(RoleScopeResource.class));
    
    var userResource2 = Mockito.mock(UserResource.class);
    when(usersResource.get("id_2")).thenReturn(userResource2);
    when(userResource2.toRepresentation()).thenReturn(buildUserRepresentation("_2", false));
    
    var rolesResource = Mockito.mock(RolesResource.class);
    when(realmResource.roles()).thenReturn(rolesResource);
    
    var roleResource1 = Mockito.mock(RoleResource.class);
    when(rolesResource.get("user")).thenReturn(roleResource1);
    when(roleResource1.toRepresentation()).thenReturn(new RoleRepresentation());
    
    var roleResource2 = Mockito.mock(RoleResource.class);
    when(rolesResource.get("admin")).thenReturn(roleResource2);
    when(roleResource2.toRepresentation()).thenReturn(new RoleRepresentation());
  }
  
  @Test
  void whenFindAllUsers_thenListAvailableUsers() {
    when(usersResource.list()).thenReturn(List.of(buildUserRepresentation("_1", true), buildUserRepresentation("_2", false)));
    var users = authenticationAdminClient.findAllUsers();
    assertThat(users).isNotNull().isNotEmpty().hasSize(1);
    var user = users.get(0);
    assertThat(user.getId()).isEqualTo("id_1");
    assertThat(user.getUsername()).isEqualTo("username_1");
  }
  
  @Test
  void givenAnExistingUser_whenFindUserBy_thenReturnUser() {
    var user = authenticationAdminClient.findUserBy("id_1");
    assertThat(user.getId()).isEqualTo("id_1");
    assertThat(user.getUsername()).isEqualTo("username_1");
  }
  
  @Test
  void givenANonExistingUser_whenFindUserBy_thenThrowObjectNotFoundException() {
    when(usersResource.get("id_3")).thenThrow(NotFoundException.class);
    assertThrows(ObjectNotFoundException.class, () -> authenticationAdminClient.findUserBy("id_3"));
  }
  
  @Test
  void givenAnExistingUserNotEnabled_whenFindUserBy_thenThrowObjectNotFoundException() {
    assertThrows(ObjectNotFoundException.class, () -> authenticationAdminClient.findUserBy("id_2"));
  }
  
  @Test
  void givenANewUser_whenCreateUser_thenNoExceptions() {
    when(usersResource.search("username_3")).thenReturn(List.of(), List.of(buildUserRepresentation("_3", true)));
    var userResource = Mockito.mock(UserResource.class);
    when(usersResource.get("id_3")).thenReturn(userResource);
    var roleMappingResource = Mockito.mock(RoleMappingResource.class);
    when(userResource.roles()).thenReturn(roleMappingResource);
    when(roleMappingResource.realmLevel()).thenReturn(Mockito.mock(RoleScopeResource.class));
    
    var newUser = UserRequestDto.builder()
      .username("username_3")
      .password("password")
      .build();
    try {
      authenticationAdminClient.createUser(newUser);
    } catch (Exception ex) {
      fail("The creation process must not fail");
    }
  }
  
  @Test
  void givenAnExistingUser_whenCreateUser_thenThrowObjectAlreadyExistsException() {
    when(usersResource.search("username_3")).thenReturn(List.of(buildUserRepresentation("_3", true)));
    
    var newUser = UserRequestDto.builder()
      .username("username_3")
      .password("password")
      .build();
    
    assertThrows(ObjectAlreadyExistsException.class, () -> authenticationAdminClient.createUser(newUser));
  }
  
  private UserRepresentation buildUserRepresentation(String prefix, boolean enabled) {
    var userRepresentation = new UserRepresentation();
    userRepresentation.setId("id" + prefix);
    userRepresentation.setUsername("username" + prefix);
    userRepresentation.setEnabled(enabled);
    return userRepresentation;
  }
  
  @Test
  void givenANonExistingUser_whenUpdateUser_thenThrowObjectNotFoundException() {
    when(usersResource.get("id_3")).thenThrow(NotFoundException.class);
    var existingUser = UserRequestDto.builder()
      .username("username_3")
      .password("password")
      .build();
    assertThrows(ObjectNotFoundException.class, () -> authenticationAdminClient.updateUser("id_3", existingUser));
  }
  
  @Test
  void givenANotEnabledUser_whenUpdateUser_thenThrowObjectNotFoundException() {
    var existingUser = UserRequestDto.builder()
      .username("username_2")
      .password("password")
      .build();
    assertThrows(ObjectNotFoundException.class, () -> authenticationAdminClient.updateUser("id_2", existingUser));
  }
  
  @Test
  void givenAExistingUser_whenUpdateUser_thenReturnUpdatedUser() {
    when(usersResource.search("username_1_updated")).thenReturn(List.of());
    
    var existingUser = UserRequestDto.builder()
      .username("username_1_updated")
      .password("password")
      .build();
    var userDto = authenticationAdminClient.updateUser("id_1", existingUser);
    assertThat(userDto.getUsername()).isEqualTo("username_1_updated");
  }
  
  @Test
  void givenAExistingUser_whenUpdateUserWithAlreadyUsedUsername_thenThrowObjectAlreadyExistException() {
    when(usersResource.search("username_2")).thenReturn(List.of(buildUserRepresentation("_2", false)));
    var existingUser = UserRequestDto.builder()
      .username("username_2")
      .password("password")
      .build();
    assertThrows(ObjectAlreadyExistsException.class, () -> authenticationAdminClient.updateUser("id_1", existingUser));
  }
  
  @Test
  void givenANonExistingUser_whenDeleteUser_thenThrowObjectNotFoundException() {
    when(usersResource.get("id_3")).thenThrow(NotFoundException.class);
    assertThrows(ObjectNotFoundException.class, () -> authenticationAdminClient.deleteUser("id_3"));
  }
  
  @Test
  void givenANotEnabledUser_whenDeleteUser_thenThrowObjectNotFoundException() {
    assertThrows(ObjectNotFoundException.class, () -> authenticationAdminClient.deleteUser("id_2"));
  }
  
  @Test
  void givenAExistingUser_whenDeleteUser_thenDoNotThrowExceptions() {
    try {
      authenticationAdminClient.deleteUser("id_1");
    } catch (Exception ex) {
      fail("Not expecting any exception when deleting users");
    }
  }
  
  @Test
  void givenANonExistingUser_whenAddAdministratorStatus_thenThrowObjectNotFoundException() {
    when(usersResource.get("id_3")).thenThrow(NotFoundException.class);
    assertThrows(ObjectNotFoundException.class, () -> authenticationAdminClient.addAdministratorStatus("id_3"));
  }
  
  @Test
  void givenANotEnabledUser_whenAddAdministratorStatus_thenThrowObjectNotFoundException() {
    assertThrows(ObjectNotFoundException.class, () -> authenticationAdminClient.addAdministratorStatus("id_2"));
  }
  
  @Test
  void givenAnExistingUser_whenAddAdministratorStatus_thenReturnUser() {
    var userDto = authenticationAdminClient.addAdministratorStatus("id_1");
    assertThat(userDto).isNotNull();
  }
  
  @Test
  void givenANonExistingUser_whenRemoveAdministratorStatus_thenThrowObjectNotFoundException() {
    when(usersResource.get("id_3")).thenThrow(NotFoundException.class);
    assertThrows(ObjectNotFoundException.class, () -> authenticationAdminClient.removeAdministratorStatus("id_3"));
  }
  
  @Test
  void givenANotEnabledUser_whenRemoveAdministratorStatus_thenThrowObjectNotFoundException() {
    assertThrows(ObjectNotFoundException.class, () -> authenticationAdminClient.removeAdministratorStatus("id_2"));
  }
  
  @Test
  void givenAnExistingUser_whenRevokeAdministratorStatus_thenReturnUser() {
    var userDto = authenticationAdminClient.removeAdministratorStatus("id_1");
    assertThat(userDto).isNotNull();
  }
}
