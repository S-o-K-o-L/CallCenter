package com.rozhkov.callcenter.controller;

import com.rozhkov.callcenter.dto.UserRoomDto;
import com.rozhkov.callcenter.dto.UserSpecDto;
import com.rozhkov.callcenter.dto.jwt.JwtRequest;
import com.rozhkov.callcenter.entity.Spec;
import com.rozhkov.callcenter.service.ConsultantService;
import com.rozhkov.callcenter.service.LogicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ConsultantController {
    private final LogicService logicService;
    private final ConsultantService consultantService;

    @PostMapping("/consultant")
    public ResponseEntity<?> addUsersToConsultant(@RequestBody UserSpecDto userSpecDto) {
        return logicService.attachUserToSpecQueue(userSpecDto);
    }

    @PostMapping("/consultant/get_cons_user")
    public ResponseEntity<?> getUsers() {
        return logicService.getUsersFromConsultantQueue();
    }

    @PostMapping("/consultant/del_cons_user")
    public ResponseEntity<?> delUsers(@RequestBody UserSpecDto userSpecDto) {
        return logicService.delUserFromConsultantQueue(userSpecDto);
    }
    @PostMapping("/update_spec")
    public ResponseEntity<?> updateSpec(@RequestBody UserSpecDto userSpecDto) throws InterruptedException {
        return logicService.updateSpec(userSpecDto);
    }

    @PostMapping("/consultant/get_cons")
    public ResponseEntity<?> getCons(@RequestBody JwtRequest jwtRequest) {
        return ResponseEntity.ok(consultantService.getOneConsultantFromDb(jwtRequest));
    }
}
