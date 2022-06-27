package com.theagilemonkeys.crm.adapter.inbound.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {
  
  @GetMapping
  public List<Object> findAll() {
    throw new UnsupportedOperationException();
  }
  
  @GetMapping("/{id}")
  public Object findBy(@PathVariable("id") String id) {
    throw new UnsupportedOperationException();
  }
  
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public void create(@RequestBody Object customer) {
    throw new UnsupportedOperationException();
  }
  
  @PutMapping("/{id}")
  public Object update(@PathVariable("id") String id, @RequestBody Object customer) {
    throw new UnsupportedOperationException();
  }
  
  @DeleteMapping("/{id}")
  public void delete(@PathVariable("id") String id) {
    throw new UnsupportedOperationException();
  }
}
