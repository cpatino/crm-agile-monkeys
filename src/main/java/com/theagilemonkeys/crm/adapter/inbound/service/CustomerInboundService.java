package com.theagilemonkeys.crm.adapter.inbound.service;

import com.theagilemonkeys.crm.adapter.inbound.dto.CustomerDto;
import com.theagilemonkeys.crm.adapter.inbound.dto.CustomerListDto;

public interface CustomerInboundService {
  
  void create(CustomerDto customerDto, byte[] photo);
  
  CustomerListDto findAll();
  
  CustomerDto findBy(String id);
  
  CustomerDto update(CustomerDto customerDto, byte[] photo);
  
  void delete(String id, String lastUpdatedBy);
}
