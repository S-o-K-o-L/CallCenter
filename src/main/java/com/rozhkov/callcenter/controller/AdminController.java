package com.rozhkov.callcenter.controller;

import com.rozhkov.callcenter.dto.jwt.JwtRequest;
import com.rozhkov.callcenter.dto.UserRoomDto;
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
        return ResponseEntity.ok("sdf");
    }

    @PostMapping("/connect")
    public ResponseEntity<?> addNewUser(@RequestBody UserRoomDto userRoomDto) {
        return logicServer.addNewUser(userRoomDto);
    }

    @PostMapping("/get")
    public ResponseEntity<?> getUsers() {
        return logicServer.getAdminsQueue();
    }
}