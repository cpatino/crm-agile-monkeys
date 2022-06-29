package com.theagilemonkeys.crm.domain.gateway;

import com.theagilemonkeys.crm.domain.model.Customer;
import com.theagilemonkeys.crm.domain.service.Creator;
import com.theagilemonkeys.crm.domain.service.Deleter;
import com.theagilemonkeys.crm.domain.service.Finder;
import com.theagilemonkeys.crm.domain.service.Updater;

public interface CustomerPersistenceGateway extends Creator<Customer>, Finder<Customer>, Updater<Customer>, Deleter<Customer> {
}
