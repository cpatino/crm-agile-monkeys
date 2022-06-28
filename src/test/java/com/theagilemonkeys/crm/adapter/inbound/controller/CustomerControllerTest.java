package com.theagilemonkeys.crm.adapter.inbound.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theagilemonkeys.crm.adapter.inbound.dto.CustomerDto;
import com.theagilemonkeys.crm.adapter.inbound.dto.CustomerListDto;
import com.theagilemonkeys.crm.adapter.inbound.security.ControllerSecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerControllerTest {
  
  private static final String URL_TEMPLATE = "/customers";
  
  @Autowired
  private WebApplicationContext context;
  @Autowired
  private ObjectMapper objectMapper;
  private MockMvc mockMvc;
  
  @BeforeEach
  public void setup() {
    mockMvc = MockMvcBuilders
      .webAppContextSetup(context)
      .apply(springSecurity())
      .build();
  }
  
  @Test
  void givenNoAuthentication_whenFindAll_then401Error() throws Exception {
    mockMvc.perform(get(URL_TEMPLATE))
      .andExpect(status().isUnauthorized());
  }
  
  @Test
  void givenNotEnabledRole_whenFindAll_then403Error() throws Exception {
    mockMvc.perform(get(URL_TEMPLATE)
        .with(buildJwtRequestPostProcessor("XXX")))
      .andExpect(status().isForbidden());
  }
  
  @Test
  void givenUsersWithTokens_whenCreateAndFindAll_thenCustomerIsAvailable() throws Exception {
    mockMvc.perform(multipart(HttpMethod.POST, URI.create(URL_TEMPLATE))
        .file("photo", "an image".getBytes())
        .with(buildJwtRequestPostProcessor("user"))
        .param("id", "123")
        .param("name", "name")
        .param("surname", "surname"))
      .andExpect(status().isCreated());
    
    var resultAsString = mockMvc.perform(get(URL_TEMPLATE)
        .with(buildJwtRequestPostProcessor("admin")))
      .andExpect(status().isOk())
      .andReturn()
      .getResponse()
      .getContentAsString();
    var customerListDto = objectMapper.readValue(resultAsString, CustomerListDto.class);
    
    assertThat(customerListDto).isNotNull();
    assertThat(customerListDto.getCustomers()).isNotNull().isNotEmpty();
  }
  
  @Test
  void givenUsersWithTokens_whenCreateMoreThanOneWithSameId_then400Error() throws Exception {
    mockMvc.perform(multipart(HttpMethod.POST, URI.create(URL_TEMPLATE))
        .file("photo", "an image".getBytes())
        .with(buildJwtRequestPostProcessor("user"))
        .param("id", "456")
        .param("name", "name")
        .param("surname", "surname"))
      .andExpect(status().isCreated());
    
    mockMvc.perform(multipart(HttpMethod.POST, URI.create(URL_TEMPLATE))
        .file("photo", "an image".getBytes())
        .with(buildJwtRequestPostProcessor("user"))
        .param("id", "456")
        .param("name", "name")
        .param("surname", "surname"))
      .andExpect(status().isBadRequest());
  }
  
  @Test
  void givenUsersWithTokens_whenCreateAndFindById_thenCustomerIsAvailable() throws Exception {
    mockMvc.perform(multipart(HttpMethod.POST, URI.create(URL_TEMPLATE))
        .file("photo", "an image".getBytes())
        .with(buildJwtRequestPostProcessor("user"))
        .param("id", "789")
        .param("name", "name")
        .param("surname", "surname"))
      .andExpect(status().isCreated());
    
    var resultAsString = mockMvc.perform(get(URL_TEMPLATE + "/789")
        .with(buildJwtRequestPostProcessor("admin")))
      .andExpect(status().isOk())
      .andReturn()
      .getResponse()
      .getContentAsString();
    var customerDto = objectMapper.readValue(resultAsString, CustomerDto.class);
    
    assertThat(customerDto).isNotNull();
    assertThat(customerDto.getId()).isEqualTo("789");
  }
  
  @Test
  void givenUsersWithTokens_whenFindByIdNotFound_then404Error() throws Exception {
    mockMvc.perform(get(URL_TEMPLATE + "/NOT_CREATED")
        .with(buildJwtRequestPostProcessor("user")))
      .andExpect(status().isNotFound());
  }
  
  private JwtRequestPostProcessor buildJwtRequestPostProcessor(String role) {
    var jwt = Jwt.withTokenValue("token")
      .header("alg", "none")
      .claim("preferred_username", "user")
      .claim("realm_access", Map.of("roles", List.of(role)))
      .build();
    
    return jwt().jwt(jwt).authorities(new ControllerSecurityConfig.KeycloakRealmRoleConverter());
  }
}
