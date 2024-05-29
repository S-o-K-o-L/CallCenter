package com.rozhkov.callcenter.controller;

import com.rozhkov.callcenter.dto.UserRoomDto;
import com.rozhkov.callcenter.dto.UserSpecDto;
import com.rozhkov.callcenter.service.LogicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ConsultantController {
    private final LogicService logicService;

    @PostMapping("/consultant")
    public ResponseEntity<?> addUsersToConsultant(@RequestBody UserSpecDto userSpecDto) {
        return logicService.attachUserToSpecQueue(userSpecDto);
    }

    @PostMapping("/get_cons_user")
    public ResponseEntity<?> getUsers() {
        return logicService.getUsersFromConsultantQueue();
    }
}
