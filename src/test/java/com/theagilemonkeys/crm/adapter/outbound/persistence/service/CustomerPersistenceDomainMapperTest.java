package com.theagilemonkeys.crm.adapter.outbound.persistence.service;

import com.theagilemonkeys.crm.adapter.outbound.persistence.entity.CustomerDocument;
import com.theagilemonkeys.crm.adapter.service.DomainMapper;
import com.theagilemonkeys.crm.domain.model.Customer;
import org.bson.types.Binary;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = CustomerPersistenceDomainMapper.class)
class CustomerPersistenceDomainMapperTest {
  
  @Autowired
  private DomainMapper<Customer, CustomerDocument> domainMapper;
  
  @Test
  void givenACustomerDocument_whenToDomain_thenMap() {
    var customerDocument = CustomerDocument.builder()
      .id("123")
      .name("name")
      .surname("surname")
      .photo(new Binary("This is the binary".getBytes()))
      .createdBy("createdBy")
      .build();
  
    var customer = domainMapper.toDomain(customerDocument);
    assertThat(customer.getId()).isEqualTo(customerDocument.getId());
    assertThat(customer.getName()).isEqualTo(customerDocument.getName());
    assertThat(customer.getSurname()).isEqualTo(customerDocument.getSurname());
    assertThat(customer.getPhoto()).isEqualTo(customerDocument.getPhoto().getData());
    assertThat(customer.getCreatedBy()).isEqualTo(customerDocument.getCreatedBy());
  }
  
  @Test
  void givenACustomer_whenFromDomain_thenMap() {
    var customer = Customer.builder()
      .id("123")
      .name("name")
      .surname("surname")
      .photo("This is the binary".getBytes())
      .createdBy("createdBy")
      .build();
  
    var customerDocument = domainMapper.fromDomain(customer);
    assertThat(customerDocument.getId()).isEqualTo(customer.getId());
    assertThat(customerDocument.getName()).isEqualTo(customer.getName());
    assertThat(customerDocument.getSurname()).isEqualTo(customer.getSurname());
    assertThat(customerDocument.getPhoto().getData()).isEqualTo(customer.getPhoto());
    assertThat(customerDocument.getCreatedBy()).isEqualTo(customer.getCreatedBy());
  }
}
