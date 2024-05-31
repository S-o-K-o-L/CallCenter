package com.rozhkov.callcenter.controller;

import com.rozhkov.callcenter.dto.UserSpecDto;
import com.rozhkov.callcenter.dto.jwt.JwtRequest;
import com.rozhkov.callcenter.listener.UserConsultantListener;
import com.rozhkov.callcenter.service.ConsultantService;
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
public class ConsultantController implements UserConsultantListener {
    private LogicService logicService;
    private final ConsultantService consultantService;
    private final List<UserSpecDto> users = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArraySet<SseEmitter> emitters = new CopyOnWriteArraySet<>();
    private final CopyOnWriteArraySet<SseEmitter> emittersConsultant = new CopyOnWriteArraySet<>();
    private JwtRequest  jwtRequest;
    @Autowired
    public void setLogicService(LogicService logicService) {
        this.logicService = logicService;
    }

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
        this.jwtRequest = new JwtRequest();
        jwtRequest.setUsername(userSpecDto.getUsername());
        return logicService.updateSpecListener(userSpecDto);
    }

    @PostMapping("/consultant/get_cons")
    public ResponseEntity<?> getCons(@RequestBody JwtRequest jwtRequest) {
        return ResponseEntity.ok(consultantService.getOneConsultantFromDb(jwtRequest));
    }

    @GetMapping(value = "/consultant/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamUsers() {
        SseEmitter emitter = new SseEmitter();
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitters.add(emitter);
        return emitter;
    }

    @GetMapping(value = "/consultant/stream-spec", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamConsultant() {
        SseEmitter emitter = new SseEmitter();
        emitter.onCompletion(() -> emittersConsultant.remove(emitter));
        emittersConsultant.add(emitter);
        return emitter;
    }

    @Override
    public void onUserConsultantChange(UserSpecDto user) {
        users.remove(user);
        notifyClientsUserChange();
    }

    private void notifyClientsUserChange() {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(logicService.getUsersFromConsultantQueue());
            } catch (Exception e) {
                emitter.complete();
                emitters.remove(emitter);
            }
        }
    }

    @Override
    public void onConsultantSpecChange(UserSpecDto user) {
        users.remove(user);
        notifyClientsConsultantSpecChange();
    }

    private void notifyClientsConsultantSpecChange() {
        for (SseEmitter emitter : emittersConsultant) {
            try {
                emitter.send(consultantService.getOneConsultantFromDb(jwtRequest));
            } catch (Exception e) {
                emitter.complete();
                emittersConsultant.remove(emitter);
            }
        }
    }
}
