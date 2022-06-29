package com.theagilemonkeys.crm.domain.service;

import com.theagilemonkeys.crm.domain.exception.ObjectAlreadyExistsException;
import com.theagilemonkeys.crm.domain.exception.ObjectNotFoundException;
import com.theagilemonkeys.crm.domain.gateway.CustomerPersistenceGateway;
import com.theagilemonkeys.crm.domain.model.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
class CustomerService implements CrudService<Customer> {
  
  private final CustomerPersistenceGateway persistenceGateway;
  
  @Override
  public Customer create(Customer newCustomer) {
    checkCustomerAlreadyCreated(newCustomer.getId());
    return persistenceGateway.create(newCustomer);
  }
  
  @Override
  public Customer findById(String id) {
    return persistenceGateway.findById(id);
  }
  
  @Override
  public Stream<Customer> findAll() {
    return persistenceGateway.findAll();
  }
  
  private void checkCustomerAlreadyCreated(String id) {
    try {
      findById(id);
      throw new ObjectAlreadyExistsException(id);
    } catch (ObjectNotFoundException ex) {
      log.info(ex.getMessage() + ". Customer can be created!");
    }
  }
  
  @Override
  @Transactional
  public Customer update(Customer existingCustomer) {
    var persistedCustomer = findById(existingCustomer.getId());
    var toBeUpdatedCustomerBuilder = Customer.cloneAsBuilder(persistedCustomer);
    
    if (Objects.nonNull(existingCustomer.getName())) {
      toBeUpdatedCustomerBuilder.name(existingCustomer.getName());
    }
    if (Objects.nonNull(existingCustomer.getSurname())) {
      toBeUpdatedCustomerBuilder.surname(existingCustomer.getSurname());
    }
    if (Objects.nonNull(existingCustomer.getPhoto())) {
      toBeUpdatedCustomerBuilder.photo(existingCustomer.getPhoto());
    }
    toBeUpdatedCustomerBuilder.lastUpdatedBy(existingCustomer.getLastUpdatedBy());
    
    return persistenceGateway.update(toBeUpdatedCustomerBuilder.build());
  }
  
  @Override
  @Transactional
  public Customer delete(Customer existingCustomer) {
    var toBeDeletedCustomer = Customer.cloneAsBuilder(findById(existingCustomer.getId()))
      .lastUpdatedBy(existingCustomer.getLastUpdatedBy())
      .build();
    return persistenceGateway.delete(toBeDeletedCustomer);
  }
}
