package com.theagilemonkeys.crm.adapter.outbound.persistence.service;

import com.theagilemonkeys.crm.adapter.outbound.persistence.entity.CustomerDocument;
import com.theagilemonkeys.crm.adapter.outbound.persistence.repository.CustomerRepository;
import com.theagilemonkeys.crm.adapter.service.DomainMapper;
import com.theagilemonkeys.crm.domain.exception.ObjectNotFoundException;
import com.theagilemonkeys.crm.domain.gateway.CustomerPersistenceGateway;
import com.theagilemonkeys.crm.domain.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
class CustomerPersistenceService implements CustomerPersistenceGateway {
  
  private final CustomerRepository repository;
  private final DomainMapper<Customer, CustomerDocument> domainMapper;
  
  @Override
  public Customer create(Customer newCustomer) {
    return save(newCustomer);
  }
  
  @Override
  public Customer findById(String id) {
    return Optional.ofNullable(id)
      .flatMap(repository::findByIdAndDeletedFalse)
      .map(domainMapper::toDomain)
      .orElseThrow(() -> new ObjectNotFoundException(id));
  }
  
  @Override
  public Stream<Customer> findAll() {
    return repository.findByDeletedFalse()
      .map(domainMapper::toDomain)
      .toList()
      .stream();
  }
  
  @Override
  public Customer update(Customer existingDomain) {
    return save(existingDomain);
  }
  
  @Override
  public Customer delete(Customer existingDomain) {
    return Optional.ofNullable(existingDomain)
      .map(domainMapper::fromDomain)
      .map(CustomerDocument::copyAsBuilder)
      .map(builder -> builder.deleted(true).build())
      .map(repository::save)
      .map(domainMapper::toDomain)
      .orElseThrow(() -> new RuntimeException("A problem was found when trying to delete the customer."));
  }
  
  private Customer save(Customer customer) {
    return Optional.ofNullable(customer)
      .map(domainMapper::fromDomain)
      .map(repository::save)
      .map(domainMapper::toDomain)
      .orElseThrow(() -> new RuntimeException("The customer was not saved correctly."));
  }
}
