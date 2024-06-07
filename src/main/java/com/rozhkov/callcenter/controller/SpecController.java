package com.rozhkov.callcenter.controller;

import com.rozhkov.callcenter.dto.UserRoomDto;
import com.rozhkov.callcenter.dto.jwt.JwtRequest;
import com.rozhkov.callcenter.service.LogicService;
import com.rozhkov.callcenter.service.SpecService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SpecController {
    private final SpecService service;
    @PostMapping("/get_spec")
    public ResponseEntity<?> getSpec() {
        return ResponseEntity.ok(service.getAllSpec());
    }
}
