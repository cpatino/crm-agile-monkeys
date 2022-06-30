package com.theagilemonkeys.crm.adapter.outbound.authentication.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class KeycloakClientConfig {
  
  @Bean
  Keycloak keycloak(@Value("${keycloak.auth-server-url}") String authServerUrl,
                    @Value("${keycloak.realm}") String realm,
                    @Value("${keycloak.resource}") String clientId,
                    @Value("${admin.username}") String username,
                    @Value("${admin.password}") String password) {
    return KeycloakBuilder.builder()
      .grantType(CredentialRepresentation.PASSWORD)
      .serverUrl(authServerUrl)
      .realm(realm)
      .clientId(clientId)
      .username(username)
      .password(password)
      .build();
  }
}
