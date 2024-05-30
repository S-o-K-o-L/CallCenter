package com.rozhkov.callcenter.service;

import com.rozhkov.callcenter.dto.UserSpecDto;
import com.rozhkov.callcenter.dto.jwt.JwtRequest;
import com.rozhkov.callcenter.entity.Role;
import com.rozhkov.callcenter.entity.Spec;
import com.rozhkov.callcenter.entity.User;
import com.rozhkov.callcenter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsultantService {
    private final UserRepository userRepository;

    @Transactional
    public List<User> getConsultantFromDb() {
        return userRepository.findAllByRole().orElse(new ArrayList<>());
    }

    public UserSpecDto getOneConsultantFromDb(JwtRequest jwtRequest) {
        User user = userRepository.findByUsername(jwtRequest.getUsername()).get();
        UserSpecDto userSpecDto = new UserSpecDto();
        userSpecDto.setSpecs(user.getSpecs()
                .stream()
                .map(Spec::getSpec)
                .collect(Collectors.toList()));
        userSpecDto.setUsername(jwtRequest.getUsername());
        return userSpecDto;
    }
}
