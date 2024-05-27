package com.rozhkov.callcenter.controller;

import com.rozhkov.callcenter.dto.jwt.JwtRequest;
import com.rozhkov.callcenter.dto.UserRoomDto;
import com.rozhkov.callcenter.listener.UserChangeListener;
import com.rozhkov.callcenter.service.LogicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AdminController implements UserChangeListener {
    private LogicService logicService;
    private final List<UserRoomDto> users = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArraySet<SseEmitter> emitters = new CopyOnWriteArraySet<>();
    @Autowired
    public void setLogicService(LogicService logicService) {
        this.logicService = logicService;
    }

    @PostMapping("/admin")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest) {
        return ResponseEntity.ok("sdf");
    }

    @PostMapping("/get_users")
    public ResponseEntity<?> getUsers() {
        return logicService.getAdminsQueue();
    }

    @PostMapping("/delete_users")
    public ResponseEntity<?> deleteUsers() {
        return logicService.getAdminsQueue();
    }

    @GetMapping(value = "/admins/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamUsers() {
        SseEmitter emitter = new SseEmitter();
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitters.add(emitter);
        return emitter;
    }

    @Override
    public void onUserAdded(UserRoomDto user) {
        users.add(user);
        notifyClients();
    }

    private void notifyClients() {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(logicService.getAdminsQueue());
            } catch (Exception e) {
                emitter.complete();
                emitters.remove(emitter);
            }
        }
    }


}