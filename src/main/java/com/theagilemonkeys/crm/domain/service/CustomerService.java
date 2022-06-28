package com.theagilemonkeys.crm.domain.service;

import com.theagilemonkeys.crm.domain.exception.ObjectAlreadyExistsException;
import com.theagilemonkeys.crm.domain.exception.ObjectNotFoundException;
import com.theagilemonkeys.crm.domain.gateway.CustomerPersistenceGateway;
import com.theagilemonkeys.crm.domain.model.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
class CustomerService implements CrudService<Customer> {
  
  private final CustomerPersistenceGateway persistenceGateway;
  
  @Override
  public Customer create(Customer newDomain) {
    checkCustomerAlreadyCreated(newDomain.getId());
    return persistenceGateway.create(newDomain);
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
}
