package com.rozhkov.callcenter.service;

import com.rozhkov.callcenter.entity.Role;
import com.rozhkov.callcenter.repository.RoleRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role getUserRole() {
        return roleRepository.findByRole("ROLE_USER").get();
    }
}
