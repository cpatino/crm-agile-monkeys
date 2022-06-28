package com.theagilemonkeys.crm.adapter.service;

public interface DomainMapper<T, E> {
  
  T toDomain(E toBeMapped);
  
  E fromDomain(T toBeMapped);
}
