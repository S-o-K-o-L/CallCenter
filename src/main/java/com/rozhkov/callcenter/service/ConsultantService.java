package com.rozhkov.callcenter.service;

import com.rozhkov.callcenter.entity.Role;
import com.rozhkov.callcenter.entity.User;
import com.rozhkov.callcenter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultantService {
    private final UserRepository userRepository;

    @Transactional
    public List<User> getConsultantFromDb() {
        return userRepository.findAllByRole().orElse(new ArrayList<>());
    }
}
