package com.rozhkov.callcenter.service;

import com.rozhkov.callcenter.entity.Role;
import com.rozhkov.callcenter.entity.Spec;
import com.rozhkov.callcenter.repository.RoleRepository;
import com.rozhkov.callcenter.repository.SpecRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpecService {
    private final SpecRepository specRepository;

    public List<String> getAllSpec() {
        return specRepository
                .findAll()
                .stream()
                .map(Spec::getSpec)
                .collect(Collectors.toList());
    }
}