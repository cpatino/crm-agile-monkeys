package com.theagilemonkeys.crm.adapter.inbound.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerControllerTest {
  
  private static final String URL_TEMPLATE = "/customers";
  
  @Autowired
  private WebApplicationContext context;
  
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
    mockMvc.perform(get(URL_TEMPLATE).with(user("XXX").roles("XXX")))
      .andExpect(status().isForbidden());
  }
  
  @Test
  void givenUserWithUserRole_whenFindAll_then200() throws Exception {
    mockMvc.perform(get(URL_TEMPLATE).with(user("XXX").roles("user")))
      .andExpect(status().isOk());
  }
}
