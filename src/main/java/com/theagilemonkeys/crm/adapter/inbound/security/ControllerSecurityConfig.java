package com.theagilemonkeys.crm.adapter.inbound.security;

import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@EnableWebSecurity
public class ControllerSecurityConfig {
  
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(authorize -> authorize
        .mvcMatchers("/customers/**").hasAnyRole("user", "admin")
        .mvcMatchers("/users/**").hasRole("admin")
        .anyRequest().authenticated())
      .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
    return http.build();
  }
  
  private Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
    var jwtConverter = new JwtAuthenticationConverter();
    jwtConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());
    return jwtConverter;
  }
  
  public static class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
      return ((Map<String, List<String>>) jwt.getClaim("realm_access"))
        .get("roles").stream()
        .map(roleName -> "ROLE_" + roleName)
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
    }
  }
}
