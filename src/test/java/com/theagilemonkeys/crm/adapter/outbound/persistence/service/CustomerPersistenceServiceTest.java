package com.theagilemonkeys.crm.adapter.outbound.persistence.service;

import com.theagilemonkeys.crm.adapter.outbound.persistence.entity.CustomerDocument;
import com.theagilemonkeys.crm.adapter.outbound.persistence.repository.CustomerRepository;
import com.theagilemonkeys.crm.adapter.service.DomainMapper;
import com.theagilemonkeys.crm.domain.exception.ObjectNotFoundException;
import com.theagilemonkeys.crm.domain.gateway.CustomerPersistenceGateway;
import com.theagilemonkeys.crm.domain.model.Customer;
import org.bson.types.Binary;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Instant;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {CustomerPersistenceService.class})
class CustomerPersistenceServiceTest {
  
  @Autowired
  private CustomerPersistenceGateway customerPersistenceGateway;
  @MockBean
  private CustomerRepository repository;
  @MockBean
  private DomainMapper<Customer, CustomerDocument> domainMapper;
  
  @Test
  void givenNullCustomer_whenCreate_thenRuntimeException() {
    assertThrows(RuntimeException.class, () -> customerPersistenceGateway.create(null));
  }
  
  @Test
  void givenACustomer_whenCreate_thenCallRepositorySave() {
    var customer = buildCustomer();
    var customerDocument = buildCustomerDocument(customer);
    
    when(domainMapper.fromDomain(customer)).thenReturn(customerDocument);
    when(repository.save(customerDocument)).thenReturn(customerDocument);
    when(domainMapper.toDomain(customerDocument)).thenReturn(customer);
    var saved = customerPersistenceGateway.create(customer);
    assertThat(saved).isEqualTo(customer);
  }
  
  @Test
  void givenNullWhenFindById_whenFindById_thenThrowObjectNotFoundException() {
    assertThrows(ObjectNotFoundException.class, () -> customerPersistenceGateway.findById("SOME_ID"));
  }
  
  @Test
  void givenEmptyWhenFindById_whenFindById_thenThrowObjectNotFoundException() {
    when(repository.findById("SOME_ID")).thenReturn(Optional.empty());
    assertThrows(ObjectNotFoundException.class, () -> customerPersistenceGateway.findById("SOME_ID"));
  }
  
  @Test
  void givenAFoundCustomer_whenFindById_thenReturnCustomer() {
    var customer = buildCustomer();
    var customerDocument = buildCustomerDocument(customer);
    
    when(repository.findById("123")).thenReturn(Optional.of(customerDocument));
    when(domainMapper.toDomain(customerDocument)).thenReturn(customer);
    var saved = customerPersistenceGateway.findById("123");
    assertThat(saved).isEqualTo(customer);
  }
  
  @Test
  void givenAnEmptyStream_whenFindAll_thenEmptyStream() {
    var savedCustomers = customerPersistenceGateway.findAll();
    assertThat(savedCustomers).isNotNull().isEmpty();
  }
  
  @Test
  void givenANonEmptyStream_whenFindAll_thenCustomerStream() {
    var customer = buildCustomer();
    var customerDocument = buildCustomerDocument(customer);
    
    when(repository.findByDeletedFalse()).thenReturn(Stream.of(customerDocument));
    when(domainMapper.toDomain(customerDocument)).thenReturn(customer);
    var savedCustomers = customerPersistenceGateway.findAll();
    assertThat(savedCustomers).isNotNull().isNotEmpty().contains(customer);
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
  
  private CustomerDocument buildCustomerDocument(Customer customer) {
    return CustomerDocument.builder()
      .id(customer.getId())
      .name(customer.getName())
      .surname(customer.getSurname())
      .photo(new Binary(customer.getPhoto()))
      .createdBy(customer.getCreatedBy())
      .created(Instant.now())
      .build();
  }
}
