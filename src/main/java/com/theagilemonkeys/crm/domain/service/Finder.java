package com.theagilemonkeys.crm.domain.service;

import java.util.stream.Stream;

public interface Finder<T> {
  
  T findById(String id);
  
  Stream<T> findAll();
}