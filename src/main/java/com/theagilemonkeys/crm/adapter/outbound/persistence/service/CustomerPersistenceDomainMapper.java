package com.theagilemonkeys.crm.adapter.outbound.persistence.service;

import com.theagilemonkeys.crm.adapter.outbound.persistence.entity.CustomerDocument;
import com.theagilemonkeys.crm.adapter.service.DomainMapper;
import com.theagilemonkeys.crm.domain.model.Customer;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.stereotype.Service;

@Service
class CustomerPersistenceDomainMapper implements DomainMapper<Customer, CustomerDocument> {
  
  @Override
  public Customer toDomain(CustomerDocument toBeMapped) {
    return Customer.builder()
      .id(toBeMapped.getId())
      .name(toBeMapped.getName())
      .surname(toBeMapped.getSurname())
      .photo(toBeMapped.getPhoto().getData())
      .createdBy(toBeMapped.getCreatedBy())
      .build();
  }
  
  @Override
  public CustomerDocument fromDomain(Customer toBeMapped) {
    return CustomerDocument.builder()
      .id(toBeMapped.getId())
      .name(toBeMapped.getName())
      .surname(toBeMapped.getSurname())
      .photo(new Binary(BsonBinarySubType.BINARY, toBeMapped.getPhoto()))
      .createdBy(toBeMapped.getCreatedBy())
      .build();
  }
}
