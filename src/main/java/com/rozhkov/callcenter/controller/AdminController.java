package com.rozhkov.callcenter.controller;

import com.rozhkov.callcenter.dto.JwtRequest;
import com.rozhkov.callcenter.dto.RoomDto;
import com.rozhkov.callcenter.service.LogicServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AdminController {
    private final LogicServer logicServer;

    @PostMapping("/admin")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest) {
        return authService.createAuthToken(authRequest);
    }

    @PostMapping("/get")
    public ResponseEntity<?> createAuthToken(@RequestBody RoomDto roomDto) {
        logicServer
        return ResponseEntity.ok(roomDto);
    }
}