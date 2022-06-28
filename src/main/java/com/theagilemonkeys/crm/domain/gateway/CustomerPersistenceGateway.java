package com.theagilemonkeys.crm.domain.gateway;

import com.theagilemonkeys.crm.domain.model.Customer;
import com.theagilemonkeys.crm.domain.service.Creator;
import com.theagilemonkeys.crm.domain.service.Finder;

public interface CustomerPersistenceGateway extends Creator<Customer>, Finder<Customer> {
}
