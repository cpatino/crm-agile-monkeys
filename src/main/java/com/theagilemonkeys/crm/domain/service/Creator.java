package com.theagilemonkeys.crm.domain.service;

public interface Creator<T> {
  
  T create(T newDomain);
}