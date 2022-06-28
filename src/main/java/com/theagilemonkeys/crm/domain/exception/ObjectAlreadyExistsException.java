package com.theagilemonkeys.crm.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class ObjectAlreadyExistsException extends RuntimeException {
  
  public ObjectAlreadyExistsException(String id) {
    super("An object already exists using the id: " + id);
  }
}
