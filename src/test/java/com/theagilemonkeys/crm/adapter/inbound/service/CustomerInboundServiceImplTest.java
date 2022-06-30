package com.theagilemonkeys.crm.adapter.inbound.service;

import com.theagilemonkeys.crm.adapter.inbound.dto.CustomerDto;
import com.theagilemonkeys.crm.domain.model.Customer;
import com.theagilemonkeys.crm.domain.service.CrudService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.HttpClientErrorException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = CustomerInboundServiceImpl.class)
class CustomerInboundServiceImplTest {
  
  @Autowired
  private CustomerInboundService service;
  @MockBean
  private CrudService<Customer> customerCrudService;
  
  @Test
  void whenCreate_thenCallCrudService() {
    var customer = Customer.builder()
      .id("id")
      .name("name")
      .surname("surname")
      .photo("an image".getBytes())
      .createdBy("createdBy")
      .lastUpdatedBy("last")
      .build();
    
    var customerDto = CustomerDto.builder()
      .id("id")
      .name("name")
      .surname("surname")
      .createdBy("createdBy")
      .build();
    when(customerCrudService.create(any())).thenReturn(customer);
    service.create(customerDto, "an image".getBytes());
    verify(customerCrudService).create(any());
  }
  
  @Test
  void whenFindAll_thenCallCrudService() {
    service.findAll();
    verify(customerCrudService).findAll();
  }
  
  @Test
  void whenFindBy_thenCallCrudService() {
    var customer = Customer.builder()
      .id("id")
      .name("name")
      .surname("surname")
      .photo("an image".getBytes())
      .createdBy("createdBy")
      .build();
    when(customerCrudService.findById("id")).thenReturn(customer);
    var customerDto = service.findBy("id");
    assertThat(customerDto).isNotNull();
  }
  
  @Test
  void givenEmptyCustomerDtoAndPhoto_whenUpdate_thenThrowHttpClientErrorException() {
    var customerDto = CustomerDto.builder().id("id").lastUpdatedBy("last").build();
    assertThrows(HttpClientErrorException.class, () -> service.update(customerDto, null));
  }
  
  @Test
  void givenEmptyCustomerDto_whenUpdate_throwHttpClientErrorException() {
    var customer = Customer.builder()
      .id("id")
      .name("name")
      .surname("surname")
      .photo("an image".getBytes())
      .createdBy("createdBy")
      .lastUpdatedBy("last")
      .build();
    when(customerCrudService.update(any())).thenReturn(customer);
    var customerDto = service.update(CustomerDto.builder().id("id").lastUpdatedBy("last").build(), "an image".getBytes());
    assertThat(customerDto).isNotNull();
  }
  
  @Test
  void givenACustomerId_whenDelete_thenCallCrudService() {
    service.delete("id", "lastUpdatedBy");
    verify(customerCrudService).delete(any());
  }
}
