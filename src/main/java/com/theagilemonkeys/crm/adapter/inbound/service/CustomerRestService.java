package com.theagilemonkeys.crm.adapter.inbound.service;

import com.theagilemonkeys.crm.adapter.inbound.dto.CustomerDto;
import com.theagilemonkeys.crm.adapter.inbound.dto.CustomerListDto;
import com.theagilemonkeys.crm.domain.model.Customer;
import com.theagilemonkeys.crm.domain.service.CrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class CustomerRestService {
  
  private final CrudService<Customer> customerCrudService;
  
  public void create(CustomerDto customerDto, byte[] photo) {
    var customer = Customer.builder()
      .id(customerDto.getId())
      .name(customerDto.getName())
      .surname(customerDto.getSurname())
      .photo(photo)
      .createdBy(customerDto.getCreatedBy())
      .build();
    customerCrudService.create(customer);
  }
  
  public CustomerListDto findAll() {
    var customers = customerCrudService.findAll()
      .map(this::fromDomain)
      .toList();
    return CustomerListDto.builder()
      .customers(customers)
      .build();
  }
  
  public CustomerDto findBy(String id) {
    return fromDomain(customerCrudService.findById(id));
  }
  
  private CustomerDto fromDomain(Customer customer) {
    return CustomerDto.builder()
      .id(customer.getId())
      .name(customer.getName())
      .surname(customer.getSurname())
      .photo(Base64.getEncoder().encodeToString(customer.getPhoto()))
      .createdBy(customer.getCreatedBy())
      .build();
  }
}
