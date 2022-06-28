package com.theagilemonkeys.crm.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class ObjectNotFoundException extends RuntimeException {
  
  public ObjectNotFoundException(String id) {
    super("The object could not be found using the id: " + id);
  }
}
