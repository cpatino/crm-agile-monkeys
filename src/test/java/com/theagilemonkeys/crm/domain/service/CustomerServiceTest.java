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
  
  @Test
  void whenFindById_thenCallFindByIdFromGateway() {
    crudService.findById("123");
    verify(persistenceGateway).findById("123");
  }
  
  @Test
  void whenFindAll_thenCallFindAll() {
    crudService.findAll();
    verify(persistenceGateway).findAll();
  }
  
  @Test
  void givenANewCustomer_whenUpdate_thenThrowObjectNotFoundException() {
    var customer = buildCustomer();
    when(persistenceGateway.findById("123")).thenThrow(new ObjectNotFoundException("123"));
    assertThrows(ObjectNotFoundException.class, () -> crudService.update(customer));
  }
  
  @Test
  void givenAnExistingCustomer_whenUpdate_thenUpdateCustomer() {
    var updatedPhoto = "Updated binary".getBytes();
    var toBeUpdatedCustomer = buildCustomer("updated name", "updated surname", updatedPhoto, "lastUpdatedBy");
    var persistedCustomer = buildCustomer();
    when(persistenceGateway.findById("123")).thenReturn(persistedCustomer);
    crudService.update(toBeUpdatedCustomer);
    verify(persistenceGateway).update(toBeUpdatedCustomer);
  }
  
  @Test
  void givenANotExistingCustomer_whenDelete_thenThrowObjectNotFoundException() {
    var customer = buildCustomer();
    when(persistenceGateway.findById("123")).thenThrow(new ObjectNotFoundException("123"));
    assertThrows(ObjectNotFoundException.class, () -> crudService.delete(customer));
  }
  
  @Test
  void givenAnExistingCustomer_whenDelete_thenCallDelete() {
    var toBeDeletedCustomer = buildCustomer(null, null, null, "lastUpdatedBy");
    var persistedCustomer = buildCustomer();
    when(persistenceGateway.findById("123")).thenReturn(persistedCustomer);
    crudService.delete(toBeDeletedCustomer);
    verify(persistenceGateway).delete(any());
  }
  
  private Customer buildCustomer() {
    return buildCustomer("name", "surname", "This is the binary".getBytes(), null);
  }
  
  private Customer buildCustomer(String name, String surname, byte[] photo, String lastUpdatedBy) {
    return Customer.builder()
      .id("123")
      .name(name)
      .surname(surname)
      .photo(photo)
      .createdBy("createdBy")
      .lastUpdatedBy(lastUpdatedBy)
      .build();
  }
}