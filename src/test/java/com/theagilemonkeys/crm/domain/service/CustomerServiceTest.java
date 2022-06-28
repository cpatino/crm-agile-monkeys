package com.theagilemonkeys.crm.domain.service;

import com.theagilemonkeys.crm.domain.exception.ObjectAlreadyExistsException;
import com.theagilemonkeys.crm.domain.exception.ObjectNotFoundException;
import com.theagilemonkeys.crm.domain.gateway.CustomerPersistenceGateway;
import com.theagilemonkeys.crm.domain.model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = CustomerService.class)
class CustomerServiceTest {
  
  @Autowired
  private CrudService<Customer> crudService;
  @MockBean
  private CustomerPersistenceGateway persistenceGateway;
  
  @Test
  void givenAnAlreadySavedCustomer_whenCreate_thenThrowObjectAlreadyExistsException() {
    var customer = buildCustomer();
    when(persistenceGateway.findById("123")).thenReturn(customer);
    assertThrows(ObjectAlreadyExistsException.class, () -> crudService.create(customer));
    verify(persistenceGateway, never()).create(customer);
  }
  
  @Test
  void givenANewCustomer_whenCreate_thenCreateCustomer() {
    var customer = buildCustomer();
    when(persistenceGateway.findById("123")).thenThrow(new ObjectNotFoundException("123"));
    crudService.create(customer);
    verify(persistenceGateway).create(customer);
  }
  
  private Customer buildCustomer() {
    return Customer.builder()
      .id("123")
      .name("name")
      .surname("surname")
      .photo("This is the binary".getBytes())
      .createdBy("createdBy")
      .build();
  }
}