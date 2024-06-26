package com.rozhkov.callcenter.controller;

import com.rozhkov.callcenter.dto.UserRoomDto;
import com.rozhkov.callcenter.listener.UserAdminListener;
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
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AdminController implements UserAdminListener {
    private LogicService logicService;
    private final ConsultantService consultantService;
    private final List<UserRoomDto> users = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArraySet<SseEmitter> emitters = new CopyOnWriteArraySet<>();
    private final CopyOnWriteArraySet<SseEmitter> emittersDelete = new CopyOnWriteArraySet<>();
    @Autowired
    public void setLogicService(LogicService logicService) {
        this.logicService = logicService;
    }

    @PostMapping("/admins/get_users")
    public ResponseEntity<?> getUsers() {
        return logicService.getAdminsQueue();
    }

    @PostMapping("/admins/delete_users")
    public ResponseEntity<?> deleteUsers(@RequestBody UUID sessionID) {
        return logicService.removeUser(sessionID);
    }

    @PostMapping("/admins/get_consultant")
    public ResponseEntity<?> getConsultant() {
        return ResponseEntity.ok(consultantService.getConsultantFromDb());
    }

    @GetMapping(value = "/admins/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamUsers() {
        SseEmitter emitter = new SseEmitter();
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitters.add(emitter);
        return emitter;
    }

    @GetMapping(value = "/admins/stream-delete", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamUsersDelete() {
        SseEmitter emitter = new SseEmitter();
        emitter.onCompletion(() -> emittersDelete.remove(emitter));
        emittersDelete.add(emitter);
        return emitter;
    }

    @Override
    public void onUserAdded(UserRoomDto user) {
        users.add(user);
        notifyClientsAdded();
    }

    @Override
    public void onUserDelete(UserRoomDto user) {
        users.remove(user);
        notifyClientsDeleted();
    }

    private void notifyClientsAdded() {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(logicService.getAdminsQueue());
            } catch (Exception e) {
                emitter.complete();
                emitters.remove(emitter);
            }
        }
    }

    private void notifyClientsDeleted() {
        for (SseEmitter emitter : emittersDelete) {
            try {
                emitter.send(ResponseEntity.ok("Deleted"));
            } catch (Exception e) {
                emitter.complete();
                emitters.remove(emitter);
            }
        }
    }
}