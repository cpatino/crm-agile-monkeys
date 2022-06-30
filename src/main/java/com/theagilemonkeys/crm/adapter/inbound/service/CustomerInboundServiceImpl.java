package com.theagilemonkeys.crm.adapter.inbound.service;

import com.theagilemonkeys.crm.adapter.inbound.dto.CustomerDto;
import com.theagilemonkeys.crm.adapter.inbound.dto.CustomerListDto;
import com.theagilemonkeys.crm.domain.model.Customer;
import com.theagilemonkeys.crm.domain.service.CrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Base64;
import java.util.Objects;

@Service
@RequiredArgsConstructor
class CustomerInboundServiceImpl implements CustomerInboundService {
  
  private final CrudService<Customer> customerCrudService;
  
  @Override
  public CustomerDto create(CustomerDto customerDto, byte[] photo) {
    var customer = Customer.builder()
      .id(customerDto.getId())
      .name(customerDto.getName())
      .surname(customerDto.getSurname())
      .photo(photo)
      .createdBy(customerDto.getCreatedBy())
      .build();
    return fromDomain(customerCrudService.create(customer));
  }
  
  @Override
  public CustomerListDto findAll() {
    var customers = customerCrudService.findAll()
      .map(this::fromDomain)
      .toList();
    return CustomerListDto.builder()
      .customers(customers)
      .build();
  }
  
  @Override
  public CustomerDto findBy(String id) {
    return fromDomain(customerCrudService.findById(id));
  }
  
  @Override
  public CustomerDto update(CustomerDto customerDto, byte[] photo) {
    if (!hasUpdatedInformation(customerDto, photo)) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "No data was provided to perform an update");
    }
    var customer = Customer.builder()
      .id(customerDto.getId())
      .name(customerDto.getName())
      .surname(customerDto.getSurname())
      .photo(photo)
      .lastUpdatedBy(customerDto.getLastUpdatedBy())
      .build();
    return fromDomain(customerCrudService.update(customer));
  }
  
  @Override
  public void delete(String id, String lastUpdatedBy) {
    var customer = Customer.builder()
      .id(id)
      .lastUpdatedBy(lastUpdatedBy)
      .build();
    customerCrudService.delete(customer);
  }
  
  private boolean hasUpdatedInformation(CustomerDto customerDto, byte[] photo) {
    return Objects.nonNull(customerDto.getName()) || Objects.nonNull(customerDto.getSurname()) || Objects.nonNull(photo);
  }
  
  private CustomerDto fromDomain(Customer customer) {
    return CustomerDto.builder()
      .id(customer.getId())
      .name(customer.getName())
      .surname(customer.getSurname())
      .photo(Base64.getEncoder().encodeToString(customer.getPhoto()))
      .createdBy(customer.getCreatedBy())
      .lastUpdatedBy(customer.getLastUpdatedBy())
      .build();
  }
}
