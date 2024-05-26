package com.rozhkov.callcenter.service;

import com.rozhkov.callcenter.dto.UserRoomDto;
import com.rozhkov.callcenter.entity.User;
import com.rozhkov.callcenter.exceptions.AppError;
import com.rozhkov.callcenter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
public class LogicServer {
    private final UserRepository userRepository;

    private final CopyOnWriteArrayList<UserRoomDto> connectedUsersForAdminsQueue = new CopyOnWriteArrayList<>();

    public ResponseEntity<?> addNewUser(UserRoomDto userRoomDto) {
        connectedUsersForAdminsQueue.add(userRoomDto);
        Optional<User> user = userRepository.findByUsername(userRoomDto.getUsername());
        if (user.isEmpty()) {
            userRoomDto.setUsername("user" + Math.random() * 1000);
        }
        return ResponseEntity.ok(userRoomDto);
    }

    public ResponseEntity<?> getAdminsQueue() {
        connectedUsersForAdminsQueue.add(new UserRoomDto());
        return ResponseEntity.ok(connectedUsersForAdminsQueue);
    }

    public ResponseEntity<?> attachUserToSpecQueue(UserRoomDto userRoomDto) {
        if (connectedUsersForAdminsQueue.contains(userRoomDto)) {

            return ResponseEntity.ok(userRoomDto);
        } else {
            return new ResponseEntity<>(new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Not such user"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
