package com.theagilemonkeys.crm.domain.service;

public interface Deleter<T> {
  
  T delete(T existingDomain);
}