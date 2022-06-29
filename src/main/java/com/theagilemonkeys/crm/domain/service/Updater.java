package com.theagilemonkeys.crm.domain.service;

public interface Updater<T> {
  
  T update(T existingDomain);
}