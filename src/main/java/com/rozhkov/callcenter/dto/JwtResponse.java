package com.rozhkov.callcenter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.authority.GrantedAuthoritiesContainer;

import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private Collection<?> role;
}