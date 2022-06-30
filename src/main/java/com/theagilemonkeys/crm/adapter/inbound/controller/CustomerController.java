package com.theagilemonkeys.crm.adapter.inbound.controller;

import com.theagilemonkeys.crm.adapter.inbound.dto.CustomerDto;
import com.theagilemonkeys.crm.adapter.inbound.dto.CustomerListDto;
import com.theagilemonkeys.crm.adapter.inbound.service.CustomerInboundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {
  
  private final CustomerInboundService service;
  
  @GetMapping
  public CustomerListDto findAll() {
    return service.findAll();
  }
  
  @GetMapping("/{id}")
  public CustomerDto findBy(@PathVariable("id") String id) {
    return service.findBy(id);
  }
  
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public CustomerDto create(@RequestParam("id") @NotEmpty String id,
                            @RequestParam("name") @NotEmpty String name,
                            @RequestParam("surname") @NotEmpty String surname,
                            @RequestParam("photo") @NotNull MultipartFile photo,
                            JwtAuthenticationToken jwtAuthenticationToken) throws IOException {
    var customerDto = CustomerDto.builder()
      .id(id)
      .name(name)
      .surname(surname)
      .createdBy(getUserFromToken(jwtAuthenticationToken.getToken()))
      .build();
    return service.create(customerDto, photo.getBytes());
  }
  
  @PutMapping("/{id}")
  public CustomerDto update(@PathVariable("id") String id,
                            @RequestParam("name") String name,
                            @RequestParam("surname") String surname,
                            @RequestParam("photo") MultipartFile photo,
                            JwtAuthenticationToken jwtAuthenticationToken) throws IOException {
    var customerDto = CustomerDto.builder()
      .id(id)
      .name(name)
      .surname(surname)
      .lastUpdatedBy(getUserFromToken(jwtAuthenticationToken.getToken()))
      .build();
    return service.update(customerDto, Objects.nonNull(photo) ? photo.getBytes() : null);
  }
  
  @DeleteMapping("/{id}")
  public void delete(@PathVariable("id") String id, JwtAuthenticationToken jwtAuthenticationToken) {
    service.delete(id, getUserFromToken(jwtAuthenticationToken.getToken()));
  }
  
  private String getUserFromToken(Jwt jwt) {
    return Optional.of(jwt)
      .map(given -> given.getClaimAsString("preferred_username"))
      .orElseThrow();
  }
}
