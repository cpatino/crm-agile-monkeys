package com.theagilemonkeys.crm.adapter.outbound.persistence.repository;

import com.theagilemonkeys.crm.adapter.outbound.persistence.entity.CustomerDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.stream.Stream;

public interface CustomerRepository extends MongoRepository<CustomerDocument, String> {
  
  Stream<CustomerDocument> findByDeletedFalse();
}
